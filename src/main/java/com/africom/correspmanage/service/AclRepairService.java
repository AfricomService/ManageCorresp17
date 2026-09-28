package com.africom.correspmanage.service;

import com.africom.correspmanage.client.CorrespGatewayRestClient;
import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.client.UserFeignDTO;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Réparation ponctuelle des ACL des CORRESP créés avant l'ajout de la notion d'Owner.
 * Rejoue la logique de changeTaskStatus (IN_PROGRESS / CLOSED) en mode additif uniquement.
 */
@Service
public class AclRepairService {

    private final Logger log = LoggerFactory.getLogger(AclRepairService.class);

    private final CorrespRepository correspRepository;
    private final AclUtilService aclUtilService;
    private final ResponsableTacheService responsableTacheService;
    private final OrgaCareRestClient orgaCareRestClient;
    private final CorrespGatewayRestClient correspGatewayRestClient;

    public AclRepairService(
        CorrespRepository correspRepository,
        AclUtilService aclUtilService,
        ResponsableTacheService responsableTacheService,
        OrgaCareRestClient orgaCareRestClient,
        CorrespGatewayRestClient correspGatewayRestClient
    ) {
        this.correspRepository = correspRepository;
        this.aclUtilService = aclUtilService;
        this.responsableTacheService = responsableTacheService;
        this.orgaCareRestClient = orgaCareRestClient;
        this.correspGatewayRestClient = correspGatewayRestClient;
    }

    /** IDs des CORRESP triés, filtrables par plage [fromId, toId]. */
    public List<Long> findCorrespIds(Long fromId, Long toId) {
        List<Long> ids = new ArrayList<>();
        int page = 0;
        Page<Corresp> current;
        do {
            current = correspRepository.findAll(PageRequest.of(page++, 500, Sort.by("id")));
            for (Corresp c : current.getContent()) {
                Long id = c.getId();
                if (id == null) continue;
                if (fromId != null && id < fromId) continue;
                if (toId != null && id > toId) continue;
                ids.add(id);
            }
        } while (current.hasNext());
        return ids;
    }

