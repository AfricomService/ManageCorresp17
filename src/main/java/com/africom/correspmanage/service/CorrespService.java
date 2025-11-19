package com.africom.correspmanage.service;

import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.client.TypeDocumentFeignDTO;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import com.africom.correspmanage.domain.enumeration.DispatchType;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.PjRepository;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.dto.CorrespDTO;
import com.africom.correspmanage.service.dto.FileUploadDTO;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.service.mapper.CorrespMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link Corresp}.
 */
@Service
@Transactional
public class CorrespService {

    private final Logger log = LoggerFactory.getLogger(CorrespService.class);

    private final CorrespRepository correspRepository;

    private final CorrespMapper correspMapper;

    private final TasksRepository tasksRepository;

    private final PjRepository pjRepository;
    private final AclUtilService aclUtilService;
    private final OrgaCareRestClient orgaCareRestClient;

    public CorrespService(
        CorrespRepository correspRepository,
        CorrespMapper correspMapper,
        TasksRepository tasksRepository,
        PjService pjService,
        PjRepository pjRepository,
        AclUtilService aclUtilService,
        OrgaCareRestClient orgaCareRestClient
    ) {
        this.correspRepository = correspRepository;
        this.correspMapper = correspMapper;
        this.tasksRepository = tasksRepository;
        this.pjRepository = pjRepository;
        this.aclUtilService = aclUtilService;
        this.orgaCareRestClient = orgaCareRestClient;
    }

    /**
     * Save a corresp.
     *
     * @param correspDTO the entity to save.
     * @return the persisted entity.
     */
    public CorrespDTO save(CorrespDTO correspDTO) {
        log.debug("Request to save Corresp : {}", correspDTO);
        Corresp corresp = correspMapper.toEntity(correspDTO);
        corresp = correspRepository.save(corresp);
        return correspMapper.toDto(corresp);
    }

    /**
     * Update a corresp.
     *
     * @param correspDTO the entity to save.
     * @return the persisted entity.
     */
    public CorrespDTO update(CorrespDTO correspDTO) {
        log.debug("Request to update Corresp : {}", correspDTO);
        Corresp corresp = correspMapper.toEntity(correspDTO);
        corresp = correspRepository.save(corresp);
        return correspMapper.toDto(corresp);
    }

