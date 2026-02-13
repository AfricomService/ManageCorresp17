package com.africom.correspmanage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

/**
 * Util service to create / update ACL entries safely.
 */
@Service
@Transactional
public class AclUtilService {

    private final Logger log = LoggerFactory.getLogger(AclUtilService.class);

    private final JdbcMutableAclService aclService;
    private final JdbcTemplate jdbcTemplate;

    public AclUtilService(JdbcMutableAclService aclService, JdbcTemplate jdbcTemplate) {
        this.aclService = aclService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createAcl(Object domainObject) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        log.debug("createAcl for {}", domainObject);
        try {
            aclService.createAcl(oid);
        } catch (AlreadyExistsException ignored) {
            // already created
            log.debug("ACL already exists for {}", oid);
        }
    }

    public void ensureAclExists(Object domainObject) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            log.debug("ACL not found for {}, creating", oid);
            aclService.createAcl(oid);
        }
    }

    /**
     * Simple addPermission that always inserts ACE (may throw on duplicate sid insertion).
     * Use addPermissionIfNotExists when you want to avoid duplicates and race conditions.
     */
    public void addPermission(Object domainObject, String username, Permission permission) {
        if (username == null || username.isBlank()) return;
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
        }
        // normalize login
        String normalized = username.trim().toLowerCase();
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(normalized), true);
        aclService.updateAcl(acl);
    }

    public void addPermissionForAuthority(Object domainObject, String authoritySid, Permission permission) {
        if (authoritySid == null || authoritySid.isBlank()) return;
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
        }
        acl.insertAce(acl.getEntries().size(), permission, new GrantedAuthoritySid(authoritySid.trim()), true);
        aclService.updateAcl(acl);
    }

    public void deletePermission(Object domainObject, String username, Permission permission) {
        if (username == null || username.isBlank()) return;
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
            Sid sid = new PrincipalSid(username.trim().toLowerCase());
            for (int i = 0; i < acl.getEntries().size(); i++) {
                AccessControlEntry entry = acl.getEntries().get(i);
                if (entry.getSid().equals(sid) && entry.getPermission().equals(permission)) {
                    acl.deleteAce(i);
                    aclService.updateAcl(acl);
                    break;
                }
            }
        } catch (NotFoundException nfe) {
            // nothing to remove
        }
    }

    @Transactional
    public void deleteAllPermissions(Object domainObject) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            aclService.deleteAcl(oid, false);
        } catch (NotFoundException ex) {
            // ACL does not exist → nothing to do
            log.debug("No ACL to delete for {}", oid);
        }
    }

    /**
     * Add ACE if not present. This method:
     *  - checks existing ACEs,
     *  - checks existence of sid in acl_sid table (to avoid DuplicateKeyException),
     *  - on race DuplicateKeyException it will reload and retry safely.
     */
    public void addPermissionIfNotExists(Object domainObject, Sid sid, Permission permission) {
        if (sid == null || permission == null) return;

        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
        }

        // 1) If ACE already exists → nothing to do
        boolean exists = acl.getEntries().stream().anyMatch(e ->
            e.getSid().equals(sid) && e.getPermission().equals(permission)
        );
        if (exists) {
            log.debug("ACE already exists for {} / {} on {}", sid, permission, oid);
            return;
        }

        // 2) Extract sid string value to check acl_sid table
        String sidValue = sidValueFromSid(sid);

        try {
            if (sidValue != null && sidExistsInDb(sidValue)) {
                // sid already recorded in acl_sid: safe to insert ACE
                acl.insertAce(acl.getEntries().size(), permission, sid, true);
                aclService.updateAcl(acl);
                log.debug("Inserted ACE for existing sid {} on {}", sidValue, oid);
                return;
            }

            // 3) sid not present in acl_sid (or unknown) -> try to insert ACE (may create sid entry)
            acl.insertAce(acl.getEntries().size(), permission, sid, true);
            aclService.updateAcl(acl);
            log.debug("Inserted ACE (and possibly created sid) {} on {}", sidValue, oid);

        } catch (DuplicateKeyException dke) {
            // Race: another thread/process inserted the sid concurrently.
            log.info("DuplicateKeyException while inserting sid '{}'. Reloading ACL and retrying ACE insertion.", sidValue);
            try {
                MutableAcl refreshed = (MutableAcl) aclService.readAclById(oid);
                boolean nowExists = refreshed.getEntries().stream().anyMatch(e ->
                    e.getSid().equals(sid) && e.getPermission().equals(permission)
                );
                if (!nowExists) {
                    refreshed.insertAce(refreshed.getEntries().size(), permission, sid, true);
                    aclService.updateAcl(refreshed);
                    log.debug("Inserted ACE after concurrent sid creation for {} on {}", sidValue, oid);
                } else {
                    log.debug("ACE already present after refresh for {} on {}", sidValue, oid);
                }
            } catch (Exception ex) {
                log.warn("Impossible de finaliser l'ajout d'ACE après DuplicateKey pour sid '{}': {}", sidValue, ex.getMessage());
            }
        } catch (DataAccessException dae) {
            log.warn("Erreur lors de updateAcl pour sid '{}': {}", sidValue, dae.getMessage());
        }
    }

    // -------------------------
    // Helper methods
    // -------------------------

    private boolean sidExistsInDb(String sidValue) {
        if (sidValue == null || sidValue.isBlank()) return false;
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM acl_sid WHERE sid = ?", Integer.class, sidValue);
            return count != null && count > 0;
        } catch (DataAccessException ex) {
            // si erreur DB, on log et renvoie false (on laissera la tentative d'insertion gérer DuplicateKey si nécessaire)
            log.warn("Impossible de vérifier existence sid '{}': {}", sidValue, ex.getMessage());
            return false;
        }
    }

    private String sidValueFromSid(Sid sid) {
        if (sid instanceof PrincipalSid) {
            String p = ((PrincipalSid) sid).getPrincipal();
            return p == null ? null : p.trim().toLowerCase();
        } else if (sid instanceof GrantedAuthoritySid) {
            String a = ((GrantedAuthoritySid) sid).getGrantedAuthority();
            return a == null ? null : a.trim();
        }
        return null;
    }

    private Serializable getId(Object domainObject) {
        try {
            java.lang.reflect.Method m = domainObject.getClass().getMethod("getId");
            return (Serializable) m.invoke(domainObject);
        } catch (Exception e) {
            throw new IllegalStateException("Domain object must have getId method returning Serializable", e);
        }
    }
}