    /**
     * Répare (ou simule la réparation) des ACL d'un CORRESP et de ses tâches.
     * Transaction englobante en lecture seule : chaque écriture ACL se fait dans sa
     * propre transaction (REQUIRES_NEW dans AclUtilService).
     */
    @Transactional(readOnly = true)
    public Map<String, Object> repairCorresp(Long correspId, boolean dryRun) {
        Corresp corresp = correspRepository
            .findById(correspId)
            .orElseThrow(() -> new EntityNotFoundException("Corresp not found with id " + correspId));

        Report r = new Report(dryRun);
        Map<String, String> deptCache = new HashMap<>();
        String correspLabel = "CORRESP " + corresp.getId();

        Set<Tasks> tasks = corresp.getTasks() != null ? corresp.getTasks() : Collections.emptySet();

        for (Tasks task : tasks) {
            if (task == null || task.getStatus() == null) continue;

            boolean inProgress = CorrespStatus.IN_PROGRESS.equals(task.getStatus());
            boolean closed = CorrespStatus.CLOSED.equals(task.getStatus());
            if (!inProgress && !closed) continue; // DRAFT / CANCELLED / DISPATCH : pas d'ACL à poser

            r.tasksScanned++;
            String taskLabel = "TASK " + task.getId();

            String taskType = task.getTaskType();
            boolean onlyRead = taskType != null && taskType.equalsIgnoreCase("Lecture");
            boolean readAndWrite =
                taskType != null &&
                    (taskType.equalsIgnoreCase("Traitement") ||
                        taskType.equalsIgnoreCase("Validation") ||
                        taskType.equalsIgnoreCase("Signature"));

            List<Sid> sids = new ArrayList<>();
            for (String dept : resolveDeptCodes(task.getId(), deptCache)) {
                sids.add(new GrantedAuthoritySid(dept));
            }
            for (String login : resolveLogins(task.getId())) {
                sids.add(new PrincipalSid(login));
            }

            for (Sid sid : sids) {
                // --- Sur la TÂCHE ---
                grant(r, task, taskLabel, sid, BasePermission.READ);
                if (inProgress) {
                    grant(r, task, taskLabel, sid, BasePermission.WRITE);
                }

                // --- Sur le CORRESP ---
                if (closed) {
                    grant(r, corresp, correspLabel, sid, BasePermission.READ);
                } else if (onlyRead || readAndWrite) {
                    grant(r, corresp, correspLabel, sid, BasePermission.READ);
                    if (readAndWrite) {
                        grant(r, corresp, correspLabel, sid, BasePermission.WRITE);
                    }
                }
            }
        }

        return r.toMap(correspId);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void grant(Report r, Object domainObject, String objectLabel, Sid sid, Permission permission) {
        String sidLabel = sidLabel(sid);
        String permLabel = permLabel(permission);
        String key = objectLabel + "|" + sidLabel + "|" + permLabel;
        if (!r.seen.add(key)) return; // déjà traité pour ce corresp

        String line = objectLabel + " / " + sidLabel + " / " + permLabel;
        try {
            if (aclUtilService.hasPermissionForSid(domainObject, sid, permission)) return;

            r.missing++;
            if (r.dryRun) {
                r.details.add("MANQUANT : " + line);
                return;
            }

            aclUtilService.addPermissionIfNotExists(domainObject, sid, permission);

            if (aclUtilService.hasPermissionForSid(domainObject, sid, permission)) {
                r.added++;
                r.details.add("AJOUTÉ : " + line);
            } else {
                r.errors++;
                r.details.add("ÉCHEC (ACE toujours absente après insertion) : " + line);
            }
        } catch (Exception e) {
            r.errors++;
            r.details.add("ERREUR : " + line + " -> " + e.getMessage());
            log.warn("Réparation ACL en erreur pour {} : {}", line, e.getMessage());
        }
    }

    private Set<String> resolveDeptCodes(Long taskId, Map<String, String> cache) {
        List<String> raw = responsableTacheService.getResponsableCodeDeptByTacheId(taskId);
        if (raw == null) return Collections.emptySet();
        Set<String> result = new LinkedHashSet<>();
        for (String dept : raw) {
            if (dept == null || dept.trim().isEmpty()) continue;
            String d = dept.trim();
            String effective = cache.computeIfAbsent(d, this::fetchEffectiveDeptCode);
            result.add(effective == null || effective.isBlank() ? d : effective);
        }
        return result;
    }

    private String fetchEffectiveDeptCode(String d) {
        try {
            Object dto = orgaCareRestClient.getDepartementDtoByCode(d);
            if (dto == null) return d;
            if (dto instanceof Map) {
                Object codeObj = ((Map<?, ?>) dto).get("code");
                return codeObj != null ? codeObj.toString().trim() : d;
            }
            java.lang.reflect.Method m = dto.getClass().getMethod("getCode");
            Object codeObj = m.invoke(dto);
            return codeObj != null ? codeObj.toString().trim() : d;
        } catch (Exception ex) {
            log.warn("OrgaCare failed for '{}': {} — fallback", d, ex.getMessage());
            return d;
        }
    }

    private Set<String> resolveLogins(Long taskId) {
        List<String> matricules = responsableTacheService.getResponsablesMatriculePersByTacheId(taskId);
        if (matricules == null || matricules.isEmpty()) return Collections.emptySet();

        List<String> distinct = matricules
            .stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .distinct()
            .collect(Collectors.toList());
        if (distinct.isEmpty()) return Collections.emptySet();

        try {
            List<UserFeignDTO> users = correspGatewayRestClient.findUsersByLogins(distinct);
            if (users == null) return Collections.emptySet();
            return users
                .stream()
                .filter(Objects::nonNull)
                .map(u -> u.getLogin() == null ? null : u.getLogin().trim().toLowerCase())
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.warn("findUsersByLogins failed pour tache {} : {}", taskId, e.getMessage());
            return Collections.emptySet();
        }
    }

    private String sidLabel(Sid sid) {
        if (sid instanceof PrincipalSid) return "USER:" + ((PrincipalSid) sid).getPrincipal();
        if (sid instanceof GrantedAuthoritySid) return "DEPT:" + ((GrantedAuthoritySid) sid).getGrantedAuthority();
        return String.valueOf(sid);
    }

    private String permLabel(Permission p) {
        if (p.getMask() == BasePermission.READ.getMask()) return "READ";
        if (p.getMask() == BasePermission.WRITE.getMask()) return "WRITE";
        return "MASK_" + p.getMask();
    }

    private static class Report {

        final boolean dryRun;
        int tasksScanned;
        int missing;
        int added;
        int errors;
        final List<String> details = new ArrayList<>();
        final Set<String> seen = new HashSet<>();

        Report(boolean dryRun) {
            this.dryRun = dryRun;
        }

        Map<String, Object> toMap(Long correspId) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("correspId", correspId);
            m.put("dryRun", dryRun);
            m.put("tasksScanned", tasksScanned);
            m.put("aclMissing", missing);
            m.put("aclAdded", added);
            m.put("errors", errors);
            m.put("details", details);
            return m;
        }
    }
}
