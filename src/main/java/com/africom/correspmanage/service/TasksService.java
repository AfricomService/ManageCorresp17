package com.africom.correspmanage.service;

import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.TasksRepository;
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

    public TasksService(
        TasksRepository tasksRepository,
        TasksMapper tasksMapper,
        CorrespRepository correspRepository,
        OrgaCareRestClient orgaCareRestClient
    ) {
        this.tasksRepository = tasksRepository;
        this.tasksMapper = tasksMapper;
        this.correspRepository = correspRepository;
        this.orgaCareRestClient = orgaCareRestClient;
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
     * Nécessite que OrgaCareRestClient expose une méthode getDepartementCodesByUserId(Long userId).
     */
    public List<TasksDTO> getAllTasksByUserAcl(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        List<String> deptCodes;
        try {
            // nouvel appel : récupère une List<Map<String, Object>> où chaque map contient
            // "departementCode", "departementName", "types", ...
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);

            if (affectations == null || affectations.isEmpty()) {
                return Collections.emptyList();
            }

            // extraire seulement les departementCode (comme le code précédent attendait déjà une List<String>)
            deptCodes = affectations.stream()
                .map(a -> {
                    Object codeObj = a.get("departementCode");
                    return codeObj == null ? null : codeObj.toString();
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Impossible de récupérer les codes des départements pour userId {} : {}", userId, e.getMessage());
            return Collections.emptyList();
        }

        // Récupère les tasks accessibles par ces départements
        List<TasksDTO> all = getAllTasksByAcl(deptCodes);

        // Filtre pour ne garder que les tâches IN_PROGRESS (même logique qu'avant)
        return all.stream()
            .filter(t -> t.getStatus() != null && "IN_PROGRESS".equals(t.getStatus().toString()))
            .collect(Collectors.toList());
    }

}
