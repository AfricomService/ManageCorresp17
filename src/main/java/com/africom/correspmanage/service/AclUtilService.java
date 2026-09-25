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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.africom.correspmanage.security.SecurityUtils;

import java.io.Serializable;
import java.util.Collections;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAcl(Object domainObject) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        log.debug("createAcl for {}", domainObject);
        try {
            MutableAcl acl = aclService.createAcl(oid);
            fixOwnerToLogin(acl);
        } catch (AlreadyExistsException ignored) {
            // already created
            log.debug("ACL already exists for {}", oid);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureAclExists(Object domainObject) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            log.debug("ACL not found for {}, creating", oid);
            MutableAcl acl = aclService.createAcl(oid);
            fixOwnerToLogin(acl);
        }
    }

    /**
     * Simple addPermission that always inserts ACE (may throw on duplicate sid insertion).
     * Use addPermissionIfNotExists when you want to avoid duplicates and race conditions.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addPermission(Object domainObject, String username, Permission permission) {
        if (username == null || username.isBlank()) return;
        String normalized = username.trim().toLowerCase();
        // Réconcilier avec la base pour avoir le bon type de Sid
        Sid sid = reconcileSidWithDb(new PrincipalSid(normalized));

        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
            fixOwnerToLogin(acl);
        }
// ── NOUVEAU ──
        insertAceAsOwner(acl, acl.getEntries().size(), permission, sid);
        aclService.updateAcl(acl);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addPermissionForAuthority(Object domainObject, String authoritySid, Permission permission) {
        if (authoritySid == null || authoritySid.isBlank()) return;
        // NE PAS mettre en minuscules les départements - garder la casse originale
        String normalized = authoritySid.trim();
        // Réconcilier avec la base pour avoir le bon type de Sid
        Sid sid = reconcileSidWithDb(new GrantedAuthoritySid(normalized));

        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
            fixOwnerToLogin(acl);
        }
// ── NOUVEAU ──
        insertAceAsOwner(acl, acl.getEntries().size(), permission, sid);
        aclService.updateAcl(acl);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletePermission(Object domainObject, String username, Permission permission) {
        if (username == null || username.isBlank()) return;
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
            String normalized = username.trim().toLowerCase();
            // Réconcilier avec la base pour avoir le bon type de Sid
            Sid sid = reconcileSidWithDb(new PrincipalSid(normalized));
            for (int i = 0; i < acl.getEntries().size(); i++) {
                AccessControlEntry entry = acl.getEntries().get(i);
                if (entry.getSid().equals(sid) && entry.getPermission().equals(permission)) {
// ── NOUVEAU ──
                    deleteAceAsOwner(acl, i);
                    aclService.updateAcl(acl);
                    break;
                }
            }
        } catch (NotFoundException nfe) {
            // nothing to remove
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addPermissionIfNotExists(Object domainObject, Sid sid, Permission permission) {
        if (sid == null || permission == null) return;

        Sid effectiveSid = reconcileSidWithDb(sid);
        String sidValue = sidValueFromSid(effectiveSid);

        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
            fixOwnerToLogin(acl);
        }

        boolean exists = acl.getEntries().stream().anyMatch(e -> e.getSid().equals(effectiveSid) && e.getPermission().equals(permission));
        if (exists) {
            log.debug("ACE already exists for {} / {} on {}", effectiveSid, permission, oid);
            return;
        }

        ensureSidExists(effectiveSid, sidValue);

        try {
// ── NOUVEAU ──
            insertAceAsOwner(acl, acl.getEntries().size(), permission, effectiveSid);
            aclService.updateAcl(acl);
            log.debug("Inserted ACE (sid={}) on {}", sidValue, oid);
        } catch (DuplicateKeyException dke) {
            log.info("DuplicateKeyException while inserting sid '{}'. Reloading ACL and aborting current attempt.", sidValue);
        } catch (DataAccessException dae) {
            log.warn("Erreur lors de updateAcl pour sid '{}': {}", sidValue, dae.getMessage());
        }
    }

    // -------------------------
    // Modification d'ACL sous l'identité du propriétaire
    // -------------------------

    /**
     * Spring n'autorise insertAce / deleteAce qu'au propriétaire de l'ACL (celui qui l'a créée),
     * à une autorité admin configurée, ou à un porteur de la permission ADMINISTRATION.
     * Le propriétaire est ici le BOC, alors que les modifications suivantes sont faites par
     * d'autres utilisateurs (Achats, Comptabilité...). On exécute donc la modification
     * sous l'identité du propriétaire, uniquement le temps de l'appel.
     */
    private void insertAceAsOwner(MutableAcl acl, int index, Permission permission, Sid sid) {
        runAsAclOwner(acl, () -> acl.insertAce(index, permission, sid, true));
    }

    private void deleteAceAsOwner(MutableAcl acl, int index) {
        runAsAclOwner(acl, () -> acl.deleteAce(index));
    }

    private void runAsAclOwner(MutableAcl acl, Runnable action) {
        Sid owner = acl.getOwner();
        if (!(owner instanceof PrincipalSid)) {
            action.run();
            return;
        }
        SecurityContext original = SecurityContextHolder.getContext();
        try {
            SecurityContext ownerContext = SecurityContextHolder.createEmptyContext();
            ownerContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    ((PrincipalSid) owner).getPrincipal(),
                    "N/A",
                    AuthorityUtils.NO_AUTHORITIES
                )
            );
            SecurityContextHolder.setContext(ownerContext);
            action.run();
        } finally {
            SecurityContextHolder.setContext(original);
        }
    }

        /**
     * Force le propriétaire de l'ACL au login (matricule) de l'utilisateur courant,
     * au lieu de l'UUID Keycloak assigné par défaut par Spring (authentication.getName()
     * renvoie le claim "sub" pour un JwtAuthenticationToken). Rétablit la logique
     * d'avant la migration Keycloak. Appelé juste après aclService.createAcl(oid).
     */
    private void fixOwnerToLogin(MutableAcl acl) {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null || login.isBlank()) return;
        String normalized = login.trim().toLowerCase();
        Sid ownerSid = new PrincipalSid(normalized);
        ensureSidExists(ownerSid, normalized);
        acl.setOwner(ownerSid);
        aclService.updateAcl(acl);
    }

    private void ensureSidExists(Sid sid, String sidValue) {
        if (sidValue == null || sidValue.isBlank()) return;
        boolean principalFlag = sid instanceof PrincipalSid;
        try {
            jdbcTemplate.update(
                "insert into acl_sid (principal, sid) values (?, ?) on conflict (sid) do nothing",
                principalFlag,
                sidValue
            );
        } catch (DataAccessException ex) {
            log.warn("Impossible de préparer le sid '{}' dans acl_sid: {}", sidValue, ex.getMessage());
        }
    }

    private Sid reconcileSidWithDb(Sid sid) {
        String sidValue = sidValueFromSid(sid);
        if (sidValue == null || sidValue.isBlank()) return sid;
        try {
            Boolean principalFlag = jdbcTemplate.queryForObject("select principal from acl_sid where sid = ?", Boolean.class, sidValue);
            if (principalFlag == null) return sid;
            return principalFlag ? new PrincipalSid(sidValue) : new GrantedAuthoritySid(sidValue);
        } catch (DataAccessException ex) {
            log.debug("Impossible de lire acl_sid pour '{}': {}", sidValue, ex.getMessage());
            return sid;
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

    /**
     * Supprime une permission (ACE) pour un GrantedAuthoritySid si elle existe.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletePermissionForAuthority(Object domainObject, String authoritySid, Permission permission) {
        if (authoritySid == null || authoritySid.isBlank()) return;
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
            // Réconcilier avec la base pour avoir le bon type de Sid
            Sid sid = reconcileSidWithDb(new GrantedAuthoritySid(authoritySid.trim()));
            for (int i = 0; i < acl.getEntries().size(); i++) {
                AccessControlEntry entry = acl.getEntries().get(i);
                if (entry.getSid().equals(sid) && entry.getPermission().equals(permission)) {
// ── NOUVEAU ──
                    deleteAceAsOwner(acl, i);
                    aclService.updateAcl(acl);
                    break;
                }
            }
        } catch (NotFoundException nfe) {
            // nothing to remove
        } catch (DataAccessException dae) {
            log.warn("Erreur lors de suppression ACE authority '{}': {}", authoritySid, dae.getMessage());
        }
    }

    /**
     * Supprime une permission pour un Sid donné (général).
     * Utile si on veut supprimer aussi bien PrincipalSid que GrantedAuthoritySid.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletePermissionBySid(Object domainObject, Sid sid, Permission permission) {
        if (sid == null) return;
        // Réconcilier avec la base pour avoir le bon type de Sid
        Sid effectiveSid = reconcileSidWithDb(sid);
        String sidValue = sidValueFromSid(effectiveSid);
        if (sidValue == null || sidValue.isBlank()) return;
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
            for (int i = 0; i < acl.getEntries().size(); i++) {
                AccessControlEntry entry = acl.getEntries().get(i);
                if (entry.getSid().equals(effectiveSid) && entry.getPermission().equals(permission)) {
// ── NOUVEAU ──
                    deleteAceAsOwner(acl, i);
                    aclService.updateAcl(acl);
                    break;
                }
            }
        } catch (NotFoundException nfe) {
            // nothing to remove
        } catch (DataAccessException dae) {
            log.warn("Erreur lors de suppression ACE pour sid '{}': {}", sidValue, dae.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean hasPermissionForSid(Object domainObject, Sid sid, Permission permission) {
        if (domainObject == null || sid == null || permission == null) return false;

        // Réconcilier le Sid avec ce qui est réellement en base
        Sid effectiveSid = reconcileSidWithDb(sid);

        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            Acl acl = aclService.readAclById(oid);
            try {
                // isGranted peut lancer org.springframework.security.access.AccessDeniedException si non autorisé
                return acl.isGranted(Collections.singletonList(permission), Collections.singletonList(effectiveSid), false);
            } catch (org.springframework.security.acls.model.NotFoundException nfe) {
                // ACE/permission not found
                return false;
            } catch (org.springframework.security.access.AccessDeniedException ade) {
                // access denied -> user doesn't have the permission
                return false;
            }
        } catch (org.springframework.security.acls.model.NotFoundException nfe) {
            // no ACL at all for this object
            return false;
        } catch (DataAccessException dae) {
            log.warn("Erreur DB lors lecture ACL pour {}: {}", oid, dae.getMessage());
            return false;
        }
    }


}
