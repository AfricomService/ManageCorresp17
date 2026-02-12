package com.africom.correspmanage.service;

import com.africom.correspmanage.security.jwt.TokenProvider;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.acls.domain.GrantedAuthoritySid; // ajouter import


@Service
@Transactional
public class AclUtilService {

    private final Logger log = LoggerFactory.getLogger(AclUtilService.class);

    private final JdbcMutableAclService aclService;

    public AclUtilService(JdbcMutableAclService aclService) {
        this.aclService = aclService;
    }

    public void createAcl(Object domainObject) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        log.debug("inside createAcl {}", domainObject);
        try {
            aclService.createAcl(oid);
        } catch (AlreadyExistsException ignored) {
            // already created
        }
    }

    public void addPermission(Object domainObject, String username, Permission permission) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException nfe) {
            acl = aclService.createAcl(oid);
        }
        // add ACE at the end
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(username), true);
        aclService.updateAcl(acl);
    }

    public void deletePermission(Object domainObject, String username, Permission permission) {
        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
        try {
            MutableAcl acl = (MutableAcl) aclService.readAclById(oid);
            Sid sid = new PrincipalSid(username);
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
        }
    }

    private Serializable getId(Object domainObject) {
        try {
            java.lang.reflect.Method m = domainObject.getClass().getMethod("getId");
            return (Serializable) m.invoke(domainObject);
        } catch (Exception e) {
            throw new IllegalStateException("Domain object must have getId method returning Serializable", e);
        }
    }
//
//    // Ajoute en bas de la classe AclUtilService (ou adapte si tu veux autre place)
//    public void addPermissions(Object domainObject, Iterable<String> sids, Permission permission) {
//        if (sids == null) return;
//        for (String sid : sids) {
//            if (sid == null || sid.isBlank()) continue;
//            try {
//                addPermission(domainObject, sid, permission);
//            } catch (Exception e) {
//                log.warn("Impossible d'ajouter la permission {} pour sid {} sur {} : {}", permission, sid, domainObject, e.getMessage());
//            }
//        }
//    }
//
//    /**
//     * Convenience: add both READ and WRITE for a collection of sids
//     */
//    public void addReadWritePermissions(Object domainObject, Iterable<String> sids) {
//        addPermissions(domainObject, sids, BasePermission.READ);
//        addPermissions(domainObject, sids, BasePermission.WRITE);
//    }
//
//    // Ajoute un ACE pour un user (PrincipalSid)
//    public void addPermissionForUser(Object domainObject, String username, Permission permission) {
//        if (username == null || username.isBlank()) return;
//        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
//        MutableAcl acl;
//        try {
//            acl = (MutableAcl) aclService.readAclById(oid);
//        } catch (NotFoundException nfe) {
//            acl = aclService.createAcl(oid);
//        }
//        String normalized = username.toLowerCase(); // normaliser
//        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(normalized), true);
//        aclService.updateAcl(acl);
//    }
//
//    // Ajoute un ACE pour une authority (dept) en utilisant GrantedAuthoritySid
//    public void addPermissionForAuthority(Object domainObject, String authoritySid, Permission permission) {
//        if (authoritySid == null || authoritySid.isBlank()) return;
//        ObjectIdentity oid = new ObjectIdentityImpl(domainObject.getClass(), getId(domainObject));
//        MutableAcl acl;
//        try {
//            acl = (MutableAcl) aclService.readAclById(oid);
//        } catch (NotFoundException nfe) {
//            acl = aclService.createAcl(oid);
//        }
//        acl.insertAce(acl.getEntries().size(), permission, new GrantedAuthoritySid(authoritySid), true);
//        aclService.updateAcl(acl);
//    }
//
//    // Helpers sur collections
//    public void addReadWriteForUsers(Object domainObject, Iterable<String> usernames) {
//        if (usernames == null) return;
//        for (String u : usernames) {
//            try { addPermissionForUser(domainObject, u, BasePermission.READ); } catch (Exception ignored) {}
//            try { addPermissionForUser(domainObject, u, BasePermission.WRITE); } catch (Exception ignored) {}
//        }
//    }
//
//    public void addReadWriteForAuthorities(Object domainObject, Iterable<String> authorities) {
//        if (authorities == null) return;
//        for (String a : authorities) {
//            try { addPermissionForAuthority(domainObject, a, BasePermission.READ); } catch (Exception ignored) {}
//            try { addPermissionForAuthority(domainObject, a, BasePermission.WRITE); } catch (Exception ignored) {}
//        }
//    }
//
//    public void addReadForAuthorities(Object domainObject, Iterable<String> authorities) {
//        if (authorities == null) return;
//        for (String a : authorities) {
//            try { addPermissionForAuthority(domainObject, a, BasePermission.READ); } catch (Exception ignored) {}
//        }
//    }

}