    /**
     * Partially update a corresp.
     *
     * @param correspDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CorrespDTO> partialUpdate(CorrespDTO correspDTO) {
        log.debug("Request to partially update Corresp : {}", correspDTO);

        return correspRepository
            .findById(correspDTO.getId())
            .map(existingCorresp -> {
                correspMapper.partialUpdate(existingCorresp, correspDTO);

                return existingCorresp;
            })
            .map(correspRepository::save)
            .map(correspMapper::toDto);
    }

    /**
     * Get all the corresps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CorrespDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Corresps");
        return correspRepository.findAll(pageable).map(correspMapper::toDto);
    }

    /**
     * Get all the corresps with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CorrespDTO> findAllWithEagerRelationships(Pageable pageable) {
        return correspRepository.findAllWithEagerRelationships(pageable).map(correspMapper::toDto);
    }

    /**
     * Get one corresp by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CorrespDTO> findOne(Long id) {
        log.debug("Request to get Corresp : {}", id);
        return correspRepository.findOneWithEagerRelationships(id).map(correspMapper::toDto);
    }

    /**
     * Delete the corresp by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Corresp : {}", id);
        correspRepository.deleteById(id);
    }

    public Page<CorrespDTO> findAllCorrespByType(String correspType, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by("id").descending());

        switch (correspType) {
            case "INCOMING":
                return correspRepository.findAllByCorresptype(CorrespType.INCOMING, pageable).map(correspMapper::toDto);
            case "OUTGOING":
                return correspRepository.findAllByCorresptype(CorrespType.OUTGOING, pageable).map(correspMapper::toDto);
            case "INTERNAL":
                return correspRepository.findAllByCorresptype(CorrespType.INTERNAL, pageable).map(correspMapper::toDto);
            default:
                throw new IllegalArgumentException("Unknown correspondence type: " + correspType);
        }
    }

    public Page<CorrespDTO> findAllCorrespByFilter(
        String reference,
        String status,
        String dispatchType,
        String startDate,
        String endDate,
        int pageIndex,
        int pageSize
    ) {
        List<Predicate> predicates = new ArrayList<>();

        Specification<Corresp> spec = (root, query, criteriaBuilder) -> {
            if (reference != null && !reference.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("reference")), "%" + reference.toLowerCase() + "%"));
            }

            if (status != null && !status.isEmpty()) {
                switch (status.toUpperCase()) {
                    case "DRAFT":
                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.DRAFT));
                        break;
                    case "IN_PROGRESS":
                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.IN_PROGRESS));
                        break;
                    case "CLOSED":
                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.CLOSED));
                        break;
                    case "CANCELLED":
                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.CANCELLED));
                        break;
                    default:
                        break;
                }
            }

            if (dispatchType != null && !dispatchType.isEmpty()) {
                switch (dispatchType.toUpperCase()) {
                    case "LETTER":
                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.LETTER));
                        break;
                    case "REGISTERED_LETTER":
                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.REGISTERED_LETTER));
                        break;
                    case "EMAIL":
                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.EMAIL));
                        break;
                    case "FAX":
                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.FAX));
                        break;
                    default:
                        break;
                }
            }

            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                //                log.debug("THIS IS THE DATE {}",dateCreation.substring(0,10));

                LocalDate dateStart = LocalDate.parse(startDate.substring(0, 10));
                int yearStart = dateStart.getYear();
                int monthStart = dateStart.getMonthValue();
                int dayStart = dateStart.getDayOfMonth();

                LocalDate dateEnd = LocalDate.parse(endDate.substring(0, 10));
                int yearEnd = dateEnd.getYear();
                int monthEnd = dateEnd.getMonthValue();
                int dayEnd = dateEnd.getDayOfMonth();

                ZonedDateTime startDateZoned = ZonedDateTime.of(yearStart, monthStart, dayStart, 0, 0, 0, 0, ZoneId.of("UTC"));
                ZonedDateTime endDateZoned = ZonedDateTime.of(yearEnd, monthEnd, dayEnd, 23, 59, 59, 999999999, ZoneId.of("UTC"));

                predicates.add(criteriaBuilder.between(root.get("courrierDate"), startDateZoned, endDateZoned));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by("id").descending());

        return correspRepository.findAll(spec, pageable).map(correspMapper::toDto);
    }

    public Page<CorrespDTO> findAllCorresps(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by("id").descending());

        return correspRepository.findAll(pageable).map(correspMapper::toDto);
    }

    public Corresp saveCorrespWithTasksAndPj(
        CorrespDTO correspDTO,
        List<TasksDTO> tasksDTOS,
        List<Pj> pjs,
        TypeDocumentFeignDTO typeDocumentDTO
    ) {
        log.debug("correspDTO {}", correspDTO);
        log.debug("tasksDTOS {}", tasksDTOS);
        log.debug("pjs {}", pjs);
        log.debug("type {}", typeDocumentDTO);

        List<Tasks> tasksList = new ArrayList<>();

        for (TasksDTO tasksDTO : tasksDTOS) {
            Tasks tasks = new Tasks();
            tasks.setResponsible(tasksDTO.getResponsible());
            tasks.setStatus(CorrespStatus.DRAFT);
            tasks.setTaskType(tasksDTO.getTaskType());
            tasks.setDueDate(tasksDTO.getDueDate());
            tasks.createdBy(SecurityUtils.getCurrentUserLogin().get());
            tasks.creationDate(ZonedDateTime.now());
            tasks.setStartDate(ZonedDateTime.now());

            StringBuilder objectBuilder = new StringBuilder();

            objectBuilder
                .append(tasksDTO.getResponsible())
                .append(" effectuera la tâche de ")
                .append(tasksDTO.getTaskType() != null ? tasksDTO.getTaskType().toUpperCase() : "N/A");

            if (tasksDTO.getDueDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = tasksDTO.getDueDate().format(formatter);
                objectBuilder.append(" en ne dépassant pas la date limite du ").append(formattedDate);
            }

            tasks.setObject(objectBuilder.toString());

            tasksList.add(tasks);
        }

        List<Pj> pjsList = new ArrayList<>();

        for (Pj pjDTO : pjs) {
            Pj pjEntity = new Pj();
            pjEntity.setName(pjDTO.getName());
            pjEntity.setFileType(pjDTO.getFileType());
            pjEntity.setUniqueName(pjDTO.getUniqueName());
            pjEntity.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
            pjEntity.setCreationDate(ZonedDateTime.now());
            pjEntity.setPath(pjDTO.getPath());

            pjsList.add(pjEntity);
        }

        Set<Tasks> tasksSaved = new HashSet<>(tasksRepository.saveAll(tasksList));
        Set<Pj> pjsSaved = new HashSet<>(pjRepository.saveAll(pjsList));

        Corresp correspToSave = new Corresp();

        UUID uuid = UUID.randomUUID();
        correspToSave.setIdinstance(uuid);
        correspToSave.setCourrierDate(correspDTO.getCourrierDate());
        correspToSave.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        correspToSave.setDateCreated(ZonedDateTime.now());
        correspToSave.setSubject(correspDTO.getSubject());
        correspToSave.setSender(correspDTO.getSender());
        correspToSave.setContact(correspDTO.getContact());
        correspToSave.setContactEmail(correspDTO.getContactEmail());
        correspToSave.setContactEmailCc(correspDTO.getContactEmailCc());
        correspToSave.setResponseTo(correspDTO.getResponseTo());
        correspToSave.setSenderReference(correspDTO.getSenderReference());
        correspToSave.setPriority(correspDTO.getPriority());
        correspToSave.setStatus(CorrespStatus.DRAFT);
        correspToSave.setBody(correspDTO.getBody());
        correspToSave.setDispatchType(correspDTO.getDispatchType());
        correspToSave.setCorresptype(correspDTO.getCorresptype());
        correspToSave.setTypeDocumentId(typeDocumentDTO.getId());

        correspToSave.setTasks(tasksSaved);
        correspToSave.setAttachments(pjsSaved);

        Corresp saved = correspRepository.save(correspToSave);

        List<String> departementsToReadWrite = orgaCareRestClient.getDepartementAndParentsCodes(typeDocumentDTO.getDefautdest());

        aclUtilService.createAcl(saved);

        departementsToReadWrite.forEach(departement -> {
            aclUtilService.addPermission(saved, departement, BasePermission.READ);
            aclUtilService.addPermission(saved, departement, BasePermission.WRITE);
        });

        log.debug("save corresp {}", saved);

        return saved;
    }

    public List<Corresp> getListByAcl(String deptCode) {
        return correspRepository.findAllByAclForDept(deptCode);
    }

    public Corresp addTaskToExistingCorresp(TasksDTO tasksDTO, Long correspId) {
        Corresp existingCorresp = correspRepository.findById(correspId).get();

        Tasks tasks = new Tasks();
        tasks.setResponsible(tasksDTO.getResponsible());
        tasks.setStatus(CorrespStatus.DRAFT);
        tasks.setTaskType(tasksDTO.getTaskType());
        tasks.setDueDate(tasksDTO.getDueDate());
        tasks.createdBy(SecurityUtils.getCurrentUserLogin().get());
        tasks.creationDate(ZonedDateTime.now());
        tasks.setStartDate(ZonedDateTime.now());

        StringBuilder objectBuilder = new StringBuilder();

        objectBuilder
            .append(tasksDTO.getResponsible())
            .append(" effectuera la tâche de ")
            .append(tasksDTO.getTaskType() != null ? tasksDTO.getTaskType().toUpperCase() : "N/A");

        if (tasksDTO.getDueDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = tasksDTO.getDueDate().format(formatter);
            objectBuilder.append(" en ne dépassant pas la date limite du ").append(formattedDate);
        }

        tasks.setObject(objectBuilder.toString());

        Set<Tasks> tasksList = existingCorresp.getTasks();

        Tasks savedTask = tasksRepository.save(tasks);

        tasksList.add(savedTask);

        existingCorresp.setTasks(tasksList);

        return correspRepository.save(existingCorresp);
    }

    public Corresp changeTaskStatus(Long correspId, Long taskId, String statusTo) {
        Corresp existingCorresp = correspRepository.findById(correspId).orElseThrow(() -> new RuntimeException("Corresp not found"));

        CorrespStatus newStatus;
        try {
            newStatus = CorrespStatus.valueOf(statusTo);
        } catch (IllegalArgumentException e) {
            newStatus = CorrespStatus.DRAFT;
        }

        Tasks taskToUpdate = existingCorresp
            .getTasks()
            .stream()
            .filter(t -> t.getId().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Task not found in this corresp"));

        taskToUpdate.setStatus(newStatus);

        if (statusTo.equals("CLOSED")) {
            taskToUpdate.setCloseDate(ZonedDateTime.now());
        }

        if (statusTo.equals("IN_PROGRESS")) {
            taskToUpdate.setStartDate(ZonedDateTime.now());
        }

        tasksRepository.save(taskToUpdate);

        return existingCorresp;
    }
}
