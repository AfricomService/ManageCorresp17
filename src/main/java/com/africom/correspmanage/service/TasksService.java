package com.africom.correspmanage.service;

import com.africom.correspmanage.client.CorrespGatewayRestClient;
import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.ResponsableTache;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.service.dto.PagedTaskDTO;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.service.mapper.TasksMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.africom.correspmanage.security.SecurityUtils;

/**
 * Service Implementation for managing {@link Tasks}.
 */
@Service
@Transactional
public class TasksService {

    private final Logger log = LoggerFactory.getLogger(TasksService.class);

    private final TasksRepository tasksRepository;

    private final TasksMapper tasksMapper;

    private final CorrespRepository correspRepository;

    private final OrgaCareRestClient orgaCareRestClient;

    private final ResponsableTacheService responsableTacheService;

    private final CorrespGatewayRestClient correspGatewayRestClient;
    private final CorrespService correspService;

    public TasksService(
        TasksRepository tasksRepository,
        TasksMapper tasksMapper,
        CorrespRepository correspRepository,
        OrgaCareRestClient orgaCareRestClient,
        ResponsableTacheService responsableTacheService,
        CorrespGatewayRestClient correspGatewayRestClient,
        CorrespService correspService) {
        this.tasksRepository = tasksRepository;
        this.tasksMapper = tasksMapper;
        this.correspRepository = correspRepository;
        this.orgaCareRestClient = orgaCareRestClient;
        this.responsableTacheService = responsableTacheService;
        this.correspGatewayRestClient = correspGatewayRestClient;
        this.correspService = correspService;
    }

