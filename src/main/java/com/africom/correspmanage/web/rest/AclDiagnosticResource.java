package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.security.AuthoritiesConstants;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.AclDiagnosticService;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de diagnostic de la logique ACL (lecture seule).
 */
@RestController
@RequestMapping("/api")
public class AclDiagnosticResource {

    private final AclDiagnosticService aclDiagnosticService;

    public AclDiagnosticResource(AclDiagnosticService aclDiagnosticService) {
        this.aclDiagnosticService = aclDiagnosticService;
    }

    /** Diagnostic de l'utilisateur connecté (uniquement ses propres données). */
    @GetMapping("/acl-diagnostic/me")
    public ResponseEntity<Map<String, Object>> diagnoseMe(@RequestParam(required = false) Long correspId) {
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("Utilisateur non authentifié"));

        Map<String, Object> result = aclDiagnosticService.diagnose(login, correspId);
        result.put("tokenAuthorities", currentAuthorities());
        return ResponseEntity.ok(result);
    }

    /**
     * SIDs de l'utilisateur connecté (léger, appelé à chaque connexion).
     * Ouvert à tout utilisateur authentifié, le login vient du token (jamais d'un paramètre).
     */
    @GetMapping("/acl-diagnostic/me/sids")
    public ResponseEntity<Map<String, Object>> mySids() {
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("Utilisateur non authentifié"));

        Map<String, Object> result = aclDiagnosticService.mySids(login);
        result.put("tokenAuthorities", currentAuthorities());
        return ResponseEntity.ok(result);
    }

    /** Diagnostic d'un utilisateur quelconque : ADMIN uniquement (enforcé backend). */
    @GetMapping("/admin/acl-diagnostic")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Map<String, Object>> diagnoseUser(
        @RequestParam String login,
        @RequestParam(required = false) Long correspId
    ) {
        return ResponseEntity.ok(aclDiagnosticService.diagnose(login, correspId));
    }

    private static java.util.List<String> currentAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return java.util.Collections.emptyList();
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().collect(Collectors.toList());
    }
}
