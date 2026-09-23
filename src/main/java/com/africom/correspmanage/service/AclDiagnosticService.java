package com.africom.correspmanage.service;

import com.africom.correspmanage.client.CorrespGatewayRestClient;
import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.client.UserFeignDTO;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de diagnostic de la logique ACL (lecture seule).
 */
@Service
@Transactional(readOnly = true)
public class AclDiagnosticService {

    private static final Logger log = LoggerFactory.getLogger(AclDiagnosticService.class);
    private static final String NO_SID = "__NO_SID__";

    private final CorrespService correspService;
    private final CorrespRepository correspRepository;
    private final OrgaCareRestClient orgaCareRestClient;
    private final CorrespGatewayRestClient correspGatewayRestClient;
    private final NamedParameterJdbcTemplate named;

    public AclDiagnosticService(
        CorrespService correspService,
        CorrespRepository correspRepository,
        OrgaCareRestClient orgaCareRestClient,
        CorrespGatewayRestClient correspGatewayRestClient,
        JdbcTemplate jdbcTemplate
    ) {
        this.correspService = correspService;
        this.correspRepository = correspRepository;
        this.orgaCareRestClient = orgaCareRestClient;
        this.correspGatewayRestClient = correspGatewayRestClient;
        this.named = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Map<String, Object> diagnose(String rawLogin, Long correspId) {
        String login = rawLogin == null ? "" : rawLogin.trim().toLowerCase();
        int readMask = BasePermission.READ.getMask();

        Map<String, Object> out = new LinkedHashMap<>();
        List<String> problems = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        out.put("login", login);

        // ── 1) userId via gateway ────────────────────────────────────────────
        String userId = null;
        try {
            UserFeignDTO user = correspGatewayRestClient.findUserByLogin(login);
            userId = user != null ? user.getId() : null;
        } catch (Exception e) {
            problems.add("Appel gateway findUserByLogin en échec : " + e.getMessage());
        }
        if (userId == null) {
            problems.add("userId introuvable pour le login '" + login + "' : aucun département ne pourra être résolu");
        }
        out.put("userId", userId);

        // ── 2) Départements via OrgaCare ─────────────────────────────────────
        List<String> deptCodes = new ArrayList<>();
        if (userId != null) {
            try {
                List<Map<String, Object>> aff = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
                if (aff != null) {
                    deptCodes = aff
                        .stream()
                        .map(a -> a.get("departementCode"))
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .collect(Collectors.toList());
                }
            } catch (Exception e) {
                problems.add("Appel OrgaCare getDepartementAffectationsByUserId en échec : " + e.getMessage());
            }
            if (deptCodes.isEmpty()) {
                warnings.add("Aucune affectation département retournée par OrgaCare pour userId " + userId);
            }
        }
        out.put("departmentCodes", deptCodes);

        // ── 3) SIDs effectifs (même méthode que la vraie logique) ────────────
        List<String> sids = correspService.buildSidsForCurrentUser(login);
        out.put("effectiveSids", sids);
        if (sids.contains(NO_SID)) {
            problems.add("SID sentinelle '__NO_SID__' utilisé : ni login ni département résolus, la liste sera vide");
        }

        List<String> lowerSids = sids.stream().map(String::toLowerCase).collect(Collectors.toList());
        Map<String, Object> params = new HashMap<>();
        params.put("sids", lowerSids);
        params.put("readMask", readMask);

        // ── 4) Présence / type / casse dans acl_sid ──────────────────────────
        List<Map<String, Object>> sidRows = named.queryForList(
            "select id, principal, sid from acl_sid where lower(sid) in (:sids) order by sid",
            params
        );
        out.put("aclSidRows", sidRows);

        Map<String, List<Map<String, Object>>> byLower = sidRows
            .stream()
            .collect(Collectors.groupingBy(r -> r.get("sid").toString().toLowerCase()));

        for (String sid : sids) {
            if (NO_SID.equals(sid)) continue;
            List<Map<String, Object>> rows = byLower.getOrDefault(sid.toLowerCase(), Collections.emptyList());
            if (rows.isEmpty()) {
                warnings.add("SID '" + sid + "' absent de acl_sid : aucune ACE n'a jamais été créée pour lui");
                continue;
            }
            boolean expectedPrincipal = sid.equals(login);
            if (rows.stream().noneMatch(r -> sid.equals(r.get("sid")))) {
                problems.add("Casse différente pour '" + sid + "' dans acl_sid : " + rows.stream().map(r -> r.get("sid")).collect(Collectors.toList()));
            }
            if (rows.stream().anyMatch(r -> Boolean.TRUE.equals(r.get("principal")) != expectedPrincipal)) {
                problems.add(
                    "Type incohérent pour '" + sid + "' dans acl_sid : attendu principal=" + expectedPrincipal +
                        " (login = principal, département = authority)"
                );
            }
        }

        // ── 5) Nombre d'objets avec READ accordé, par SID et par classe ──────
        List<Map<String, Object>> readCounts = named.queryForList(
            "select s.sid, s.principal, c.class as object_class, count(distinct e.acl_object_identity) as nb_objects " +
                "from acl_entry e " +
                "join acl_sid s on s.id = e.sid " +
                "join acl_object_identity oi on oi.id = e.acl_object_identity " +
                "join acl_class c on c.id = oi.object_id_class " +
                "where e.granting = true and (e.mask & :readMask) = :readMask and lower(s.sid) in (:sids) " +
                "group by s.sid, s.principal, c.class " +
                "order by s.sid, c.class",
            params
        );
        out.put("readAccessByAclSid", readCounts);

        // ── 6) Totaux réellement retournés par les requêtes de liste ─────────
        Map<String, Object> visible = new LinkedHashMap<>();
        try {
            Page<Number> adminBoc = correspRepository.findAllIdsByAclOrDraftOrDispatch(sids, readMask, login, PageRequest.of(0, 1));
            visible.put("aclDraftDispatch_adminBoc", adminBoc.getTotalElements());
        } catch (Exception e) {
            problems.add("Requête findAllIdsByAclOrDraftOrDispatch en échec : " + e.getMessage());
        }
        try {
            Page<Number> user = correspRepository.findAllIdsByAclForSidsOrDraft(sids, readMask, login, PageRequest.of(0, 1));
            visible.put("aclDraft_user", user.getTotalElements());
        } catch (Exception e) {
            problems.add("Requête findAllIdsByAclForSidsOrDraft en échec : " + e.getMessage());
        }
        out.put("visibleCorrespCount", visible);

        // ── 7) Détail optionnel pour UNE correspondance ──────────────────────
        if (correspId != null) {
            out.put("correspDetail", correspDetail(correspId, login, sids));
        }

        out.put("problems", problems);
        out.put("warnings", warnings);
        out.put("status", problems.isEmpty() ? "OK" : "KO");
        log.debug("ACL diagnostic pour '{}' : {}", login, out.get("status"));
        return out;
    }

    /**
     * Version légère : uniquement les SIDs effectifs de l'utilisateur (aucune requête SQL sur acl_*).
     * Utilise la même méthode que les vraies listes : buildSidsForCurrentUser.
     */
    public Map<String, Object> mySids(String rawLogin) {
        String login = rawLogin == null ? "" : rawLogin.trim().toLowerCase();
        List<String> sids = correspService.buildSidsForCurrentUser(login);

        List<String> departmentSids = sids
            .stream()
            .filter(s -> !NO_SID.equals(s))
            .filter(s -> !s.equalsIgnoreCase(login))
            .collect(Collectors.toList());

        String status;
        String message;
        if (sids.contains(NO_SID)) {
            status = "KO";
            message = "Aucun SID résolu (login et départements introuvables) : les listes seront vides. Vérifier gateway/OrgaCare.";
        } else if (departmentSids.isEmpty()) {
            status = "PARTIAL";
            message = "Seul le SID personnel est résolu : aucun département retourné par OrgaCare.";
        } else {
            status = "OK";
            message = "SID personnel et départements résolus.";
        }

        // ── Warnings : SIDs sans ACE, absents de acl_sid, ou de type incohérent ──
        List<String> warnings = new ArrayList<>();
        List<String> realSids = sids.stream().filter(s -> !NO_SID.equals(s)).collect(Collectors.toList());

        if (!realSids.isEmpty()) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("sids", realSids.stream().map(String::toLowerCase).collect(Collectors.toList()));

                // Une ligne par SID présent dans acl_sid, avec son nombre d'ACE (tous types de permissions confondus)
                List<Map<String, Object>> rows = named.queryForList(
                    "select s.sid, s.principal, count(e.id) as nb_ace " +
                        "from acl_sid s " +
                        "left join acl_entry e on e.sid = s.id " +
                        "where lower(s.sid) in (:sids) " +
                        "group by s.sid, s.principal",
                    params
                );

                Map<String, List<Map<String, Object>>> byLower = rows
                    .stream()
                    .collect(Collectors.groupingBy(r -> r.get("sid").toString().toLowerCase()));

                for (String sid : realSids) {
                    List<Map<String, Object>> found = byLower.getOrDefault(sid.toLowerCase(), Collections.emptyList());
                    if (found.isEmpty()) {
                        warnings.add("SID '" + sid + "' absent de acl_sid : aucune ACE n'a jamais été créée pour lui");
                        continue;
                    }

                    long totalAce = found.stream().mapToLong(r -> ((Number) r.get("nb_ace")).longValue()).sum();
                    if (totalAce == 0) {
                        warnings.add("SID '" + sid + "' présent dans acl_sid mais sans aucune ACE");
                    }

                    boolean expectedPrincipal = sid.equalsIgnoreCase(login);
                    boolean typeMismatch = found.stream().anyMatch(r -> Boolean.TRUE.equals(r.get("principal")) != expectedPrincipal);
                    if (typeMismatch) {
                        warnings.add(
                            "SID '" + sid + "' : type incohérent dans acl_sid (attendu principal=" + expectedPrincipal +
                                " : login = principal, département = authority)"
                        );
                    }
                    if (found.stream().noneMatch(r -> sid.equals(r.get("sid")))) {
                        warnings.add("SID '" + sid + "' : casse différente dans acl_sid " +
                            found.stream().map(r -> r.get("sid")).collect(Collectors.toList()));
                    }
                }
            } catch (Exception e) {
                warnings.add("Vérification acl_sid impossible : " + e.getMessage());
            }
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("login", login);
        out.put("principalSid", login);
        out.put("departmentSids", departmentSids);
        out.put("effectiveSids", sids);
        out.put("status", status);
        out.put("message", message);
        out.put("warnings", warnings);
        return out;
    }