    /**
     * Récupère le login d'un utilisateur à partir de son id via le gateway.
     * Retourne null si impossible.
     */
    private String getUserLogin(String userId) {
        try {
            Map<String, Object> userMap = correspGatewayRestClient.getUserById(userId);
            if (userMap != null) {
                Object login = userMap.get("login");
                if (login != null && !login.toString().trim().isEmpty()) {
                    // changeTaskStatus stocke le login en minuscules dans acl_sid via toLowerCase()
                    return login.toString().trim().toLowerCase();
                }
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer le login pour userId {} : {}", userId, e.getMessage());
        }
        return null;
    }


    /**
     * Save a tasks.
     *
     * @param tasksDTO the entity to save.
     * @return the persisted entity.
     */
    public TasksDTO save(TasksDTO tasksDTO) {
        log.debug("Request to save Tasks : {}", tasksDTO);
        Tasks tasks = tasksMapper.toEntity(tasksDTO);
        tasks = tasksRepository.save(tasks);
        return tasksMapper.toDto(tasks);
    }

    /**
     * Update a tasks.
     *
     * @param tasksDTO the entity to save.
     * @return the persisted entity.
     */
    public TasksDTO update(TasksDTO tasksDTO) {
        log.debug("Request to update Tasks : {}", tasksDTO);
        Tasks tasks = tasksMapper.toEntity(tasksDTO);
        tasks = tasksRepository.save(tasks);
        return tasksMapper.toDto(tasks);
    }

    @Transactional(readOnly = true)
    public List<TasksDTO> findAllByEtat(String etat) {
        log.debug("Request to get all Tasks");

        CorrespStatus correspStatus = CorrespStatus.DRAFT;

        switch (etat) {
            case "DRAFT":
                correspStatus = CorrespStatus.DRAFT;
                break;
            case "IN_PROGRESS":
                correspStatus = CorrespStatus.IN_PROGRESS;
                break;
            case "CANCELLED":
                correspStatus = CorrespStatus.CANCELLED;
                break;
            case "CLOSED":
                correspStatus = CorrespStatus.CLOSED;
                break;
        }

        return tasksRepository
            .findAllByStatusEquals(correspStatus)
            .stream()
            .map(tasksMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Partially update a tasks.
     *
     * @param tasksDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TasksDTO> partialUpdate(TasksDTO tasksDTO) {
        log.debug("Request to partially update Tasks : {}", tasksDTO);

        return tasksRepository
            .findById(tasksDTO.getId())
            .map(existingTasks -> {
                tasksMapper.partialUpdate(existingTasks, tasksDTO);

                return existingTasks;
            })
            .map(tasksRepository::save)
            .map(tasksMapper::toDto);
    }

    /**
     * Marque une tâche comme prise en charge : persiste la date actuelle
     * dans l'attribut datePriseEnCharge.
     *
     * @param id l'id de la tâche.
     * @return la tâche mise à jour, ou Optional.empty() si non trouvée.
     */
    public Optional<TasksDTO> markPriseEnCharge(Long id) {
        log.debug("Request to mark Tasks prise en charge : {}", id);
        return tasksRepository
            .findById(id)
            .map(existingTask -> {
                existingTask.setDatePriseEnCharge(ZonedDateTime.now());
                return existingTask;
            })
            .map(tasksRepository::save)
            .map(tasksMapper::toDto);
    }

    /**
     * Get all the tasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TasksDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return tasksRepository.findAll(pageable).map(tasksMapper::toDto);
    }

    /**
     * Get one tasks by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TasksDTO> findOne(Long id) {
        log.debug("Request to get Tasks : {}", id);
        return tasksRepository.findById(id).map(tasksMapper::toDto);
    }

    /**
     * Delete the tasks by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tasks : {}", id);
        tasksRepository.deleteById(id);
    }

    public void deleteTask(Long taskId, Long correspId) {
        Corresp corresp = correspRepository.findById(correspId).orElseThrow(() -> new RuntimeException("Corresp not found"));

        boolean removed = corresp.getTasks().removeIf(task -> task.getId().equals(taskId));

        if (!removed) {
            throw new RuntimeException("Task not found in this corresp");
        }

        correspRepository.save(corresp);

        tasksRepository.deleteById(taskId);
    }

    //    public List<TasksDTO> getAllTasksByCorrespId(Long correspId) {
    //        log.debug("Request to get all Tasks by correspId : {}", correspId);
    //
    //        return tasksRepository.findTasksByCorrespId(correspId).stream().map(tasksMapper::toDto).toList();
    //    }

//    public List<TasksDTO> getAllTasksByCorrespId(Long correspId) {
//        log.debug("Request to get all Tasks by correspId : {}", correspId);
//
//        return tasksRepository
//            .findTasksByCorrespId(correspId)
//            .stream()
//            .map(task -> {
//                TasksDTO dto = new TasksDTO();
//
//                dto.setId(task.getId());
//                dto.setObject(task.getObject());
//                dto.setContent(task.getContent());
//                dto.setContentContentType(task.getContentContentType());
//                dto.setCreationDate(task.getCreationDate());
//                dto.setStartDate(task.getStartDate());
//                dto.setDueDate(task.getDueDate());
//                dto.setCloseDate(task.getCloseDate());
//                dto.setResponsible(task.getResponsible());
//                dto.setStatus(task.getStatus());
//                dto.setCreatedBy(task.getCreatedBy());
//                dto.setUpdatedBy(task.getUpdatedBy());
//                dto.setUpdatedDate(task.getUpdatedDate());
//                dto.setTaskType(task.getTaskType());
//                dto.setCommentaire(task.getCommentaire());
//
//                try {
//                    String responsableName = orgaCareRestClient.getDepartementNomByCode(task.getResponsible());
//                    dto.setResponsableName(responsableName);
//                } catch (Exception e) {
//                    log.warn("Could not fetch responsible name for code {}", task.getResponsible());
//                }
//
//                return dto;
//            })
//            .toList();
//    }

    public List<TasksDTO> getAllTasksByCorrespId(Long correspId) {
        log.debug("Request to get all Tasks by correspId : {}", correspId);

        List<Tasks> taskEntities = tasksRepository.findTasksByCorrespId(correspId);
        if (taskEntities == null || taskEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // 1) Mapper en DTO
        List<TasksDTO> dtos = taskEntities.stream().map(task -> {
            TasksDTO dto = new TasksDTO();
            dto.setId(task.getId());
            dto.setObject(task.getObject());
            dto.setContent(task.getContent());
            dto.setContentContentType(task.getContentContentType());
            dto.setCreationDate(task.getCreationDate());
            dto.setStartDate(task.getStartDate());
            dto.setDueDate(task.getDueDate());
            dto.setCloseDate(task.getCloseDate());
            dto.setResponsible(task.getResponsible());
            dto.setStatus(task.getStatus());
            dto.setCreatedBy(task.getCreatedBy());
            dto.setUpdatedBy(task.getUpdatedBy());
            dto.setUpdatedDate(task.getUpdatedDate());
            dto.setTaskType(task.getTaskType());
            dto.setCommentaire(task.getCommentaire());
            dto.setCommentaireTraitement(task.getCommentaireTraitement());
            dto.setDatePriseEnCharge(task.getDatePriseEnCharge());
            return dto;
        }).collect(Collectors.toList());

        // 2) Récupérer tous les ids de tâches pour batch DB
        List<Long> tacheIds = dtos.stream()
            .map(TasksDTO::getId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        if (tacheIds.isEmpty()) {
            return dtos;
        }

        // 3) Récupérer en une seule requête tous les ResponsableTache pour ces tâches
        List<ResponsableTache> responsablesEntities = responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) {
            responsablesEntities = Collections.emptyList();
        }

        // 4) Collecter codes et matricules uniques pour appel bulk OrgaCare
        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers)
            .filter(Objects::nonNull)
            .map(m -> m.trim().toLowerCase())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();

        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'appel OrgaCare pour noms responsables: {}", e.getMessage());
            deptNamesByCode = deptNamesByCode == null ? Collections.emptyMap() : deptNamesByCode;
            personNamesByMatricule = personNamesByMatricule == null ? Collections.emptyMap() : personNamesByMatricule;
        }

        // 5) Grouper les entités ResponsableTache par tacheId
        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        // 6) Pour chaque TasksDTO, construire la liste des ResponsableTacheDTO avec les noms résolus
        for (TasksDTO dto : dtos) {
            List<ResponsableTache> listEnt = responsablesByTache.getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();

            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());

                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    String code = ent.getCodeDept().trim();
                    String nomDept = deptNamesByCode.get(code);
                    rDto.setDeptName(nomDept);
                }

                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    String mat = ent.getMatriculePers().trim().toLowerCase();
                    String nomPers = personNamesByMatricule.get(mat);
                    rDto.setPersonneName(nomPers);
                }

                rDtos.add(rDto);
            }

            // Dédoublonner par (codeDept, matriculePers)
            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:" + k.getCodeDept())
                            + "|" + (k.getMatriculePers() == null ? "" : "P:" + k.getMatriculePers()),
                        v -> v,
                        (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));

            dto.setResponsables(unique);

            // Aussi construire le responsableName pour le frontend (fallback)
            List<String> displayNames = new ArrayList<>();
            for (ResponsableTacheDTO r : unique) {
                if (r.getPersonneName() != null && !r.getPersonneName().trim().isEmpty()) {
                    displayNames.add(r.getPersonneName().trim());
                } else if (r.getDeptName() != null && !r.getDeptName().trim().isEmpty()) {
                    displayNames.add(r.getDeptName().trim());
                } else if (r.getCodeDept() != null && !r.getCodeDept().trim().isEmpty()) {
                    displayNames.add(r.getCodeDept().trim());
                } else if (r.getMatriculePers() != null && !r.getMatriculePers().trim().isEmpty()) {
                    displayNames.add(r.getMatriculePers().trim());
                }
            }
            if (!displayNames.isEmpty()) {
                dto.setResponsableName(String.join(", ", displayNames.stream().distinct().collect(Collectors.toList())));
            }
        }

        return dtos;
    }

    //    public List<TasksDTO> getAllTasksByAcl(String deptCode) {
    //        return tasksRepository.findAllByAclForDept(deptCode).stream().map(tasksMapper::toDto).toList();
    //    }

    /**
     * Retourne les Tasks accessibles par au moins un des codes de départements fournis.
     */
    public List<TasksDTO> getAllTasksByAcl(List<String> deptCodes) {
        if (deptCodes == null || deptCodes.isEmpty()) {
            return Collections.emptyList();
        }
        // mask pour READ
        int readMask = BasePermission.READ.getMask();
        List<Tasks> tasks = tasksRepository.findAllByAclForDepts(deptCodes, readMask);
        return tasks.stream().map(tasksMapper::toDto).collect(Collectors.toList());
    }


    /**
     * Récupère d'abord la liste des codes depuis OrgaCare (par userId) puis retourne les tasks ACL correspondantes.
     * La méthode résout aussi les responsables (codeDept -> deptName, matriculePers -> personneName)
     * en appelant OrgaCare en bulk pour éviter les N+1.
     */
    @Transactional(readOnly = true)
    public List<TasksDTO> getAllTasksByUserAcl(String userId, CorrespStatus status) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<String> deptCodes;
        try {
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
            if (affectations == null || affectations.isEmpty()) {
                deptCodes = new ArrayList<>();
            } else {
                deptCodes = affectations.stream()
                    .map(a -> a.get("departementCode"))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .distinct()
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer les codes des départements pour userId {} : {}", userId, e.getMessage());
            deptCodes = new ArrayList<>();
        }

        // Récupérer aussi le login de l'utilisateur pour les ACLs PrincipalSid
        String userLogin = getUserLogin(userId);

        // Construire la liste complète des SIDs : deptCodes + login utilisateur
        List<String> allSids = new ArrayList<>(deptCodes);
        if (userLogin != null && !allSids.contains(userLogin)) {
            allSids.add(userLogin);
        }

        if (allSids.isEmpty()) {
            return Collections.emptyList();
        }

        // Récupère les tasks accessibles par ces SIDs (départements + login)
        int readMask = BasePermission.READ.getMask();
        List<Tasks> taskEntities = tasksRepository.findAllByAclForDepts(allSids, readMask);
        List<TasksDTO> all = taskEntities == null ? Collections.emptyList()
            : taskEntities.stream().map(tasksMapper::toDto).collect(Collectors.toList());
        if (all.isEmpty()) {
            return Collections.emptyList();
        }

        // Si aucun status fourni, garder le comportement précédent (IN_PROGRESS)
        CorrespStatus filterStatus = (status == null) ? CorrespStatus.IN_PROGRESS : status;

// Filtrer par status demandé + trier par creationDate DESC (plus récent en premier)
        List<TasksDTO> filtered = all.stream()
            .filter(t -> t.getStatus() != null && filterStatus.equals(t.getStatus()))
            .sorted(Comparator.comparing(
                TasksDTO::getCreationDate,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return Collections.emptyList();
        }

        // Rassembler tous les ids de tâches pour batch DB
        List<Long> tacheIds = filtered.stream()
            .map(TasksDTO::getId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        // Récupérer en une seule requête tous les ResponsableTache pour ces tâches
        List<ResponsableTache> responsablesEntities = responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) {
            responsablesEntities = Collections.emptyList();
        }

        // Collecter codes et matricules uniques pour appel bulk OrgaCare
        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers)
            .filter(Objects::nonNull)
            .map(m -> m.trim().toLowerCase())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();

        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'appel OrgaCare pour noms responsables: {}", e.getMessage());
            deptNamesByCode = deptNamesByCode == null ? Collections.emptyMap() : deptNamesByCode;
            personNamesByMatricule = personNamesByMatricule == null ? Collections.emptyMap() : personNamesByMatricule;
        }

        // Grouper les entités ResponsableTache par tacheId
        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        // Pour chaque TasksDTO filtré, construire la liste des ResponsableTacheDTO avec les noms résolus
        for (TasksDTO dto : filtered) {
            List<ResponsableTache> listEnt = responsablesByTache.getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();

            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());

                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    String code = ent.getCodeDept().trim();
                    String nomDept = deptNamesByCode.get(code);
                    rDto.setDeptName(nomDept);
                }

                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    String mat = ent.getMatriculePers().trim().toLowerCase();
                    String nomPers = personNamesByMatricule.get(mat);
                    rDto.setPersonneName(nomPers);
                }

                rDtos.add(rDto);
            }

