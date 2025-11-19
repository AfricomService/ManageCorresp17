package com.africom.correspmanage.config;

import java.io.Serializable;
import java.util.Collections;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AclPermissionEvaluator implements PermissionEvaluator {

    private final AclService aclService;

    public AclPermissionEvaluator(AclService aclService) {
        this.aclService = aclService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject == null) {
            return false;
        }
        // Not commonly used for ACLs; prefer the id/type form below
        try {
            ObjectIdentity oid = new org.springframework.security.acls.domain.ObjectIdentityImpl(targetDomainObject);
            Acl acl = aclService.readAclById(oid);
            Sid sid = new PrincipalSid(authentication);
            Permission p = convertToPermission(permission);
            return acl.isGranted(Collections.singletonList(p), Collections.singletonList(sid), false);
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        try {
            org.springframework.security.acls.domain.ObjectIdentityImpl oid = new org.springframework.security.acls.domain.ObjectIdentityImpl(
                targetType,
                targetId
            );
            Acl acl = aclService.readAclById(oid);
            Sid sid = new PrincipalSid(authentication);
            Permission p = convertToPermission(permission);
            return acl.isGranted(Collections.singletonList(p), Collections.singletonList(sid), false);
        } catch (NotFoundException e) {
            return false;
        }
    }

    private Permission convertToPermission(Object permission) {
        if (permission instanceof Permission) {
            return (Permission) permission;
        }
        // Support string names like "READ", "WRITE", "DELETE"
        if (permission instanceof String) {
            switch (((String) permission).toUpperCase()) {
                case "READ":
                    return BasePermission.READ;
                case "WRITE":
                    return BasePermission.WRITE;
                case "CREATE":
                    return BasePermission.CREATE;
                case "DELETE":
                    return BasePermission.DELETE;
                case "ADMINISTRATION":
                    return BasePermission.ADMINISTRATION;
                default:
                    return BasePermission.READ; // fallback
            }
        }
        return BasePermission.READ;
    }
}
