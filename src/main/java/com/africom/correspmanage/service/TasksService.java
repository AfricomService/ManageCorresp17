package com.africom.correspmanage.service;

import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.ResponsableTache;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.service.mapper.TasksMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public TasksService(
        TasksRepository tasksRepository,
        TasksMapper tasksMapper,
        CorrespRepository correspRepository,
        OrgaCareRestClient orgaCareRestClient,
        ResponsableTacheService responsableTacheService
    ) {
        this.tasksRepository = tasksRepository;
        this.tasksMapper = tasksMapper;
        this.correspRepository = correspRepository;
        this.orgaCareRestClient = orgaCareRestClient;
        this.responsableTacheService = responsableTacheService;
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

    public List<TasksDTO> getAllTasksByCorrespId(Long correspId) {
        log.debug("Request to get all Tasks by correspId : {}", correspId);

        return tasksRepository
            .findTasksByCorrespId(correspId)
            .stream()
            .map(task -> {
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

                try {
                    String responsableName = orgaCareRestClient.getDepartementNomByCode(task.getResponsible());
                    dto.setResponsableName(responsableName);
                } catch (Exception e) {
                    log.warn("Could not fetch responsible name for code {}", task.getResponsible());
                }

                return dto;
            })
            .toList();
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
    public List<TasksDTO> getAllTasksByUserAcl(Long userId, CorrespStatus status) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<String> deptCodes;
        try {
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
            if (affectations == null || affectations.isEmpty()) {
                return Collections.emptyList();
            }
            deptCodes = affectations.stream()
                .map(a -> a.get("departementCode"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Impossible de récupérer les codes des départements pour userId {} : {}", userId, e.getMessage());
            return Collections.emptyList();
        }

        if (deptCodes.isEmpty()) {
            return Collections.emptyList();
        }

        // Récupère les tasks accessibles par ces départements
        List<TasksDTO> all = getAllTasksByAcl(deptCodes);
        if (all == null || all.isEmpty()) {
            return Collections.emptyList();
        }

        // Si aucun status fourni, garder le comportement précédent (IN_PROGRESS)
        CorrespStatus filterStatus = (status == null) ? CorrespStatus.IN_PROGRESS : status;

        // Filtrer par status demandé
        List<TasksDTO> filtered = all.stream()
            .filter(t -> t.getStatus() != null && filterStatus.equals(t.getStatus()))
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
            .map(String::trim)
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
                    String mat = ent.getMatriculePers().trim();
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


    @Transactional(readOnly = true)
    public List<TasksDTO> getAllTasksByUserAclAllStatuses(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<String> deptCodes;
        try {
            // récupère affectations (List<Map<String,Object>>) et extrait les departementCode
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);

            if (affectations == null || affectations.isEmpty()) {
                return Collections.emptyList();
            }

            deptCodes = affectations.stream()
                .map(a -> a.get("departementCode"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Impossible de récupérer les codes des départements pour userId {} : {}", userId, e.getMessage());
            return Collections.emptyList();
        }

        if (deptCodes.isEmpty()) {
            return Collections.emptyList();
        }

        // Récupère les tasks accessibles par ces départements (TOUTES les tâches, aucun filtre sur status)
        List<TasksDTO> all = getAllTasksByAcl(deptCodes);
        if (all == null || all.isEmpty()) {
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
            .map(String::trim)
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
                    String mat = ent.getMatriculePers().trim();
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


}