            // dédoublonner par (codeDept, matriculePers)
            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:" + k.getCodeDept()) + "|" + (k.getMatriculePers() == null ? "" : "P:" + k.getMatriculePers()),
                        v -> v,
                        (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));

            dto.setResponsables(unique);
        }

        return filtered;
    }

    /** Compatibilité : l'ancien appel (page Home) reste identique, sans sous-départements. */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllTasksByUserAclPaged(
        String userId,
        CorrespStatus status,
        int pageIndex,
        int pageSize,
        LocalDate startDateFrom,
        LocalDate startDateTo
    ) {
        return getAllTasksByUserAclPaged(userId, status, pageIndex, pageSize, startDateFrom, startDateTo, false);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllTasksByUserAclPaged(
        String userId,
        CorrespStatus status,
        int pageIndex,
        int pageSize,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        boolean includeChildren
    ) {
        if (userId == null) {
            return buildEmptyPageResponse(pageIndex, pageSize);
        }

        // 1) SIDs = départements de l'utilisateur (+ sous-départements si demandé) + login
        List<String> allSids = buildSidsForUser(userId, includeChildren);
        log.debug("getAllTasksByUserAclPaged userId={} includeChildren={} sids={}", userId, includeChildren, allSids);

        if (allSids.isEmpty()) {
            return buildEmptyPageResponse(pageIndex, pageSize);
        }

        // 2) Pageable sans Sort (le tri est dans le SQL)
        Pageable pageable = PageRequest.of(Math.max(0, pageIndex - 1), pageSize);
        int readMask = BasePermission.READ.getMask();
        String statusStr = (status != null) ? status.name() : null;

        // 3) Requête paginée sur les IDs uniquement
        // Filtre sur start_date : début inclus (00:00), fin inclusive => borne exclusive = fin + 1 jour
        int dateFilter = (startDateFrom != null || startDateTo != null) ? 1 : 0;
        LocalDateTime startFrom = startDateFrom != null
            ? toUtcLocalDateTime(startDateFrom)
            : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime startToExclusive = startDateTo != null
            ? toUtcLocalDateTime(startDateTo.plusDays(1))
            : LocalDateTime.of(2999, 12, 31, 0, 0);

        Page<Object> idsPage = tasksRepository.findAllIdsByAclForSidsAndStatusPaged(
            allSids, readMask, statusStr, dateFilter, startFrom, startToExclusive, pageable
        );

        if (idsPage.isEmpty()) {
            return buildEmptyPageResponse(pageIndex, pageSize);
        }

        // 4) Charger les entités complètes
        List<Long> ids = idsPage.getContent().stream()
            .map(obj -> {
                if (obj instanceof Long) return (Long) obj;
                if (obj instanceof java.math.BigInteger) return ((java.math.BigInteger) obj).longValue();
                return Long.valueOf(obj.toString());
            })
            .collect(Collectors.toList());
        List<Tasks> taskEntities = tasksRepository.findAllByIds(ids);

        // 5) Maintenir l'ordre
        Map<Long, Tasks> taskMap = taskEntities.stream()
            .collect(Collectors.toMap(Tasks::getId, t -> t));
        List<Tasks> ordered = ids.stream()
            .map(taskMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 6) Mapper en DTO
        List<PagedTaskDTO> dtos = ordered.stream()
            .map(this::toPagedTaskDTO)          // mapping léger, pas de content/byte[]
            .collect(Collectors.toList());

        // 7) Enrichir les responsables (même logique existante)
        dtos = enrichWithResponsablesLight(dtos);

        // 8) Construire la réponse paginée
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", dtos);
        response.put("currentPage", pageIndex);
        response.put("pageSize", pageSize);
        response.put("totalItems", idsPage.getTotalElements());
        response.put("totalPages", idsPage.getTotalPages());
        response.put("hasNext", idsPage.hasNext());
        response.put("hasPrevious", idsPage.hasPrevious());
        return response;
    }

    /** Mapping léger Tasks → PagedTaskDTO (sans content, sans parentDoc, etc.) */
    private PagedTaskDTO toPagedTaskDTO(Tasks task) {
        PagedTaskDTO dto = new PagedTaskDTO();

        dto.setId(task.getId());
        dto.setObject(task.getObject());
        dto.setCreationDate(task.getCreationDate());
        dto.setDueDate(task.getDueDate());
        dto.setCloseDate(task.getCloseDate());
        dto.setStatus(task.getStatus());
        dto.setTaskType(task.getTaskType());
        dto.setCommentaire(task.getCommentaire());
        dto.setCommentaireTraitement(task.getCommentaireTraitement());
        dto.setDatePriseEnCharge(task.getDatePriseEnCharge());

        // responsible brut conservé temporairement pour fallback enrichissement
        // (sera résolu dans enrichWithResponsablesLight)

        return dto;
    }

    /**
     * Variante légère de enrichWithResponsables pour PagedTaskDTO.
     * Même logique batch OrgaCare, même dédoublonnage.
     */
    private List<PagedTaskDTO> enrichWithResponsablesLight(List<PagedTaskDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return dtos;

        List<Long> tacheIds = dtos.stream()
            .map(PagedTaskDTO::getId).filter(Objects::nonNull).distinct()
            .collect(Collectors.toList());

        List<ResponsableTache> responsablesEntities = responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) responsablesEntities = Collections.emptyList();

        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept).filter(Objects::nonNull).map(String::trim)
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers).filter(Objects::nonNull)
            .map(m -> m.trim().toLowerCase())
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();
        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur OrgaCare enrichissement responsables (light): {}", e.getMessage());
        }

        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        final Map<String, String> finalDeptNames = deptNamesByCode;
        final Map<String, String> finalPersonNames = personNamesByMatricule;

        for (PagedTaskDTO dto : dtos) {
            List<ResponsableTache> listEnt = responsablesByTache
                .getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();

            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());
                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    rDto.setDeptName(finalDeptNames.get(ent.getCodeDept().trim()));
                }
                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    rDto.setPersonneName(finalPersonNames.get(ent.getMatriculePers().trim().toLowerCase()));
                }
                rDtos.add(rDto);
            }

            // Dédoublonner par (codeDept, matriculePers)
            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:" + k.getCodeDept())
                            + "|" + (k.getMatriculePers() == null ? "" : "P:" + k.getMatriculePers()),
                        v -> v, (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));

            dto.setResponsables(unique);

            // Construire responsableName (fallback chaîne pour le frontend)
            List<String> displayNames = new ArrayList<>();
            for (ResponsableTacheDTO r : unique) {
                if (r.getPersonneName() != null && !r.getPersonneName().trim().isEmpty()) {
                    displayNames.add(r.getPersonneName().trim());
                } else if (r.getDeptName() != null && !r.getDeptName().trim().isEmpty()) {
                    displayNames.add(r.getDeptName().trim());
                } else if (r.getCodeDept() != null && !r.getCodeDept().trim().isEmpty()) {
                    displayNames.add(r.getCodeDept().trim());
                } else if (r.getMatriculePers() != null && !r.getMatriculePers().trim().isEmpty()) {
                    displayNames.add(r.getMatriculePers().trim());
                }
            }
            if (!displayNames.isEmpty()) {
                dto.setResponsableName(
                    displayNames.stream().distinct().collect(Collectors.joining(", "))
                );
            }
        }

        return dtos;
    }

    /** Début de journée (fuseau du serveur) converti en UTC, car Hibernate écrit les dates en UTC (jdbc.time_zone=UTC). */
    private LocalDateTime toUtcLocalDateTime(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * SIDs ACL d'un utilisateur :
     * codes de ses départements (+ tous leurs sous-départements si includeChildren) + son login (minuscules).
     */
    private List<String> buildSidsForUser(String userId, boolean includeChildren) {
        Set<String> sids = new LinkedHashSet<>();

        // 1) Départements de l'utilisateur (toutes sociétés confondues)
        List<String> userDeptCodes = new ArrayList<>();
        try {
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
            if (affectations != null) {
                affectations.stream()
                    .map(a -> a.get("departementCode"))
                    .filter(Objects::nonNull)
                    .map(o -> o.toString().trim())
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .forEach(userDeptCodes::add);
            }
        } catch (Exception e) {
            log.warn("buildSidsForUser: départements indisponibles pour userId {} : {}", userId, e.getMessage());
        }
        sids.addAll(userDeptCodes);

        // 2) Sous-départements (OrgaCare renvoie le code lui-même + tous les descendants)
        if (includeChildren) {
            for (String code : userDeptCodes) {
                try {
                    List<String> codes = orgaCareRestClient.getDepartementAndChildrenCodes(code);
                    if (codes != null) {
                        codes.stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .forEach(sids::add);
                    }
                } catch (Exception e) {
                    log.warn("buildSidsForUser: sous-départements indisponibles pour '{}' : {}", code, e.getMessage());
                }
            }
        }

        // 3) Login (PrincipalSid)
        String userLogin = getUserLogin(userId);
        if (userLogin != null && !userLogin.isEmpty()) {
            sids.add(userLogin);
        }

        return new ArrayList<>(sids);
    }

    private Map<String, Object> buildEmptyPageResponse(int pageIndex, int pageSize) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", Collections.emptyList());
        response.put("currentPage", pageIndex);
        response.put("pageSize", pageSize);
        response.put("totalItems", 0L);
        response.put("totalPages", 0);
        response.put("hasNext", false);
        response.put("hasPrevious", false);
        return response;
    }

    // Méthode utilitaire pour enrichir les DTOs avec les responsables (extrait la logique commune)
    private List<TasksDTO> enrichWithResponsables(List<TasksDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return dtos;

        List<Long> tacheIds = dtos.stream()
            .map(TasksDTO::getId).filter(Objects::nonNull).distinct()
            .collect(Collectors.toList());

        List<ResponsableTache> responsablesEntities = responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) responsablesEntities = Collections.emptyList();

        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept).filter(Objects::nonNull).map(String::trim)
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers).filter(Objects::nonNull).map(String::trim)
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();

        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur OrgaCare enrichissement responsables: {}", e.getMessage());
        }

        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        final Map<String, String> finalDeptNames = deptNamesByCode;
        final Map<String, String> finalPersonNames = personNamesByMatricule;

        for (TasksDTO dto : dtos) {
            List<ResponsableTache> listEnt = responsablesByTache.getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();

            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());
                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    rDto.setDeptName(finalDeptNames.get(ent.getCodeDept().trim()));
                }
                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    rDto.setPersonneName(finalPersonNames.get(ent.getMatriculePers().trim()));
                }
                rDtos.add(rDto);
            }

            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:" + k.getCodeDept())
                            + "|" + (k.getMatriculePers() == null ? "" : "P:" + k.getMatriculePers()),
                        v -> v, (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));
            dto.setResponsables(unique);
        }

        return dtos;
    }


    @Transactional(readOnly = true)
    public List<TasksDTO> getAllTasksByUserAclAllStatuses(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<String> deptCodes;
        try {
            // récupère affectations (List<Map<String,Object>>) et extrait les departementCode
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);

            if (affectations == null || affectations.isEmpty()) {
                deptCodes = new ArrayList<>();
            } else {
                deptCodes = affectations.stream()
                    .map(a -> a.get("departementCode"))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .distinct()
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer les codes des départements pour userId {} : {}", userId, e.getMessage());
            deptCodes = new ArrayList<>();
        }

        // Récupérer aussi le login de l'utilisateur pour les ACLs PrincipalSid
        String userLogin = getUserLogin(userId);

        // Construire la liste complète des SIDs : deptCodes + login utilisateur
        List<String> allSids = new ArrayList<>(deptCodes);
        if (userLogin != null && !allSids.contains(userLogin)) {
            allSids.add(userLogin);
        }

        if (allSids.isEmpty()) {
            return Collections.emptyList();
        }

        // Récupère les tasks accessibles par ces SIDs (TOUTES les tâches, aucun filtre sur status)
        int readMask = BasePermission.READ.getMask();
        List<Tasks> taskEntities = tasksRepository.findAllByAclForDepts(allSids, readMask);
        List<TasksDTO> all = taskEntities == null ? Collections.emptyList()
            : taskEntities.stream().map(tasksMapper::toDto).collect(Collectors.toList());
        if (all.isEmpty()) {
            return Collections.emptyList();
        }

        // Rassembler tous les ids de tâches pour batch DB
        List<Long> tacheIds = all.stream()
            .map(TasksDTO::getId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        // Récupérer en une seule requête tous les ResponsableTache pour ces tâches
        List<ResponsableTache> responsablesEntities = responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) {
            responsablesEntities = Collections.emptyList();
        }

        // Collecter codes et matricules uniques pour appel bulk OrgaCare
        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers)
            .filter(Objects::nonNull)
            .map(m -> m.trim().toLowerCase())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();

        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'appel OrgaCare pour noms responsables: {}", e.getMessage());
            deptNamesByCode = deptNamesByCode == null ? Collections.emptyMap() : deptNamesByCode;
            personNamesByMatricule = personNamesByMatricule == null ? Collections.emptyMap() : personNamesByMatricule;
        }

        // Grouper les entités ResponsableTache par tacheId
        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        // Pour chaque TasksDTO, construire la liste des ResponsableTacheDTO avec les noms résolus
        for (TasksDTO dto : all) {
            List<ResponsableTache> listEnt = responsablesByTache.getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();

            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());

                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    String code = ent.getCodeDept().trim();
                    String nomDept = deptNamesByCode.get(code);
                    rDto.setDeptName(nomDept);
                }

                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    String mat = ent.getMatriculePers().trim().toLowerCase();
                    String nomPers = personNamesByMatricule.get(mat);
                    rDto.setPersonneName(nomPers);
                }

                rDtos.add(rDto);
            }

            // dédoublonner par (codeDept, matriculePers)
            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:"+k.getCodeDept()) + "|" + (k.getMatriculePers() == null ? "" : "P:"+k.getMatriculePers()),
                        v -> v,
                        (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));

            dto.setResponsables(unique);
        }

        return all;
    }

    /** Compatibilité : appel existant (page Home), sans sous-départements. */
    @Transactional(readOnly = true)
    public List<TasksDTO> searchTasksByUserAcl(String userId, String reference, String subject) {
        return searchTasksByUserAcl(userId, reference, subject, false);
    }

    @Transactional(readOnly = true)
    public List<TasksDTO> searchTasksByUserAcl(String userId, String reference, String subject, boolean includeChildren)
    {
        if (userId == null) {
            return Collections.emptyList();
        }

        String refTrim     = (reference != null && !reference.trim().isEmpty()) ? reference.trim() : null;
        String subjectTrim = (subject   != null && !subject.trim().isEmpty())   ? subject.trim()   : null;

        if (refTrim == null && subjectTrim == null) {
            return Collections.emptyList();
        }

        // 1) SIDs : départements (+ sous-départements si demandé) + login
        List<String> allSids = buildSidsForUser(userId, includeChildren);

        if (allSids.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) repo natif: ACL + filtre référence LIKE (insensible à la casse)
        int readMask = BasePermission.READ.getMask();
        List<Tasks> tasks = tasksRepository.findAllByAclForDeptsAndCorrespKeyword(allSids, readMask, refTrim, subjectTrim);
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) mapper en DTO
        List<TasksDTO> dtos = tasks.stream().map(tasksMapper::toDto).collect(Collectors.toList());

        // 4) enrichissement responsables (même logique que précédemment)
        List<Long> tacheIds = dtos.stream()
            .map(TasksDTO::getId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        if (tacheIds.isEmpty()) {
            return dtos;
        }

        List<ResponsableTache> responsablesEntities = responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) responsablesEntities = Collections.emptyList();

        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers)
            .filter(Objects::nonNull)
            .map(m -> m.trim().toLowerCase())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();

        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur lors de l'appel OrgaCare pour noms responsables: {}", e.getMessage());
            deptNamesByCode = deptNamesByCode == null ? Collections.emptyMap() : deptNamesByCode;
            personNamesByMatricule = personNamesByMatricule == null ? Collections.emptyMap() : personNamesByMatricule;
        }

        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        for (TasksDTO dto : dtos) {
            List<ResponsableTache> listEnt = responsablesByTache.getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();

            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());

                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    String code = ent.getCodeDept().trim();
                    String nomDept = deptNamesByCode.get(code);
                    rDto.setDeptName(nomDept);
                }

                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    String mat = ent.getMatriculePers().trim().toLowerCase();
                    String nomPers = personNamesByMatricule.get(mat);
                    rDto.setPersonneName(nomPers);
                }

                rDtos.add(rDto);
            }

            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:" + k.getCodeDept()) + "|" + (k.getMatriculePers() == null ? "" : "P:" + k.getMatriculePers()),
                        v -> v,
                        (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));

            dto.setResponsables(unique);
        }

        return dtos;
    }

    /**
     * Recherche avancée de tâches pour un utilisateur (via ACL) avec filtres multi-critères :
     * - subject    : sous-chaîne insensible à la casse sur corresp.subject
     * - responsible: sous-chaîne insensible à la casse sur le NOM du responsable (dept ou personne)
     * - reference  : sous-chaîne insensible à la casse sur corresp.reference
     * - creationDateFrom / creationDateTo : plage sur tasks.creationDate
     * - dueDateFrom / dueDateTo           : plage sur tasks.dueDate
     *
     * La recherche porte uniquement sur les tâches visibles par l'utilisateur (ACL).
     */
    @Transactional(readOnly = true)
    public List<TasksDTO> searchAdvancedTasksByUserAcl(
        String userId,
        String subject,
        String responsible,
        String reference,
        ZonedDateTime creationDateFrom,
        ZonedDateTime creationDateTo,
        ZonedDateTime dueDateFrom,
        ZonedDateTime dueDateTo
    ) {
        if (userId == null) {
            return Collections.emptyList();
        }

        // 1) Récupérer les SIDs de l'utilisateur (deptCodes + login)
        List<String> deptCodes;
        try {
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
            deptCodes = (affectations == null || affectations.isEmpty()) ? new ArrayList<>()
                : affectations.stream()
                .map(a -> a.get("departementCode"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Impossible de récupérer les deptCodes pour userId {} : {}", userId, e.getMessage());
            deptCodes = new ArrayList<>();
        }

        String userLogin = getUserLogin(userId);
        List<String> allSids = new ArrayList<>(deptCodes);
        if (userLogin != null && !allSids.contains(userLogin)) {
            allSids.add(userLogin);
        }

        if (allSids.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) Préparer les filtres SQL (null si non renseigné = pas de filtre en DB)
        String subjectFilter    = (subject   != null && !subject.trim().isEmpty())   ? subject.trim()   : null;
        String referenceFilter  = (reference != null && !reference.trim().isEmpty()) ? reference.trim() : null;

        int readMask = BasePermission.READ.getMask();
        List<Tasks> tasks = tasksRepository.findAllByAclForSidsAndCorrespFilters(
            allSids, readMask, subjectFilter, referenceFilter
        );
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) Mapper en DTO
        List<TasksDTO> dtos = tasks.stream().map(tasksMapper::toDto).collect(Collectors.toList());

        // 4) Enrichir les responsables (batch)
        List<Long> tacheIds = dtos.stream()
            .map(TasksDTO::getId).filter(Objects::nonNull).distinct()
            .collect(Collectors.toList());

        List<ResponsableTache> responsablesEntities = tacheIds.isEmpty()
            ? Collections.emptyList()
            : responsableTacheService.findByTacheIdIn(tacheIds);
        if (responsablesEntities == null) responsablesEntities = Collections.emptyList();

        Set<String> allCodeDepts = responsablesEntities.stream()
            .map(ResponsableTache::getCodeDept).filter(Objects::nonNull).map(String::trim)
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        Set<String> allMatricules = responsablesEntities.stream()
            .map(ResponsableTache::getMatriculePers).filter(Objects::nonNull).map(String::trim)
            .filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        Map<String, String> deptNamesByCode = Collections.emptyMap();
        Map<String, String> personNamesByMatricule = Collections.emptyMap();
        try {
            if (!allCodeDepts.isEmpty()) {
                deptNamesByCode = orgaCareRestClient.getDepartementNamesByCodes(new ArrayList<>(allCodeDepts));
                if (deptNamesByCode == null) deptNamesByCode = Collections.emptyMap();
            }
            if (!allMatricules.isEmpty()) {
                personNamesByMatricule = orgaCareRestClient.getPersonneNamesByMatricules(new ArrayList<>(allMatricules));
                if (personNamesByMatricule == null) personNamesByMatricule = Collections.emptyMap();
            }
        } catch (Exception e) {
            log.warn("Erreur OrgaCare lors de la résolution des noms responsables: {}", e.getMessage());
        }

        Map<Long, List<ResponsableTache>> responsablesByTache = responsablesEntities.stream()
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        // copies finales pour les lambdas
        final Map<String, String> finalDeptNames = deptNamesByCode;
        final Map<String, String> finalPersonNames = personNamesByMatricule;

        for (TasksDTO dto : dtos) {
            List<ResponsableTache> listEnt = responsablesByTache.getOrDefault(dto.getId(), Collections.emptyList());
            List<ResponsableTacheDTO> rDtos = new ArrayList<>();
            for (ResponsableTache ent : listEnt) {
                ResponsableTacheDTO rDto = new ResponsableTacheDTO();
                rDto.setId(ent.getId());
                rDto.setTacheId(ent.getTacheId());
                rDto.setCodeDept(ent.getCodeDept());
                rDto.setMatriculePers(ent.getMatriculePers());
                if (ent.getCodeDept() != null && !ent.getCodeDept().trim().isEmpty()) {
                    rDto.setDeptName(finalDeptNames.get(ent.getCodeDept().trim()));
                }
                if (ent.getMatriculePers() != null && !ent.getMatriculePers().trim().isEmpty()) {
                    rDto.setPersonneName(finalPersonNames.get(ent.getMatriculePers().trim()));
                }
                rDtos.add(rDto);
            }
            List<ResponsableTacheDTO> unique = rDtos.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        k -> (k.getCodeDept() == null ? "" : "D:" + k.getCodeDept())
                            + "|" + (k.getMatriculePers() == null ? "" : "P:" + k.getMatriculePers()),
                        v -> v, (a, b) -> a
                    ),
                    m -> new ArrayList<>(m.values())
                ));
            dto.setResponsables(unique);
        }

        // 5) Filtres post-SQL : responsable (nom), creationDate, dueDate
        String responsableFilter = (responsible != null && !responsible.trim().isEmpty())
            ? responsible.trim().toLowerCase() : null;

        return dtos.stream().filter(dto -> {
            // filtre par nom responsable (contains, insensible casse)
            if (responsableFilter != null) {
                boolean matched = false;
                if (dto.getResponsables() != null) {
                    for (ResponsableTacheDTO r : dto.getResponsables()) {
                        String deptName = r.getDeptName();
                        String personName = r.getPersonneName();
                        if ((deptName != null && deptName.toLowerCase().contains(responsableFilter))
                            || (personName != null && personName.toLowerCase().contains(responsableFilter))) {
                            matched = true;
                            break;
                        }
                    }
                }
                if (!matched) return false;
            }
            // filtre par date de création
            if (creationDateFrom != null && dto.getCreationDate() != null
                && dto.getCreationDate().isBefore(creationDateFrom)) return false;
            if (creationDateTo != null && dto.getCreationDate() != null
                && dto.getCreationDate().isAfter(creationDateTo)) return false;
            // filtre par date limite
            if (dueDateFrom != null && dto.getDueDate() != null
                && dto.getDueDate().isBefore(dueDateFrom)) return false;
            if (dueDateTo != null && dto.getDueDate() != null
                && dto.getDueDate().isAfter(dueDateTo)) return false;
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Sociétés de l'utilisateur connecté, récupérées depuis OrgaCare (Feign).
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMesSocietes() {
        try {
            List<Map<String, Object>> societes = orgaCareRestClient.getMesSocietes();
            return societes != null ? societes : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Impossible de récupérer les sociétés de l'utilisateur depuis OrgaCare : {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Arbre des départements de l'utilisateur (son département + ses sous-départements)
     * dans l'organigramme donné, récupéré depuis OrgaCare.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMesDepartementsTree(String userId, String orgaCode) {
        if (userId == null || userId.trim().isEmpty() || orgaCode == null || orgaCode.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<Map<String, Object>> tree = orgaCareRestClient.getMyDepartementTree(orgaCode.trim(), userId.trim());
            return tree != null ? tree : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Impossible de récupérer l'arbre des départements (userId={}, orga={}) : {}", userId, orgaCode, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public boolean canCurrentUserModifyTask(Long taskId) {
        if (taskId == null) {
            return false;
        }

        Optional<Tasks> opt = tasksRepository.findById(taskId);
        if (opt.isEmpty()) {
            return false;
        }

        return canCurrentUserModifyTask(opt.get());
    }

    @Transactional(readOnly = true)
    public boolean canCurrentUserModifyTask(Tasks task) {
        if (task == null) {
            return false;
        }

        String createdBy = task.getCreatedBy();
        if (!StringUtils.hasText(createdBy)) {
            return false;
        }

        String currentLogin = com.africom.correspmanage.security.SecurityUtils.getCurrentUserLogin().orElse(null);
        if (!StringUtils.hasText(currentLogin)) {
            return false;
        }

        // normaliser : trim + ignore case
        return currentLogin.trim().equalsIgnoreCase(createdBy.trim());
    }

    /**
     * Retourne le login de l'utilisateur courant (en lowercase) ou null si non trouvé.
     */
    private String getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername().toLowerCase();
        } else if (principal instanceof String) {
            return ((String) principal).toLowerCase();
        } else {
            return null;
        }
    }

}
