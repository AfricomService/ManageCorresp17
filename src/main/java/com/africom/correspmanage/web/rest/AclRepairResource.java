package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.service.AclRepairService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint ponctuel de réparation des ACL des anciens CORRESP.
 * À supprimer une fois la réparation effectuée.
 */
@RestController
@RequestMapping("/api/admin/acl-repair")
public class AclRepairResource {

    private final Logger log = LoggerFactory.getLogger(AclRepairResource.class);

    private final AclRepairService aclRepairService;

    public AclRepairResource(AclRepairService aclRepairService) {
        this.aclRepairService = aclRepairService;
    }

    @PostMapping("/corresps")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> repairCorrespsAcl(
        @RequestParam(name = "correspIds", required = false) List<Long> correspIds,
        @RequestParam(name = "fromId", required = false) Long fromId,
        @RequestParam(name = "toId", required = false) Long toId,
        @RequestParam(name = "dryRun", defaultValue = "true") boolean dryRun
    ) {
        boolean explicit = correspIds != null && !correspIds.isEmpty();
        List<Long> ids = explicit ? correspIds : aclRepairService.findCorrespIds(fromId, toId);
        log.info("ACL repair démarré | dryRun={} | explicit={} | {} corresp(s)", dryRun, explicit, ids.size());

        int totalMissing = 0;
        int totalAdded = 0;
        int totalErrors = 0;
        int correspsConcerned = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (Long id : ids) {
            Map<String, Object> res;
            try {
                res = aclRepairService.repairCorresp(id, dryRun);
            } catch (Exception e) {
                log.warn("ACL repair échoué pour corresp {} : {}", id, e.getMessage());
                res = new LinkedHashMap<>();
                res.put("correspId", id);
                res.put("aclMissing", 0);
                res.put("aclAdded", 0);
                res.put("errors", 1);
                res.put("details", Collections.singletonList("ERREUR : " + e.getMessage()));
            }

            int missing = ((Number) res.get("aclMissing")).intValue();
            int added = ((Number) res.get("aclAdded")).intValue();
            int errors = ((Number) res.get("errors")).intValue();

            totalMissing += missing;
            totalAdded += added;
            totalErrors += errors;

            if (missing > 0 || errors > 0) {
                correspsConcerned++;
            }
            // Corresp ciblés explicitement : toujours affichés ; sinon uniquement ceux qui posent problème
            if (explicit || missing > 0 || errors > 0) {
                details.add(res);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("dryRun", dryRun);
        response.put("correspsScanned", ids.size());
        response.put("correspsConcerned", correspsConcerned);
        response.put("aclMissingTotal", totalMissing);
        response.put("aclAddedTotal", totalAdded);
        response.put("errorsTotal", totalErrors);
        response.put("details", details);

        log.info("ACL repair terminé | scanned={} | concerned={} | missing={} | added={} | errors={}",
            ids.size(), correspsConcerned, totalMissing, totalAdded, totalErrors);

        return ResponseEntity.ok(response);
    }
}