    private Map<String, Object> correspDetail(Long correspId, String login, List<String> sids) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("correspId", correspId);

        Optional<Corresp> opt = correspRepository.findById(correspId);
        detail.put("exists", opt.isPresent());
        CorrespStatus status = opt.map(Corresp::getStatus).orElse(null);
        String createdBy = opt.map(Corresp::getCreatedBy).orElse(null);
        detail.put("status", status != null ? status.name() : null);
        detail.put("createdBy", createdBy);

        Map<String, Object> params = new HashMap<>();
        params.put("cls", Corresp.class.getName());
        params.put("id", String.valueOf(correspId));

        List<Map<String, Object>> entries = named.queryForList(
            "select s.sid, s.principal, e.mask, e.granting, e.ace_order " +
                "from acl_entry e " +
                "join acl_sid s on s.id = e.sid " +
                "join acl_object_identity oi on oi.id = e.acl_object_identity " +
                "join acl_class c on c.id = oi.object_id_class " +
                "where c.class = :cls and cast(oi.object_id_identity as varchar) = :id " +
                "order by e.ace_order",
            params
        );
        detail.put("aclEntries", entries);

        boolean readGranted = entries
            .stream()
            .anyMatch(r ->
                sids.contains((String) r.get("sid")) &&
                    Boolean.TRUE.equals(r.get("granting")) &&
                    (((Number) r.get("mask")).intValue() & BasePermission.READ.getMask()) != 0
            );

        List<String> reasons = new ArrayList<>();
        if (readGranted) reasons.add("ACL_READ");
        if (CorrespStatus.DRAFT.equals(status) && createdBy != null && createdBy.equalsIgnoreCase(login)) reasons.add("DRAFT_OWNER");
        if (CorrespStatus.DISPATCH.equals(status)) reasons.add("DISPATCH (visible seulement dans la liste admin/BOC)");

        detail.put("visibleBecause", reasons);
        detail.put("visibleForUser", !reasons.isEmpty());
        return detail;
    }
}
