package com.africom.correspmanage.service;

import com.africom.correspmanage.client.*;
import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import com.africom.correspmanage.domain.enumeration.DispatchType;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.MessageRepository;
import com.africom.correspmanage.repository.PjRepository;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.dto.*;
import com.africom.correspmanage.service.mapper.CorrespMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
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
    private final ConfigManageRestClient configManageRestClient;
    private final ApplicationProperties applicationProperties;
    private final MessageRepository messageRepository;
    private final ResponsableTacheService responsableTacheService;
    private final CorrespGatewayRestClient correspGatewayRestClient;
    private final ContactCopieCorrespService contactCopieCorrespService;


    public CorrespService(
        CorrespRepository correspRepository,
        CorrespMapper correspMapper,
        TasksRepository tasksRepository,
        PjService pjService,
        PjRepository pjRepository,
        AclUtilService aclUtilService,
        OrgaCareRestClient orgaCareRestClient,
        ConfigManageRestClient configManageRestClient,
        ApplicationProperties applicationProperties,
        MessageRepository messageRepository,
        ResponsableTacheService responsableTacheService,
        CorrespGatewayRestClient correspGatewayRestClient,
        ContactCopieCorrespService contactCopieCorrespService

    ) {
        this.correspRepository = correspRepository;
        this.correspMapper = correspMapper;
        this.tasksRepository = tasksRepository;
        this.pjRepository = pjRepository;
        this.aclUtilService = aclUtilService;
        this.orgaCareRestClient = orgaCareRestClient;
        this.configManageRestClient = configManageRestClient;
        this.applicationProperties = applicationProperties;
        this.messageRepository = messageRepository;
        this.responsableTacheService = responsableTacheService;
        this.correspGatewayRestClient = correspGatewayRestClient;
        this.contactCopieCorrespService = contactCopieCorrespService;

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

//    public Page<CorrespDTO> findAllCorrespByFilter(
//        String reference,
//        String status,
//        String dispatchType,
//        String startDate,
//        String endDate,
//        int pageIndex,
//        int pageSize
//    ) {
//        List<Predicate> predicates = new ArrayList<>();
//
//        Specification<Corresp> spec = (root, query, criteriaBuilder) -> {
//            if (reference != null && !reference.isEmpty()) {
//                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("reference")), "%" + reference.toLowerCase() + "%"));
//            }
//
//            if (status != null && !status.isEmpty()) {
//                switch (status.toUpperCase()) {
//                    case "DRAFT":
//                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.DRAFT));
//                        break;
//                    case "IN_PROGRESS":
//                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.IN_PROGRESS));
//                        break;
//                    case "CLOSED":
//                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.CLOSED));
//                        break;
//                    case "CANCELLED":
//                        predicates.add(criteriaBuilder.equal(root.get("status"), CorrespStatus.CANCELLED));
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            if (dispatchType != null && !dispatchType.isEmpty()) {
//                switch (dispatchType.toUpperCase()) {
//                    case "LETTER":
//                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.LETTER));
//                        break;
//                    case "REGISTERED_LETTER":
//                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.REGISTERED_LETTER));
//                        break;
//                    case "EMAIL":
//                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.EMAIL));
//                        break;
//                    case "FAX":
//                        predicates.add(criteriaBuilder.equal(root.get("dispatchType"), DispatchType.FAX));
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
//                //                log.debug("THIS IS THE DATE {}",dateCreation.substring(0,10));
//
//                LocalDate dateStart = LocalDate.parse(startDate.substring(0, 10));
//                int yearStart = dateStart.getYear();
//                int monthStart = dateStart.getMonthValue();
//                int dayStart = dateStart.getDayOfMonth();
//
//                LocalDate dateEnd = LocalDate.parse(endDate.substring(0, 10));
//                int yearEnd = dateEnd.getYear();
//                int monthEnd = dateEnd.getMonthValue();
//                int dayEnd = dateEnd.getDayOfMonth();
//
//                ZonedDateTime startDateZoned = ZonedDateTime.of(yearStart, monthStart, dayStart, 0, 0, 0, 0, ZoneId.of("UTC"));
//                ZonedDateTime endDateZoned = ZonedDateTime.of(yearEnd, monthEnd, dayEnd, 23, 59, 59, 999999999, ZoneId.of("UTC"));
//
//                predicates.add(criteriaBuilder.between(root.get("courrierDate"), startDateZoned, endDateZoned));
//            }
//
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        };
//
//        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by("id").descending());
//
//        return correspRepository.findAll(spec, pageable).map(correspMapper::toDto);
//    }
public Page<CorrespDTO> findAllCorrespByFilter(
    Long userId,
    String reference,
    String status,
    String dispatchType,
    String startDate,
    String endDate,
    int pageIndex,
    int pageSize
) {
    // 1) Résoudre les SIDs de l'utilisateur (deptCodes + login)
    List<String> deptCodes = new ArrayList<>();
    String userLogin = "";

    if (userId != null) {
        try {
            List<Map<String, Object>> affectations = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
            if (affectations != null && !affectations.isEmpty()) {
                deptCodes = affectations.stream()
                    .map(a -> a.get("departementCode"))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .distinct()
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer les deptCodes pour userId {} : {}", userId, e.getMessage());
        }

        try {
            Map<String, Object> userMap = correspGatewayRestClient.getUserById(userId);
            if (userMap != null) {
                Object login = userMap.get("login");
                if (login != null && !login.toString().trim().isEmpty()) {
                    userLogin = login.toString().trim().toLowerCase();
                }
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer le login pour userId {} : {}", userId, e.getMessage());
        }
    }

    List<String> allSids = new ArrayList<>(deptCodes);
    if (!userLogin.isEmpty() && !allSids.contains(userLogin)) {
        allSids.add(userLogin);
    }
    // Eviter liste vide (SQL IN vide = erreur)
    if (allSids.isEmpty()) {
        allSids.add("__NO_SID__");
    }

    int readMask = BasePermission.READ.getMask();
    Pageable pageable = PageRequest.of(Math.max(0, pageIndex - 1), pageSize);
    // 2) Récupérer tous les IDs candidats (ACL + DRAFT + DISPATCH)
    Page<Number> idsPage = correspRepository.findAllIdsByAclOrDraftOrDispatch(
        allSids, readMask, userLogin, pageable
    );

    if (idsPage.isEmpty()) {
        return Page.empty(pageable);
    }

    List<Long> ids = idsPage.getContent().stream()
        .map(Number::longValue)
        .collect(Collectors.toList());

    List<Corresp> corresps = correspRepository.findAllByIds(ids);

    // 3) Maintenir l'ordre
    Map<Long, Corresp> correspMap = corresps.stream()
        .collect(Collectors.toMap(Corresp::getId, c -> c));
    List<Corresp> ordered = ids.stream()
        .map(correspMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    // 4) Appliquer les filtres en mémoire (reference, status, dispatchType, dates)
    List<Corresp> filtered = ordered.stream().filter(c -> {
        if (reference != null && !reference.isEmpty()) {
            if (c.getReference() == null || !c.getReference().toLowerCase().contains(reference.toLowerCase())) {
                return false;
            }
        }
        if (status != null && !status.isEmpty()) {
            try {
                CorrespStatus cs = CorrespStatus.valueOf(status.toUpperCase());
                if (!cs.equals(c.getStatus())) return false;
            } catch (IllegalArgumentException ignored) {}
        }
        if (dispatchType != null && !dispatchType.isEmpty()) {
            try {
                DispatchType dt = DispatchType.valueOf(dispatchType.toUpperCase());
                if (!dt.equals(c.getDispatchType())) return false;
            } catch (IllegalArgumentException ignored) {}
        }
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            try {
                LocalDate dateStart = LocalDate.parse(startDate.substring(0, 10));
                LocalDate dateEnd   = LocalDate.parse(endDate.substring(0, 10));
                ZonedDateTime from = ZonedDateTime.of(dateStart.getYear(), dateStart.getMonthValue(), dateStart.getDayOfMonth(), 0, 0, 0, 0, ZoneId.of("UTC"));
                ZonedDateTime to   = ZonedDateTime.of(dateEnd.getYear(),   dateEnd.getMonthValue(),   dateEnd.getDayOfMonth(),   23, 59, 59, 999999999, ZoneId.of("UTC"));
                if (c.getCourrierDate() == null) return false;
                if (c.getCourrierDate().isBefore(from) || c.getCourrierDate().isAfter(to)) return false;
            } catch (Exception ignored) {}
        }
        return true;
    }).collect(Collectors.toList());

    // 5) Construire la Page avec la liste filtrée
    List<CorrespDTO> dtos = filtered.stream()
        .map(correspMapper::toDto)
        .collect(Collectors.toList());

    return new PageImpl<>(dtos, pageable, idsPage.getTotalElements());
}

    public Page<CorrespDTO> findAllCorresps(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by("id").descending());

        return correspRepository.findAll(pageable).map(correspMapper::toDto);
    }
    @Transactional
    public Corresp saveCorrespWithTasksAndPj(
        CorrespDTO correspDTO,
        List<TasksDTO> tasksDTOS,
        List<Pj> pjs,
        TypeDocumentFeignDTO typeDocumentDTO,
        boolean withRef
    ) {
        log.debug("correspDTO {}", correspDTO);
        log.debug("tasksDTOS {}", tasksDTOS);
        log.debug("pjs {}", pjs);
        log.debug("type {}", typeDocumentDTO);

        // Collection pour stocker les Tasks persistées
        Set<Tasks> tasksSaved = new HashSet<>();

        // --- Création des Tasks : pour chaque TasksDTO -> si plusieurs responsables -> une tâche par responsable
        if (tasksDTOS != null) {
            final int MAX_CREATED = 500; // sécurité si beaucoup de responsables
            int createdCount = 0;

            for (TasksDTO tasksDTO : tasksDTOS) {
                // récupérer la liste de responsables (nouveau champ)
                List<ResponsableTacheDTO> inputResponsables = tasksDTO.getResponsables();

                // fallback : si pas de liste, essaie le champ legacy "responsible" (string)
                if ((inputResponsables == null || inputResponsables.isEmpty()) && tasksDTO.getResponsible() != null) {
                    String raw = tasksDTO.getResponsible();
                    if (raw != null && !raw.trim().isEmpty()) {
                        String[] parts = raw.split(",");
                        inputResponsables = new ArrayList<>();
                        for (String p : parts) {
                            String s = p.trim();
                            if (s.isEmpty()) continue;
                            ResponsableTacheDTO r = new ResponsableTacheDTO();
                            if (s.startsWith("P:")) {
                                r.setMatriculePers(s.substring(2));
                            } else if (s.startsWith("D:")) {
                                r.setCodeDept(s.substring(2));
                            } else if (s.toUpperCase().startsWith("DEPT") || s.toUpperCase().contains("DEPT")) {
                                r.setCodeDept(s);
                            } else {
                                r.setMatriculePers(s);
                            }
                            inputResponsables.add(r);
                        }
                    }
                }

                // si toujours vide => créer 1 tâche "sans responsable"
                if (inputResponsables == null || inputResponsables.isEmpty()) {
                    Tasks t = new Tasks();
                    t.setResponsible(null);
                    t.setStatus(CorrespStatus.DRAFT);
                    t.setTaskType(tasksDTO.getTaskType());
                    t.setDueDate(tasksDTO.getDueDate());
                    t.createdBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                    t.creationDate(ZonedDateTime.now());
                    t.setStartDate(ZonedDateTime.now());
                    t.setCommentaire(tasksDTO.getCommentaire());
                    StringBuilder objectBuilder = new StringBuilder();
                    objectBuilder.append("Tâche de ").append(t.getTaskType() != null ? t.getTaskType().toUpperCase() : "N/A");
                    if (t.getDueDate() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        objectBuilder.append(" jusqu'au ").append(t.getDueDate().format(formatter));
                    }
                    t.setObject(objectBuilder.toString());

                    Tasks saved = tasksRepository.save(t);
                    tasksSaved.add(saved);
                    createdCount++;
                    if (createdCount > MAX_CREATED) throw new RuntimeException("Trop de tâches créées");
                    continue;
                }

                // normalize & dedupe responsables (key by matricule or codeDept)
                Map<String, ResponsableTacheDTO> map = new LinkedHashMap<>();
                for (ResponsableTacheDTO r : inputResponsables) {
                    if (r == null) continue;
                    String codeDept = r.getCodeDept() != null ? r.getCodeDept().trim() : null;
                    String matricule = r.getMatriculePers() != null ? r.getMatriculePers().trim() : null;
                    if ((codeDept == null || codeDept.isEmpty()) && (matricule == null || matricule.isEmpty())) continue;
                    String key = (matricule != null && !matricule.isEmpty()) ? ("P:" + matricule) : ("D:" + codeDept);
                    map.putIfAbsent(key, r);
                }
                List<ResponsableTacheDTO> distinctResponsables = new ArrayList<>(map.values());

                // sécurité
                if (distinctResponsables.size() + createdCount > MAX_CREATED) {
                    throw new RuntimeException("Trop de responsables fournis (max " + MAX_CREATED + ")");
                }

                // pour chaque responsable -> créer une Tâche séparée et enregistrer le ResponsableTache
                for (ResponsableTacheDTO r : distinctResponsables) {
                    Tasks t = new Tasks();
                    t.setResponsible(null); // on stocke le responsable dans ResponsableTache
                    t.setStatus(CorrespStatus.DRAFT);
                    t.setTaskType(tasksDTO.getTaskType());
                    t.setDueDate(tasksDTO.getDueDate());
                    t.createdBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                    t.creationDate(ZonedDateTime.now());
                    t.setStartDate(ZonedDateTime.now());
                    t.setCommentaire(tasksDTO.getCommentaire());

                    String responsibleDisplay = (r.getMatriculePers() != null && !r.getMatriculePers().isBlank())
                        ? r.getMatriculePers() : (r.getCodeDept() != null ? r.getCodeDept() : "");
                    StringBuilder objectBuilder = new StringBuilder();
                    objectBuilder
                        .append(responsibleDisplay)
                        .append(" effectuera la tâche de ")
                        .append(tasksDTO.getTaskType() != null ? tasksDTO.getTaskType().toUpperCase() : "N/A");
                    if (tasksDTO.getDueDate() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        objectBuilder.append(" en ne dépassant pas la date limite du ").append(tasksDTO.getDueDate().format(formatter));
                    }
                    t.setObject(objectBuilder.toString());

                    Tasks savedTask = tasksRepository.save(t);

                    // persist le responsable pour la tâche
                    r.setTacheId(savedTask.getId());
                    try {
                        responsableTacheService.saveAllForTask(savedTask.getId(), Collections.singletonList(r));
                    } catch (Exception e) {
                        // rollback si erreur (méthode @Transactional)
                        throw new RuntimeException("Erreur lors de la sauvegarde du responsable pour la tâche " + savedTask.getId(), e);
                    }

                    tasksSaved.add(savedTask);
                    createdCount++;
                } // end loop responsables
            } // end loop tasksDTOS
        } // end if tasksDTOS

        // --- PJs
        Set<Pj> pjsSaved = new HashSet<>();
        if (pjs != null) {
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
            pjsSaved = new HashSet<>(pjRepository.saveAll(pjsList));
        }

        // --- Créer le corresp
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

        // === CHANGEMENT: DispatchType par défaut = LETTER si correspDTO.getDispatchType() est null
        if (correspDTO.getDispatchType() != null) {
            correspToSave.setDispatchType(correspDTO.getDispatchType());
        } else {
            correspToSave.setDispatchType(DispatchType.LETTER);
        }

        correspToSave.setCorresptype(correspDTO.getCorresptype());
        correspToSave.setTypeDocumentId(typeDocumentDTO.getId());
        correspToSave.setLabelDestinataire(correspDTO.getLabelDestinataire());
        correspToSave.setLabelContactCC(correspDTO.getLabelContactCC());

        // === CHANGEMENT: on n'appelle PAS configManageRestClient.getNextNumber(...) ici.
        // Si le DTO contient une référence non vide, on l'utilise ; sinon on laisse la référence null.
        if (correspDTO.getReference() != null && !correspDTO.getReference().isBlank()) {
            correspToSave.setReference(correspDTO.getReference().trim());
        }

        correspToSave.setTasks(tasksSaved);
        correspToSave.setAttachments(pjsSaved);
        correspToSave.setUpdatedDate(ZonedDateTime.now());
        correspToSave.setUpdatedBy(SecurityUtils.getCurrentUserLogin().get());

        // Save with handling of possible unique constraint violation on reference
        Corresp saved;
        try {
            saved = correspRepository.save(correspToSave);
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            // probable violation d'unicité sur la référence
            throw new RuntimeException(
                "Échec de la sauvegarde de la correspondance (contrainte possible sur la référence: "
                    + correspToSave.getReference() + ")",
                dive
            );
        }

        // --- Persister les ContactCopieCorresp si fournis par le front
        // Remarque : correspDTO.getContactCopies() doit être List<ContactCopieCorrespDTO>
        try {
            if (correspDTO.getContactCopies() != null) {
                // appels dans le même transaction => rollback automatique en cas d'erreur
                contactCopieCorrespService.saveAllForCorresp(saved.getId(), correspDTO.getContactCopies());
                log.debug("ContactCopieCorresp sauvegardés pour correspId={}", saved.getId());
            }
        } catch (Exception ex) {
            // Si erreur sur la sauvegarde des copies, on remonte une exception pour rollback complet
            throw new RuntimeException("Erreur lors de la sauvegarde des contacts en copie pour la corresp " + saved.getId(), ex);
        }

        // Body -> fichier / message (inchangé)
        String path = saveBodyToFile(correspDTO.getBody(), saved.getId());
        Message message = new Message();
        message.sender(correspDTO.getSender());
        message.setCorrespId(saved.getId());
        message.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        message.setCreationDate(ZonedDateTime.now());
        message.setUniqueName(saved.getId() + ".html");
        message.setPath(path);
        messageRepository.save(message);

        log.debug("save corresp {}", saved);
        return saved;
    }


    public String removeExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? fileName : fileName.substring(0, index);
    }


    public Corresp saveCorrespWithPjN8N(
        List<PjDTO> pjs,
        String subject,
        String from,
        String bodyHtml,
        Object receiverObj,
        Object copyToObj,
        String statusStr,
        String corresptypeStr,
        String dispatchTypeStr,
        String taskObject,
        String taskType,
        String taskCodeDept
    ) {
        // --- Création de la Corresp ---
        Corresp correspToSave = new Corresp();
        UUID uuid = UUID.randomUUID();
        correspToSave.setIdinstance(uuid);
        correspToSave.setCourrierDate(ZonedDateTime.now());
        correspToSave.setCreatedBy("N8N");
        correspToSave.setDateCreated(ZonedDateTime.now());
        correspToSave.setSubject(subject);
        correspToSave.setSender(from);

        try {
            correspToSave.setStatus(
                statusStr != null ? CorrespStatus.valueOf(statusStr) : CorrespStatus.DISPATCH
            );
        } catch (IllegalArgumentException ex) {
            log.warn("Status invalide reçu: {} — fallback DISPATCH", statusStr);
            correspToSave.setStatus(CorrespStatus.DISPATCH);
        }
        try {
            correspToSave.setCorresptype(
                corresptypeStr != null ? CorrespType.valueOf(corresptypeStr) : CorrespType.INCOMING
            );
        } catch (IllegalArgumentException ex) {
            log.warn("Corresptype invalide reçu: {} — fallback INCOMING", corresptypeStr);
            correspToSave.setCorresptype(CorrespType.INCOMING);
        }
        try {
            correspToSave.setDispatchType(
                dispatchTypeStr != null ? DispatchType.valueOf(dispatchTypeStr) : DispatchType.EMAIL
            );
        } catch (IllegalArgumentException ex) {
            log.warn("DispatchType invalide reçu: {} — fallback EMAIL", dispatchTypeStr);
            correspToSave.setDispatchType(DispatchType.EMAIL);
        }

        // --- Attachments ---
        if (pjs != null && !pjs.isEmpty()) {
            List<Pj> pjsList = new ArrayList<>();
            for (PjDTO pjDTO : pjs) {
                Pj pjEntity = new Pj();
                pjEntity.setName(removeExtension(pjDTO.getName()));
                pjEntity.setFileType(pjDTO.getName().substring(pjDTO.getName().lastIndexOf(".") + 1));
                pjEntity.setUniqueName(pjDTO.getUniqueName());
                pjEntity.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse("N8N"));
                pjEntity.setCreationDate(ZonedDateTime.now());
                pjEntity.setPath(pjDTO.getPath());
                pjsList.add(pjEntity);
            }
            Set<Pj> pjsSaved = new HashSet<>(pjRepository.saveAll(pjsList));
            correspToSave.setAttachments(pjsSaved);
        }

        // --- Création de la tâche avec les données venant de N8N ---
        String effectiveTaskObject = (taskObject != null && !taskObject.isBlank())
            ? taskObject : "tache sans description";
        String effectiveTaskType = (taskType != null && !taskType.isBlank())
            ? taskType : "traitement";
        String effectiveCodeDept = (taskCodeDept != null && !taskCodeDept.isBlank())
            ? taskCodeDept : null;

        Tasks bocTask = new Tasks();
        bocTask.setObject(effectiveTaskObject);
        bocTask.setTaskType(effectiveTaskType);
        bocTask.setCreatedBy("n8n");
        bocTask.setCreationDate(ZonedDateTime.now());
        bocTask.setStartDate(ZonedDateTime.now());
        bocTask.setStatus(CorrespStatus.IN_PROGRESS);

        Tasks savedBocTask = tasksRepository.save(bocTask);

        // --- Responsable de la tâche ---
        if (effectiveCodeDept != null) {
            ResponsableTacheDTO bocResponsable = new ResponsableTacheDTO();
            bocResponsable.setTacheId(savedBocTask.getId());
            bocResponsable.setCodeDept(effectiveCodeDept);
            try {
                responsableTacheService.saveAllForTask(
                    savedBocTask.getId(),
                    Collections.singletonList(bocResponsable)
                );
            } catch (Exception e) {
                log.warn("Erreur lors de la sauvegarde du responsable pour la tâche {} : {}",
                    savedBocTask.getId(), e.getMessage());
            }
        } else {
            log.warn("Aucun codeDept fourni par N8N pour la tâche {} — aucun responsable affecté",
                savedBocTask.getId());
        }

        // --- Lier la tâche à la corresp ---
        Set<Tasks> taskSet = new HashSet<>();
        taskSet.add(savedBocTask);
        correspToSave.setTasks(taskSet);

        correspToSave.setUpdatedDate(ZonedDateTime.now());
        correspToSave.setUpdatedBy(SecurityUtils.getCurrentUserLogin().orElse("N8N"));

        Corresp saved = correspRepository.save(correspToSave);

        // --- ACL : donner READ + WRITE au département sur la tâche et la corresp ---
        try {
            aclUtilService.ensureAclExists(savedBocTask);
            aclUtilService.ensureAclExists(saved);

            if (effectiveCodeDept != null) {
                GrantedAuthoritySid deptSid = new GrantedAuthoritySid(effectiveCodeDept);
                aclUtilService.addPermissionIfNotExists(savedBocTask, deptSid, BasePermission.READ);
                aclUtilService.addPermissionIfNotExists(savedBocTask, deptSid, BasePermission.WRITE);
                aclUtilService.addPermissionIfNotExists(saved, deptSid, BasePermission.READ);
                aclUtilService.addPermissionIfNotExists(saved, deptSid, BasePermission.WRITE);
            } else {
                log.warn("Aucun codeDept fourni — ACL département non configurée pour corresp {}", saved.getId());
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la configuration des ACLs pour la corresp {} : {}",
                saved.getId(), e.getMessage());
        }

        // --- Message ---
        String path = saveBodyToFile(bodyHtml, saved.getId());

        Message message = new Message();
        message.sender(correspToSave.getSender());
        message.setCorrespId(saved.getId());
        message.setCreatedBy("N8N");
        message.setCreationDate(ZonedDateTime.now());
        message.setUniqueName(saved.getId() + ".html");
        message.setPath(path);

        try {
            if (receiverObj instanceof String) {
                message.setReceiver(parseEmailListToMap((String) receiverObj));
            } else if (receiverObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> receivers = (List<Map<String, String>>) receiverObj;
                message.setReceiver(receivers);
            } else {
                message.setReceiver(Collections.emptyList());
            }

            if (copyToObj instanceof String) {
                message.setCopyTo(parseEmailListToMap((String) copyToObj));
            } else if (copyToObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> copyTos = (List<Map<String, String>>) copyToObj;
                message.setCopyTo(copyTos);
            } else {
                message.setCopyTo(Collections.emptyList());
            }
        } catch (Exception e) {
            log.warn("Parsing emails failed pour corresp {} : {} — fallback", saved.getId(), e.getMessage());

            List<Map<String, String>> receiversFallback = new ArrayList<>();
            if (receiverObj instanceof String) {
                for (String part : ((String) receiverObj).replaceAll("[\\n\\r\\t]+", " ").split("[,;]+")) {
                    String email = part.trim().toLowerCase();
                    if (!email.isEmpty()) receiversFallback.add(Map.of("email", email));
                }
            }
            List<Map<String, String>> copyTosFallback = new ArrayList<>();
            if (copyToObj instanceof String) {
                for (String part : ((String) copyToObj).replaceAll("[\\n\\r\\t]+", " ").split("[,;]+")) {
                    String email = part.trim().toLowerCase();
                    if (!email.isEmpty()) copyTosFallback.add(Map.of("email", email));
                }
            }
            message.setReceiver(receiversFallback);
            message.setCopyTo(copyTosFallback);
        }

        messageRepository.save(message);

        log.debug("save corresp avec tâche N8N {}", saved);
        return saved;
    }

    /**
     * Parse une chaîne contenant une ou plusieurs adresses (formats acceptés par InternetAddress),
     * retourne une List<Map<String,String>> où chaque Map contient "name" et "email".
     * Exemple de Map: { "name": "Mohamed", "email": "m@x.com" }
     */
    private List<Map<String,String>> parseEmailListToMap(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Nettoyage léger
        String cleaned = raw.replaceAll("[\\n\\r\\t]+", " ").trim();

        try {
            InternetAddress[] addresses = InternetAddress.parse(cleaned, false);
            List<Map<String,String>> result = new ArrayList<>(addresses.length);

            for (InternetAddress addr : addresses) {
                String email = addr.getAddress();
                if (email != null) {
                    email = email.trim().toLowerCase();
                    // Optionnel : validate() pour detecter email invalid (peut lancer AddressException)
                    try {
                        new InternetAddress(email).validate();
                    } catch (AddressException ex) {
                        // si invalide -> sauter ou logger; ici on saute
                        log.warn("Email invalide ignoré: {}", email);
                        continue;
                    }
                    Map<String,String> entry = new HashMap<>();
                    entry.put("email", email);
                    result.add(entry);
                }
            }
            return result;
        } catch (AddressException e) {
            // Fallback permissif : découpe par virgule / point-virgule / espace et normalise
            log.warn("Parsing permissif (fallback) pour la chaîne : {} — {}", raw, e.getMessage());
            String[] parts = cleaned.split("[,;]+");
            List<Map<String,String>> result = new ArrayList<>();
            for (String part : parts) {
                String email = part.trim().toLowerCase();
                if (email.isEmpty()) continue;
                Map<String,String> entry = new HashMap<>();
                entry.put("email", email);
                result.add(entry);
            }
            return result;
        }
    }





    /**
     * Récupère le receiver et copyTo du message associé à une correspondance.
     *
     * @param correspId l'id de la correspondance.
     * @return un Map contenant les clés "receiver" et "copyTo".
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMessageByCorrespId(Long correspId) {
        log.debug("Request to get Message receiver and copyTo by correspId : {}", correspId);
        Message message = messageRepository.findByCorrespId(correspId)
            .orElseThrow(() -> new EntityNotFoundException("Message not found for correspId: " + correspId));
        Map<String, Object> result = new HashMap<>();
        result.put("receiver", message.getReceiver());
        result.put("copyTo", message.getCopyTo());
        return result;
    }

    public Corresp updateCorrespDraft(Long id, CorrespDTO correspDTO, TypeDocumentFeignDTO typeDocumentDTO) {
        Corresp existing = correspRepository.findById(id).orElseThrow(() -> new RuntimeException("Corresp not found with id " + id));

        if (existing.getStatus() != CorrespStatus.DRAFT && existing.getStatus() != CorrespStatus.DISPATCH) {
            throw new RuntimeException("Modification impossible : le courrier doit être en statut DRAFT ou DISPATCH");
        }

        existing.setCourrierDate(correspDTO.getCourrierDate());
        existing.setSubject(correspDTO.getSubject());
        existing.setSender(correspDTO.getSender());
        existing.setContact(correspDTO.getContact());
        existing.setContactEmail(correspDTO.getContactEmail());
        existing.setContactEmailCc(correspDTO.getContactEmailCc());
        existing.setResponseTo(correspDTO.getResponseTo());
        existing.setSenderReference(correspDTO.getSenderReference());
        existing.setPriority(correspDTO.getPriority());
        String newBody = correspDTO.getBody();
        if (newBody != null) {
            existing.setBody(newBody);
        }
        existing.setDispatchType(correspDTO.getDispatchType());
        existing.setCorresptype(correspDTO.getCorresptype());
        existing.setTypeDocumentId(typeDocumentDTO.getId());
        existing.setLabelDestinataire(correspDTO.getLabelDestinataire());
        existing.setLabelContactCC(correspDTO.getLabelContactCC());

        existing.setUpdatedDate(ZonedDateTime.now());
        existing.setUpdatedBy(SecurityUtils.getCurrentUserLogin().get());

        Corresp updated = correspRepository.save(existing);
        log.debug("Updated DRAFT corresp {}", updated);

        if (newBody != null) {
            String path = saveBodyToFile(newBody, existing.getId());
            java.util.Optional<com.africom.correspmanage.domain.Message> optMsg = messageRepository.findByCorrespId(existing.getId());
            if (optMsg.isPresent()) {
                com.africom.correspmanage.domain.Message msg = optMsg.get();
                msg.setPath(path);
                msg.setUpdatedBy(SecurityUtils.getCurrentUserLogin().get());
                msg.setUpdatedDate(ZonedDateTime.now());
                messageRepository.save(msg);
            } else {
                com.africom.correspmanage.domain.Message message = new com.africom.correspmanage.domain.Message();
                message.sender(existing.getSender());
                message.setCorrespId(existing.getId());
                message.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
                message.setCreationDate(ZonedDateTime.now());
                message.setUniqueName(existing.getId() + ".html");
                message.setPath(path);
                messageRepository.save(message);
            }
        }

        return updated;
    }

    public Corresp soumettreCorresp(Long correspId) {
        Corresp corresp = correspRepository.findById(correspId).get();

        Set<Tasks> tasksSaved = corresp.getTasks();

        // Passer les tâches DRAFT -> IN_PROGRESS et les tâches IN_PROGRESS -> CLOSED
        tasksSaved.forEach(task -> {
            if (task.getStatus().equals(CorrespStatus.DRAFT)) {
                task.setStatus(CorrespStatus.IN_PROGRESS);
                task.setStartDate(ZonedDateTime.now());
            } else if (task.getStatus().equals(CorrespStatus.IN_PROGRESS)) {
                task.setStatus(CorrespStatus.CLOSED);
                task.setCloseDate(ZonedDateTime.now());
            }
        });

        corresp.setTasks(tasksSaved);
        corresp.setStatus(CorrespStatus.IN_PROGRESS);
        corresp.setUpdatedDate(ZonedDateTime.now());
        corresp.setUpdatedBy(SecurityUtils.getCurrentUserLogin().get());

        // Persister d'abord le corresp (et les tâches si cascade)
        Corresp saved = correspRepository.save(corresp);

        // caches partagés pour éviter résolutions répétées
        Map<String, String> deptEffectiveCache = new HashMap<>();

        // s'assurer ACL du corresp
        try {
            aclUtilService.ensureAclExists(saved);
        } catch (Exception e) {
            log.warn("Impossible d'assurer l'ACL pour le corresp {} : {}", saved.getId(), e.getMessage());
        }

        for (Tasks task : saved.getTasks()) {

            // ---------------------------------------------------------------
            // Tâches DRAFT -> IN_PROGRESS : donner READ + WRITE
            // ---------------------------------------------------------------
            if (CorrespStatus.IN_PROGRESS.equals(task.getStatus())) {

                try {
                    aclUtilService.ensureAclExists(task);
                } catch (Exception e) {
                    log.warn("Impossible d'assurer l'ACL pour la tache {} : {}", task.getId(), e.getMessage());
                }

                List<String> rawDeptCodes = responsableTacheService.getResponsableCodeDeptByTacheId(task.getId());
                Set<String> deptCodes = (rawDeptCodes == null) ? Collections.emptySet()
                    : rawDeptCodes.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

                if (!deptCodes.isEmpty()) {
                    for (String dept : deptCodes) {
                        if (dept.isBlank()) continue;
                        String effectiveCode = deptEffectiveCache.computeIfAbsent(dept, d -> {
                            try {
                                Object dto = orgaCareRestClient.getDepartementDtoByCode(d);
                                if (dto == null) return d;
                                if (dto instanceof Map) {
                                    @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) dto;
                                    Object codeObj = map.get("code");
                                    return codeObj != null ? codeObj.toString().trim() : d;
                                } else {
                                    try {
                                        java.lang.reflect.Method m = dto.getClass().getMethod("getCode");
                                        Object codeObj = m.invoke(dto);
                                        return codeObj != null ? codeObj.toString().trim() : d;
                                    } catch (NoSuchMethodException nsme) { return d; }
                                }
                            } catch (Exception ex) {
                                log.warn("OrgaCare failed for '{}': {} — fallback", d, ex.getMessage());
                                return d;
                            }
                        });
                        if (effectiveCode == null || effectiveCode.isBlank()) effectiveCode = dept;
                        try {
                            aclUtilService.addPermissionIfNotExists(task, new GrantedAuthoritySid(effectiveCode), BasePermission.READ);
                            aclUtilService.addPermissionIfNotExists(task, new GrantedAuthoritySid(effectiveCode), BasePermission.WRITE);
                        } catch (Exception e) {
                            log.warn("Erreur ACL dept '{}' sur tache {} : {}", effectiveCode, task.getId(), e.getMessage());
                        }
                    }
                } else {
                    log.debug("Aucun code de département trouvé pour la tache {}", task.getId());
                }

                List<String> matricules = responsableTacheService.getResponsablesMatriculePersByTacheId(task.getId());
                List<String> distinctMatricules = (matricules == null) ? Collections.emptyList()
                    : matricules.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());

                List<String> logins = Collections.emptyList();
                if (!distinctMatricules.isEmpty()) {
                    try {
                        List<com.africom.correspmanage.client.UserFeignDTO> users = correspGatewayRestClient.findUsersByLogins(distinctMatricules);
                        if (users != null && !users.isEmpty()) {
                            Set<String> loginSet = users.stream().filter(Objects::nonNull)
                                .map(u -> u.getLogin() == null ? null : u.getLogin().trim().toLowerCase())
                                .filter(Objects::nonNull).filter(s -> !s.isEmpty())
                                .collect(Collectors.toCollection(LinkedHashSet::new));
                            if (!loginSet.isEmpty()) {
                                logins = new ArrayList<>(loginSet);
                                for (String login : logins) {
                                    try {
                                        aclUtilService.addPermissionIfNotExists(task, new PrincipalSid(login), BasePermission.READ);
                                        aclUtilService.addPermissionIfNotExists(task, new PrincipalSid(login), BasePermission.WRITE);
                                    } catch (Exception e) {
                                        log.warn("Erreur ACL user '{}' sur tache {} : {}", login, task.getId(), e.getMessage());
                                    }
                                }
                            }
                        } else {
                            log.debug("Aucun user retourné par findUsersByLogins pour les matricules: {}", distinctMatricules);
                        }
                    } catch (Exception e) {
                        log.warn("Erreur findUsersByLogins pour tache {} : {}", task.getId(), e.getMessage());
                    }
                } else {
                    log.debug("Aucun matriculePers trouvé pour la tache {}", task.getId());
                }

                String taskType = task.getTaskType();
                boolean onlyRead = taskType != null && taskType.equalsIgnoreCase("Lecture");
                boolean readAndWrite = taskType != null && (
                    taskType.equalsIgnoreCase("Traitement") ||
                        taskType.equalsIgnoreCase("Validation") ||
                        taskType.equalsIgnoreCase("Signature")
                );

                if (onlyRead || readAndWrite) {
                    for (String dept : deptCodes) {
                        String effectiveCode = deptEffectiveCache.getOrDefault(dept, dept);
                        try {
                            aclUtilService.addPermissionIfNotExists(saved, new GrantedAuthoritySid(effectiveCode), BasePermission.READ);
                            if (readAndWrite) aclUtilService.addPermissionIfNotExists(saved, new GrantedAuthoritySid(effectiveCode), BasePermission.WRITE);
                        } catch (Exception e) {
                            log.warn("Erreur ACL dept '{}' sur corresp {} : {}", effectiveCode, saved.getId(), e.getMessage());
                        }
                    }
                    for (String login : logins) {
                        try {
                            aclUtilService.addPermissionIfNotExists(saved, new PrincipalSid(login), BasePermission.READ);
                            if (readAndWrite) aclUtilService.addPermissionIfNotExists(saved, new PrincipalSid(login), BasePermission.WRITE);
                        } catch (Exception e) {
                            log.warn("Erreur ACL user '{}' sur corresp {} : {}", login, saved.getId(), e.getMessage());
                        }
                    }
                }
            }

            // ---------------------------------------------------------------
            // Tâches IN_PROGRESS -> CLOSED : READ only (même logique que cloturerCorresp)
            // ---------------------------------------------------------------
            else if (CorrespStatus.CLOSED.equals(task.getStatus())) {

                try {
                    aclUtilService.ensureAclExists(task);
                } catch (Exception e) {
                    log.warn("Impossible d'assurer l'ACL pour la tache {} : {}", task.getId(), e.getMessage());
                }

                List<String> rawDeptCodes = responsableTacheService.getResponsableCodeDeptByTacheId(task.getId());
                Set<String> deptCodes = (rawDeptCodes == null) ? Collections.emptySet()
                    : rawDeptCodes.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

                if (!deptCodes.isEmpty()) {
                    for (String dept : deptCodes) {
                        if (dept.isBlank()) continue;
                        String effectiveCode = deptEffectiveCache.computeIfAbsent(dept, d -> {
                            try {
                                Object dto = orgaCareRestClient.getDepartementDtoByCode(d);
                                if (dto == null) return d;
                                if (dto instanceof Map) {
                                    @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) dto;
                                    Object codeObj = map.get("code");
                                    return codeObj != null ? codeObj.toString().trim() : d;
                                } else {
                                    try {
                                        java.lang.reflect.Method m = dto.getClass().getMethod("getCode");
                                        Object codeObj = m.invoke(dto);
                                        return codeObj != null ? codeObj.toString().trim() : d;
                                    } catch (NoSuchMethodException nsme) { return d; }
                                }
                            } catch (Exception ex) {
                                log.warn("OrgaCare failed for '{}': {} — fallback", d, ex.getMessage());
                                return d;
                            }
                        });
                        if (effectiveCode == null || effectiveCode.isBlank()) effectiveCode = dept;

                        Sid authSid = new GrantedAuthoritySid(effectiveCode);
                        try {
                            aclUtilService.addPermissionIfNotExists(task, authSid, BasePermission.READ);
                            aclUtilService.deletePermissionBySid(task, authSid, BasePermission.WRITE);
                        } catch (Exception e) {
                            log.warn("Erreur ACL (dept) tache CLOSED '{}' tache {} : {}", effectiveCode, task.getId(), e.getMessage());
                        }
                        try {
                            aclUtilService.addPermissionIfNotExists(saved, authSid, BasePermission.READ);
                            aclUtilService.deletePermissionBySid(saved, authSid, BasePermission.WRITE);
                        } catch (Exception e) {
                            log.warn("Erreur ACL (dept) tache CLOSED '{}' corresp {} : {}", effectiveCode, saved.getId(), e.getMessage());
                        }
                    }
                }

                List<String> matricules = responsableTacheService.getResponsablesMatriculePersByTacheId(task.getId());
                List<String> distinctMatricules = (matricules == null) ? Collections.emptyList()
                    : matricules.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());

                if (!distinctMatricules.isEmpty()) {
                    try {
                        List<com.africom.correspmanage.client.UserFeignDTO> users = correspGatewayRestClient.findUsersByLogins(distinctMatricules);
                        if (users != null && !users.isEmpty()) {
                            Set<String> loginSet = users.stream().filter(Objects::nonNull)
                                .map(u -> u.getLogin() == null ? null : u.getLogin().trim().toLowerCase())
                                .filter(Objects::nonNull).filter(s -> !s.isEmpty())
                                .collect(Collectors.toCollection(LinkedHashSet::new));
                            for (String login : loginSet) {
                                Sid principalSid = new PrincipalSid(login);
                                try {
                                    aclUtilService.addPermissionIfNotExists(task, principalSid, BasePermission.READ);
                                    aclUtilService.deletePermissionBySid(task, principalSid, BasePermission.WRITE);
                                } catch (Exception e) {
                                    log.warn("Erreur ACL (user) tache CLOSED '{}' tache {} : {}", login, task.getId(), e.getMessage());
                                }
                                try {
                                    aclUtilService.addPermissionIfNotExists(saved, principalSid, BasePermission.READ);
                                    aclUtilService.deletePermissionBySid(saved, principalSid, BasePermission.WRITE);
                                } catch (Exception e) {
                                    log.warn("Erreur ACL (user) tache CLOSED '{}' corresp {} : {}", login, saved.getId(), e.getMessage());
                                }
                            }
                        } else {
                            log.debug("Aucun user retourné par findUsersByLogins pour les matricules: {}", distinctMatricules);
                        }
                    } catch (Exception e) {
                        log.warn("Erreur findUsersByLogins pour tache CLOSED {} : {}", task.getId(), e.getMessage());
                    }
                } else {
                    log.debug("Aucun matriculePers trouvé pour la tache CLOSED {}", task.getId());
                }
            }
        }

        // Persister les métadonnées du corresp
        correspRepository.save(saved);

        return saved;
    }


    @Transactional
    public Corresp cloturerCorresp(Long correspId) {
        Corresp corresp = correspRepository.findById(correspId).orElseThrow(() -> new RuntimeException("Corresp not found with id " + correspId));

        Set<Tasks> tasksSaved = corresp.getTasks();

        // cache partagé pour éviter résolutions répétées des mêmes départements
        Map<String, String> deptEffectiveCache = new HashMap<>();

        // s'assurer ACL du corresp (on en aura besoin plus bas)
        try {
            aclUtilService.ensureAclExists(corresp);
        } catch (Exception e) {
            log.warn("Impossible d'assurer l'ACL pour le corresp {} : {}", corresp.getId(), e.getMessage());
        }

        for (Tasks task : tasksSaved) {
            if (task.getStatus().equals(CorrespStatus.IN_PROGRESS)) {
                // changer l'état de la tâche
                task.setStatus(CorrespStatus.CLOSED);
                task.setCloseDate(ZonedDateTime.now());

                // --- Gérer les ACLs : si responsable avait READ+WRITE => enlever WRITE et garder READ.
                try {
                    // s'assurer que l'ACL de la tâche existe
                    aclUtilService.ensureAclExists(task);
                } catch (Exception e) {
                    log.warn("Impossible d'assurer l'ACL pour la tache {} : {}", task.getId(), e.getMessage());
                }

                // 1) Départements responsables -> retirer WRITE si présent, assurer READ (sur TÂCHE et CORRESP)
                List<String> rawDeptCodes = responsableTacheService.getResponsableCodeDeptByTacheId(task.getId());
                Set<String> deptCodes = (rawDeptCodes == null) ? Collections.emptySet()
                    : rawDeptCodes.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toCollection(LinkedHashSet::new));

                if (!deptCodes.isEmpty()) {
                    for (String dept : deptCodes) {
                        if (dept.isBlank()) continue;
                        String effectiveCode = deptEffectiveCache.computeIfAbsent(dept, d -> {
                            try {
                                Object dto = orgaCareRestClient.getDepartementDtoByCode(d);
                                if (dto == null) return d;
                                if (dto instanceof Map) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> map = (Map<String, Object>) dto;
                                    Object codeObj = map.get("code");
                                    return codeObj != null ? codeObj.toString().trim() : d;
                                } else {
                                    try {
                                        java.lang.reflect.Method m = dto.getClass().getMethod("getCode");
                                        Object codeObj = m.invoke(dto);
                                        return codeObj != null ? codeObj.toString().trim() : d;
                                    } catch (NoSuchMethodException nsme) {
                                        return d;
                                    }
                                }
                            } catch (Exception ex) {
                                log.warn("OrgaCare getDepartementDtoByCode failed for '{}': {} — fallback to provided code", d, ex.getMessage());
                                return d;
                            }
                        });

                        if (effectiveCode == null || effectiveCode.isBlank()) effectiveCode = dept;

                        try {
                            // Sur la TÂCHE : supprimer WRITE si présent, assurer READ
                            aclUtilService.deletePermissionForAuthority(task, effectiveCode, BasePermission.WRITE);
                            aclUtilService.addPermissionIfNotExists(task, new GrantedAuthoritySid(effectiveCode), BasePermission.READ);
                        } catch (Exception e) {
                            log.warn("Erreur ACL (tâche) pour dept '{}' sur tache {} : {}", effectiveCode, task.getId(), e.getMessage());
                        }

                        try {
                            // Sur le CORRESP : supprimer WRITE si présent, assurer READ
                            aclUtilService.deletePermissionForAuthority(corresp, effectiveCode, BasePermission.WRITE);
                            aclUtilService.addPermissionIfNotExists(corresp, new GrantedAuthoritySid(effectiveCode), BasePermission.READ);
                        } catch (Exception e) {
                            log.warn("Erreur ACL (corresp) pour dept '{}' sur corresp {} : {}", effectiveCode, corresp.getId(), e.getMessage());
                        }
                    }
                }

                // 2) Matricules responsables -> convertir en logins -> retirer WRITE, assurer READ (sur TÂCHE et CORRESP)
                List<String> matricules = responsableTacheService.getResponsablesMatriculePersByTacheId(task.getId());
                List<String> distinctMatricules = (matricules == null) ? Collections.emptyList()
                    : matricules.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());

                if (!distinctMatricules.isEmpty()) {
                    try {
                        List<com.africom.correspmanage.client.UserFeignDTO> users = correspGatewayRestClient.findUsersByLogins(distinctMatricules);
                        if (users != null && !users.isEmpty()) {
                            Set<String> loginSet = users.stream()
                                .filter(Objects::nonNull)
                                .map(u -> u.getLogin() == null ? null : u.getLogin().trim().toLowerCase())
                                .filter(Objects::nonNull)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toCollection(LinkedHashSet::new));

                            for (String login : loginSet) {
                                try {
                                    // Sur la TÂCHE : supprimer WRITE s'il existe, assurer READ
                                    aclUtilService.deletePermission(task, login, BasePermission.WRITE);
                                    aclUtilService.addPermissionIfNotExists(task, new PrincipalSid(login), BasePermission.READ);
                                } catch (Exception e) {
                                    log.warn("Erreur ACL (tâche) pour user '{}' sur tache {} : {}", login, task.getId(), e.getMessage());
                                }

                                try {
                                    // Sur le CORRESP : supprimer WRITE s'il existe, assurer READ
                                    aclUtilService.deletePermission(corresp, login, BasePermission.WRITE);
                                    aclUtilService.addPermissionIfNotExists(corresp, new PrincipalSid(login), BasePermission.READ);
                                } catch (Exception e) {
                                    log.warn("Erreur ACL (corresp) pour user '{}' sur corresp {} : {}", login, corresp.getId(), e.getMessage());
                                }
                            }
                        } else {
                            log.debug("Aucun user retourné par findUsersByLogins pour les matricules: {}", distinctMatricules);
                        }
                    } catch (Exception e) {
                        log.warn("Erreur lors de l'appel findUsersByLogins pour la tache {} : {}", task.getId(), e.getMessage());
                    }
                } else {
                    log.debug("Aucun matriculePers trouvé pour la tache {}", task.getId());
                }
            } // end if IN_PROGRESS
        } // end for tasks

        corresp.setTasks(tasksSaved);
        corresp.setStatus(CorrespStatus.CLOSED);
        corresp.setUpdatedDate(ZonedDateTime.now());
        corresp.setUpdatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));

        return correspRepository.save(corresp);
    }


    public List<Corresp> getListByAcl(String deptCode) {
        return correspRepository.findAllByAclForDept(deptCode);
    }

    public Page<Corresp> getListByAclPage(String deptCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return correspRepository.findAllByAclForDeptWithPagination(deptCode, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Corresp addTaskToExistingCorresp(TasksDTO tasksDTO, Long correspId) {
        Corresp existingCorresp = correspRepository.findById(correspId)
            .orElseThrow(() -> new RuntimeException("Corresp not found with id " + correspId));

        List<ResponsableTacheDTO> inputResponsables = tasksDTO.getResponsables();

        // normalize & dedupe (key = dept code or matricule)
        List<ResponsableTacheDTO> distinctResponsables = Collections.emptyList();
        if (inputResponsables != null && !inputResponsables.isEmpty()) {
            Map<String, ResponsableTacheDTO> map = new LinkedHashMap<>();
            for (ResponsableTacheDTO r : inputResponsables) {
                if (r == null) continue;
                String codeDept = r.getCodeDept() != null ? r.getCodeDept().trim() : null;
                String matricule = r.getMatriculePers() != null ? r.getMatriculePers().trim() : null;
                if ((codeDept == null || codeDept.isEmpty()) && (matricule == null || matricule.isEmpty())) {
                    continue; // ignorer
                }
                String key = (matricule != null && !matricule.isEmpty()) ? ("P:" + matricule) : ("D:" + codeDept);
                // keep first occurrence
                map.putIfAbsent(key, new ResponsableTacheDTO() {{
                    setCodeDept(codeDept);
                    setMatriculePers(matricule);
                }});
            }
            distinctResponsables = new ArrayList<>(map.values());
        }

        if (distinctResponsables.isEmpty()) {
            // Comportement : on autorise la création d'une tâche sans responsable (comme avant),
            // ou on peut lever une exception si tu veux obliger au moins un responsable.
        }

        Set<Tasks> createdTasks = existingCorresp.getTasks() != null ? existingCorresp.getTasks() : new HashSet<>();

        // Limite de sécurité (optionnelle) : éviter création massive
        final int MAX_CREATED = 50;
        if (distinctResponsables.size() > MAX_CREATED) {
            throw new RuntimeException("Trop de responsables fournis (max " + MAX_CREATED + ")");
        }

        for (ResponsableTacheDTO r : distinctResponsables) {
            // créer une tâche par responsable
            Tasks tasks = new Tasks();
            tasks.setResponsible(null); // on stockera le responsable dans ResponsableTache
            tasks.setStatus(CorrespStatus.DRAFT);
            tasks.setTaskType(tasksDTO.getTaskType());
            tasks.setDueDate(tasksDTO.getDueDate());
            tasks.createdBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
            tasks.creationDate(ZonedDateTime.now());
            tasks.setStartDate(ZonedDateTime.now());
            tasks.setCommentaire(tasksDTO.getCommentaire());

            StringBuilder objectBuilder = new StringBuilder();
            String responsibleDisplay = (r.getMatriculePers() != null && !r.getMatriculePers().isBlank()) ? r.getMatriculePers() : (r.getCodeDept() != null ? r.getCodeDept() : "");
            objectBuilder
                .append(responsibleDisplay)
                .append(" effectuera la tâche de ")
                .append(tasksDTO.getTaskType() != null ? tasksDTO.getTaskType().toUpperCase() : "N/A");

            if (tasksDTO.getDueDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = tasksDTO.getDueDate().format(formatter);
                objectBuilder.append(" en ne dépassant pas la date limite du ").append(formattedDate);
            }
            tasks.setObject(objectBuilder.toString());

            Tasks savedTask = tasksRepository.save(tasks);

            // persist le responsable pour la tâche (service existant)
            r.setTacheId(savedTask.getId());
            try {
                responsableTacheService.saveAllForTask(savedTask.getId(), Collections.singletonList(r));
            } catch (Exception e) {
                // si tu veux rollback complet, laisse l'exception remonter (méthode annotée @Transactional).
                // ici on rethrow pour faire rollback.
                throw new RuntimeException("Erreur lors de la sauvegarde du responsable pour la tâche " + savedTask.getId(), e);
            }

            createdTasks.add(savedTask);
        }

        existingCorresp.setTasks(createdTasks);
        existingCorresp.setUpdatedDate(ZonedDateTime.now());
        existingCorresp.setUpdatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));

        return correspRepository.save(existingCorresp);
    }


    public Corresp addPjToExistingCorresp(PjDTO pjDTO, Long correspId) {
        Corresp existingCorresp = correspRepository.findById(correspId).get();

        Pj pjEntity = new Pj();
        pjEntity.setName(pjDTO.getName());
        pjEntity.setFileType(pjDTO.getFileType());
        pjEntity.setUniqueName(pjDTO.getUniqueName());
        pjEntity.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
        pjEntity.setCreationDate(ZonedDateTime.now());
        pjEntity.setPath(pjDTO.getPath());

        Set<Pj> pjList = existingCorresp.getAttachments();

        Pj pjSaved = pjRepository.save(pjEntity);

        pjList.add(pjSaved);

        existingCorresp.setAttachments(pjList);

        existingCorresp.setUpdatedDate(ZonedDateTime.now());

        existingCorresp.setUpdatedBy(SecurityUtils.getCurrentUserLogin().get());

        return correspRepository.save(existingCorresp);
    }

    public Corresp changeTaskStatus(Long correspId, Long taskId, String statusTo) {
        Corresp existingCorresp = correspRepository.findById(correspId)
            .orElseThrow(() -> new RuntimeException("Corresp not found"));

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

        if (newStatus == CorrespStatus.CLOSED) {
            taskToUpdate.setCloseDate(ZonedDateTime.now());
        }

        if (newStatus == CorrespStatus.IN_PROGRESS) {
            taskToUpdate.setStartDate(ZonedDateTime.now());
        }

        existingCorresp.setUpdatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
        existingCorresp.setUpdatedDate(ZonedDateTime.now());

        // Persiste la tâche modifiée
        Tasks tasksSaved = tasksRepository.save(taskToUpdate);

        // ----------------------------------------------
        // Si la tâche passe en IN_PROGRESS, on crée/attribue les ACLs (existante)
        // ----------------------------------------------
        if (tasksSaved.getStatus().equals(CorrespStatus.IN_PROGRESS)) {
            try {
                aclUtilService.ensureAclExists(tasksSaved);
            } catch (Exception e) {
                log.warn("Impossible d'assurer l'ACL pour la tache {} : {}", tasksSaved.getId(), e.getMessage());
            }

            List<String> rawDeptCodes = responsableTacheService.getResponsableCodeDeptByTacheId(tasksSaved.getId());
            Set<String> deptCodes = (rawDeptCodes == null) ? Collections.emptySet()
                : rawDeptCodes.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

            Map<String, String> deptEffectiveCache = new HashMap<>();

            if (!deptCodes.isEmpty()) {
                for (String dept : deptCodes) {
                    if (dept.isBlank()) continue;
                    String effectiveCode = deptEffectiveCache.computeIfAbsent(dept, d -> {
                        try {
                            Object dto = orgaCareRestClient.getDepartementDtoByCode(d);
                            if (dto == null) return d;
                            if (dto instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dto;
                                Object codeObj = map.get("code");
                                return codeObj != null ? codeObj.toString().trim() : d;
                            } else {
                                try {
                                    java.lang.reflect.Method m = dto.getClass().getMethod("getCode");
                                    Object codeObj = m.invoke(dto);
                                    return codeObj != null ? codeObj.toString().trim() : d;
                                } catch (NoSuchMethodException nsme) {
                                    return d;
                                }
                            }
                        } catch (Exception ex) {
                            log.warn("OrgaCare getDepartementDtoByCode failed for '{}': {} — fallback to provided code", d, ex.getMessage());
                            return d;
                        }
                    });

                    if (effectiveCode == null || effectiveCode.isBlank()) effectiveCode = dept;

                    try {
                        aclUtilService.addPermissionIfNotExists(tasksSaved, new GrantedAuthoritySid(effectiveCode), BasePermission.READ);
                        aclUtilService.addPermissionIfNotExists(tasksSaved, new GrantedAuthoritySid(effectiveCode), BasePermission.WRITE);
                    } catch (Exception e) {
                        log.warn("Impossible d'ajouter les permissions pour dept '{}' sur tache {} : {}", effectiveCode, tasksSaved.getId(), e.getMessage());
                    }
                }
            } else {
                log.debug("Aucun code de département trouvé pour la tache {}", tasksSaved.getId());
            }

            // personnes
            List<String> matricules = responsableTacheService.getResponsablesMatriculePersByTacheId(tasksSaved.getId());
            List<String> distinctMatricules = (matricules == null) ? Collections.emptyList()
                : matricules.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());

            List<String> logins = Collections.emptyList();
            if (!distinctMatricules.isEmpty()) {
                try {
                    List<com.africom.correspmanage.client.UserFeignDTO> users = correspGatewayRestClient.findUsersByLogins(distinctMatricules);
                    if (users != null && !users.isEmpty()) {
                        Set<String> loginSet = users.stream()
                            .filter(Objects::nonNull)
                            .map(u -> u.getLogin() == null ? null : u.getLogin().trim().toLowerCase())
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                        if (!loginSet.isEmpty()) {
                            logins = new ArrayList<>(loginSet);
                            for (String login : logins) {
                                try {
                                    aclUtilService.addPermissionIfNotExists(tasksSaved, new PrincipalSid(login), BasePermission.READ);
                                    aclUtilService.addPermissionIfNotExists(tasksSaved, new PrincipalSid(login), BasePermission.WRITE);
                                } catch (Exception e) {
                                    log.warn("Impossible d'ajouter les permissions pour user '{}' sur tache {} : {}", login, tasksSaved.getId(), e.getMessage());
                                }
                            }
                        }
                    } else {
                        log.debug("Aucun user retourné par findUsersByLogins pour les matricules: {}", distinctMatricules);
                    }
                } catch (Exception e) {
                    log.warn("Erreur lors de l'appel findUsersByLogins pour la tache {} : {}", tasksSaved.getId(), e.getMessage());
                }
            } else {
                log.debug("Aucun matriculePers trouvé pour la tache {}", tasksSaved.getId());
            }

            // droits sur la corresp en fonction du type de tâche
            String taskType = tasksSaved.getTaskType();
            boolean onlyRead = taskType != null && taskType.equalsIgnoreCase("Lecture");
            boolean readAndWrite = taskType != null && (
                taskType.equalsIgnoreCase("Traitement") ||
                    taskType.equalsIgnoreCase("Validation") ||
                    taskType.equalsIgnoreCase("Signature")
            );

            if (onlyRead || readAndWrite) {
                try {
                    aclUtilService.ensureAclExists(existingCorresp);
                } catch (Exception e) {
                    log.warn("Impossible d'assurer l'ACL pour le corresp {} : {}", existingCorresp.getId(), e.getMessage());
                }

                if (!deptCodes.isEmpty()) {
                    for (String dept : deptCodes) {
                        String effectiveCode = deptEffectiveCache.getOrDefault(dept, dept);
                        try {
                            aclUtilService.addPermissionIfNotExists(existingCorresp, new GrantedAuthoritySid(effectiveCode), BasePermission.READ);
                            if (readAndWrite) {
                                aclUtilService.addPermissionIfNotExists(existingCorresp, new GrantedAuthoritySid(effectiveCode), BasePermission.WRITE);
                            }
                        } catch (Exception e) {
                            log.warn("Impossible d'ajouter les permissions pour dept '{}' sur corresp {} : {}", effectiveCode, existingCorresp.getId(), e.getMessage());
                        }
                    }
                }

                if (logins != null && !logins.isEmpty()) {
                    for (String login : logins) {
                        try {
                            aclUtilService.addPermissionIfNotExists(existingCorresp, new PrincipalSid(login), BasePermission.READ);
                            if (readAndWrite) {
                                aclUtilService.addPermissionIfNotExists(existingCorresp, new PrincipalSid(login), BasePermission.WRITE);
                            }
                        } catch (Exception e) {
                            log.warn("Impossible d'ajouter les permissions pour user '{}' sur corresp {} : {}", login, existingCorresp.getId(), e.getMessage());
                        }
                    }
                }
            }
        } // FIN IN_PROGRESS block

        // ----------------------------------------------
        // NOUVEAU : quand la tâche passe en CLOSED -> forcer les responsables en READ only
        // ----------------------------------------------
        if (tasksSaved.getStatus().equals(CorrespStatus.CLOSED)) {
            // récupérer codes départements et matricules
            List<String> rawDeptCodes = responsableTacheService.getResponsableCodeDeptByTacheId(tasksSaved.getId());
            Set<String> deptCodes = (rawDeptCodes == null) ? Collections.emptySet()
                : rawDeptCodes.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

            List<String> matricules = responsableTacheService.getResponsablesMatriculePersByTacheId(tasksSaved.getId());
            List<String> distinctMatricules = (matricules == null) ? Collections.emptyList()
                : matricules.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());

            Map<String, String> deptEffectiveCache = new HashMap<>();

            // départements : sur TÂCHE garantir READ et supprimer WRITE ; sur CORRESP ajouter READ si absent et supprimer WRITE toujours
            if (!deptCodes.isEmpty()) {
                for (String dept : deptCodes) {
                    if (dept.isBlank()) continue;
                    String effectiveCode = deptEffectiveCache.computeIfAbsent(dept, d -> {
                        try {
                            Object dto = orgaCareRestClient.getDepartementDtoByCode(d);
                            if (dto == null) return d;
                            if (dto instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dto;
                                Object codeObj = map.get("code");
                                return codeObj != null ? codeObj.toString().trim() : d;
                            } else {
                                try {
                                    java.lang.reflect.Method m = dto.getClass().getMethod("getCode");
                                    Object codeObj = m.invoke(dto);
                                    return codeObj != null ? codeObj.toString().trim() : d;
                                } catch (NoSuchMethodException nsme) {
                                    return d;
                                }
                            }
                        } catch (Exception ex) {
                            log.warn("OrgaCare getDepartementDtoByCode failed for '{}': {} — fallback to provided code", d, ex.getMessage());
                            return d;
                        }
                    });

                    if (effectiveCode == null || effectiveCode.isBlank()) effectiveCode = dept;

                    Sid authSid = new GrantedAuthoritySid(effectiveCode);

                    try {
                        // sur la tache : s'assurer READ et supprimer WRITE
                        aclUtilService.addPermissionIfNotExists(tasksSaved, authSid, BasePermission.READ);
                        aclUtilService.deletePermissionBySid(tasksSaved, authSid, BasePermission.WRITE);
                    } catch (Exception e) {
                        log.warn("Erreur gestion ACL (dept) sur TACHE pour '{}': {}", effectiveCode, e.getMessage());
                    }

                    try {
                        // sur la corresp : ajouter READ si absent ; supprimer WRITE toujours (même si READ existait déjà)
                        boolean hasReadOnCorresp = aclUtilService.hasPermissionForSid(existingCorresp, authSid, BasePermission.READ);
                        if (!hasReadOnCorresp) {
                            aclUtilService.addPermissionIfNotExists(existingCorresp, authSid, BasePermission.READ);
                        }
                        // supprimer WRITE dans tous les cas pour forcer lecture seule
                        aclUtilService.deletePermissionBySid(existingCorresp, authSid, BasePermission.WRITE);
                    } catch (Exception e) {
                        log.warn("Erreur gestion ACL (dept) sur CORRESP pour '{}': {}", effectiveCode, e.getMessage());
                    }
                }
            }

            // personnes : convertir matricules -> logins et appliquer même logique
            if (!distinctMatricules.isEmpty()) {
                try {
                    List<com.africom.correspmanage.client.UserFeignDTO> users =
                        correspGatewayRestClient.findUsersByLogins(distinctMatricules);

                    if (users != null && !users.isEmpty()) {
                        Set<String> loginSet = users.stream()
                            .filter(Objects::nonNull)
                            .map(u -> u.getLogin() == null ? null : u.getLogin().trim().toLowerCase())
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                        for (String login : loginSet) {
                            Sid principalSid = new PrincipalSid(login);
                            try {
                                // sur la TACHE : s'assurer READ et supprimer WRITE
                                aclUtilService.addPermissionIfNotExists(tasksSaved, principalSid, BasePermission.READ);
                                aclUtilService.deletePermissionBySid(tasksSaved, principalSid, BasePermission.WRITE);
                            } catch (Exception e) {
                                log.warn("Erreur gestion ACL (user) sur TACHE pour '{}': {}", login, e.getMessage());
                            }

                            try {
                                // sur la CORRESP : ajouter READ si absent ; supprimer WRITE toujours
                                boolean hasReadOnCorresp = aclUtilService.hasPermissionForSid(existingCorresp, principalSid, BasePermission.READ);
                                if (!hasReadOnCorresp) {
                                    aclUtilService.addPermissionIfNotExists(existingCorresp, principalSid, BasePermission.READ);
                                }
                                // supprimer WRITE quel que soit l'état de READ
                                aclUtilService.deletePermissionBySid(existingCorresp, principalSid, BasePermission.WRITE);
                            } catch (Exception e) {
                                log.warn("Erreur gestion ACL (user) sur CORRESP pour '{}': {}", login, e.getMessage());
                            }
                        }
                    } else {
                        log.debug("Aucun utilisateur trouvé par findUsersByLogins pour les matricules: {}", distinctMatricules);
                    }
                } catch (Exception e) {
                    log.warn("Erreur lors de l'appel findUsersByLogins pour la tache {} : {}", tasksSaved.getId(), e.getMessage());
                }
            }
        } // FIN CLOSED block

        // Persister les métadonnées du corresp (updatedDate/updatedBy)
        correspRepository.save(existingCorresp);

        return existingCorresp;
    }


    public String saveBodyToFile(String richTextContent, Long correspId) {
        try {
            ZonedDateTime now = ZonedDateTime.now();

            // Build directory path cleanly
            Path storagePath = Paths.get(
                applicationProperties.getDir_body(),
                String.valueOf(now.getYear()),
                now.getMonth().toString(),
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );

            Files.createDirectories(storagePath);

            // Always use the file <correspId>.html
            Path filePath = storagePath.resolve(correspId + ".html");

            // If file exists → delete it
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Create a new empty file
            Files.createFile(filePath);

            log.debug("filepath {}", filePath);
            log.debug("richTextContent {}", richTextContent);

            // Write the content
            Files.writeString(filePath, richTextContent, StandardOpenOption.TRUNCATE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error saving body to file", e);
        }
    }

    public String loadBodyFromFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading body file", e);
        }
    }

    @Transactional
    public void deleteCorrespWithTasksAndPjs(Long correspId) {
        Corresp corresp = correspRepository.findById(correspId).orElseThrow(() -> new EntityNotFoundException("Corresp not found"));

        Set<Pj> attachments = new HashSet<>(corresp.getAttachments());
        Set<Tasks> tasks = new HashSet<>(corresp.getTasks());

        corresp.getAttachments().clear();
        corresp.getTasks().clear();

        correspRepository.save(corresp);
        correspRepository.flush();

        messageRepository.deleteByCorrespId(correspId);

        for (Pj pj : attachments) {
            long usageCount = pjRepository.countCorrespsUsingPj(pj.getId());

            if (usageCount <= 1) {
                deleteFileFromDisk(pj.getPath());
                pjRepository.delete(pj);
            }
        }

        for (Tasks task : tasks) {
            long usageCount = tasksRepository.countCorrespsUsingTask(task.getId());

            if (usageCount <= 1) {
                aclUtilService.deleteAllPermissions(task);
                tasksRepository.delete(task);
            }
        }

        aclUtilService.deleteAllPermissions(corresp);
        correspRepository.delete(corresp);
    }

    private void deleteFileFromDisk(String path) {
        if (path == null || path.isBlank()) return;

        try {
            Path filePath = Paths.get(path);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }

    @Transactional(readOnly = true)
    public List<CorrespDTO> findCorrespsByTaskId(Long taskId) {
        log.debug("Request to get Corresps by taskId : {}", taskId);

        return tasksRepository
            .findById(taskId)
            .map(task -> task.getCorresps().stream().map(correspMapper::toDto).collect(Collectors.toList()))
            .orElseGet(Collections::emptyList);
    }

    @Transactional(readOnly = true)
    public Page<CorrespDTO> findAllWithReference(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageIndex - 1), pageSize, Sort.by("id").descending());
        return correspRepository.findAllByReferenceIsNotNull(pageable).map(correspMapper::toDto);
    }

    /**
     * Get all corresps that have a non-null reference (full list).
     *
     * @return list of CorrespDTO
     */
    @Transactional(readOnly = true)
    public List<CorrespDTO> findAllWithReferenceList() {
        return correspRepository.findAllByReferenceIsNotNull().stream().map(correspMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Retourne toutes les correspondances accessibles par l'utilisateur courant (via ACL).
     * Les SIDs vérifiés sont : les codes de départements de l'utilisateur + son login.
     *
     * @param userId l'id de l'utilisateur (pour récupérer ses départements via OrgaCare)
     * @return liste de CorrespDTO accessibles
     */
    @Transactional(readOnly = true)
    public List<CorrespDTO> getAllCorrespsByUserAcl(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        // 1) Récupérer les codes départements de l'utilisateur via OrgaCare
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

        // 2) Récupérer le login de l'utilisateur
        String userLogin = null;
        try {
            Map<String, Object> userMap = correspGatewayRestClient.getUserById(userId);
            if (userMap != null) {
                Object login = userMap.get("login");
                if (login != null && !login.toString().trim().isEmpty()) {
                    userLogin = login.toString().trim().toLowerCase();
                }
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer le login pour userId {} : {}", userId, e.getMessage());
        }

        // 3) Construire la liste complète des SIDs : deptCodes + login utilisateur
        List<String> allSids = new ArrayList<>(deptCodes);
        if (userLogin != null && !allSids.contains(userLogin)) {
            allSids.add(userLogin);
        }

        if (allSids.isEmpty()) {
            return Collections.emptyList();
        }

        // 4) Requête native ACL : correspondances avec READ pour ces SIDs
        int readMask = BasePermission.READ.getMask();
        List<Corresp> correspEntities = correspRepository.findAllByAclForSids(allSids, readMask);

        if (correspEntities == null) {
            correspEntities = new ArrayList<>();
        }

        // 5) Ajouter les correspondances DRAFT créées par l'utilisateur courant
        if (userLogin != null) {
            List<Corresp> draftCorresps = correspRepository.findAllDraftByCreatedBy(userLogin);
            if (draftCorresps != null && !draftCorresps.isEmpty()) {
                // Collecter les IDs déjà présents pour éviter les doublons
                Set<Long> existingIds = correspEntities.stream()
                    .map(Corresp::getId)
                    .collect(Collectors.toSet());
                for (Corresp draft : draftCorresps) {
                    if (!existingIds.contains(draft.getId())) {
                        correspEntities.add(draft);
                    }
                }
            }
        }

        if (correspEntities.isEmpty()) {
            return Collections.emptyList();
        }

        return correspEntities.stream()
            .map(correspMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Retourne les correspondances accessibles par l'utilisateur (via ACL), paginées.
     *
     * @param userId    l'id de l'utilisateur
     * @param pageIndex numéro de page (commence à 1)
     * @param pageSize  taille de page (ex: 15)
     * @return Page de CorrespDTO
     */
    @Transactional(readOnly = true)
    public Page<CorrespDTO> getAllCorrespsByUserAclPaged(Long userId, int pageIndex, int pageSize) {
        if (userId == null) {
            return Page.empty();
        }

        // 1) Récupérer les codes départements de l'utilisateur via OrgaCare
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
            log.warn("Impossible de récupérer les départements pour userId {} : {}", userId, e.getMessage());
            deptCodes = new ArrayList<>();
        }

        // 2) Récupérer le login de l'utilisateur
        String userLogin = null;
        try {
            Map<String, Object> userMap = correspGatewayRestClient.getUserById(userId);
            if (userMap != null) {
                Object login = userMap.get("login");
                if (login != null && !login.toString().trim().isEmpty()) {
                    userLogin = login.toString().trim().toLowerCase();
                }
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer le login pour userId {} : {}", userId, e.getMessage());
        }

        // 3) Construire la liste des SIDs (deptCodes + login)
        List<String> allSids = new ArrayList<>(deptCodes);
        if (userLogin != null && !allSids.contains(userLogin)) {
            allSids.add(userLogin);
        }

        // Cas où l'utilisateur n'a ni département ni login résolu
        if (allSids.isEmpty() && userLogin == null) {
            log.warn("Aucun SID trouvé pour userId {} — retour vide", userId);
            return Page.empty();
        }

        // Si la liste des SIDs est vide mais qu'on a un login (ex: pas de département),
        // on met quand même le login dans allSids pour la requête DRAFT
        if (allSids.isEmpty()) {
            allSids.add(userLogin);
        }

        // 4) Construire le Pageable (pageIndex commence à 1 côté client)
        Pageable pageable = PageRequest.of(
            Math.max(0, pageIndex - 1),
            pageSize
        );

        int readMask = BasePermission.READ.getMask();
        String loginForQuery = userLogin != null ? userLogin : "";

        // 5) Requête UNION paginée sur les IDs uniquement
// Après : convertir Number -> Long explicitement
        Page<Number> idsPage = correspRepository.findAllIdsByAclForSidsOrDraft(
            allSids, readMask, loginForQuery, pageable
        );

        if (idsPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> ids = idsPage.getContent()
            .stream()
            .map(Number::longValue)   // <-- conversion sûre quel que soit le type numérique retourné
            .collect(Collectors.toList());

        List<Corresp> corresps = correspRepository.findAllByIds(ids);

        // 7) Maintenir l'ordre de la page (la requête native trie, mais findAllByIds peut changer l'ordre)
        Map<Long, Corresp> correspMap = corresps.stream()
            .collect(Collectors.toMap(Corresp::getId, c -> c));
        List<Corresp> orderedCorresps = ids.stream()
            .map(correspMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 8) Mapper en DTO et retourner la Page avec le bon totalElements
        List<CorrespDTO> dtos = orderedCorresps.stream()
            .map(correspMapper::toDto)
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, idsPage.getTotalElements());
    }


    @Transactional
    public CorrespPermissionsDTO getAndEnsurePermissionsForCurrentUser(Long correspId) {
        Corresp corresp = correspRepository.findById(correspId)
            .orElseThrow(() -> new RuntimeException("Corresp not found with id " + correspId));

        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        Long userId = null;

        // try to get userId from gateway
        if (login != null) {
            try {
                UserFeignDTO userDto = correspGatewayRestClient.findUserByLogin(login);
                if (userDto != null) userId = userDto.getId();
            } catch (Exception e) {
                log.debug("Cannot get user by login {} : {}", login, e.getMessage());
            }
        }

        CorrespPermissionsDTO result = new CorrespPermissionsDTO();
        PermissionDTO correspPerm = new PermissionDTO();
        Map<Long, PermissionDTO> taskPerms = new HashMap<>();

        // 1) direct checks for principal
        if (login != null) {
            PrincipalSid principal = new PrincipalSid(login.trim().toLowerCase());
            correspPerm.setCanRead(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.READ));
            correspPerm.setCanWrite(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.WRITE));
            correspPerm.setCanDelete(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.DELETE));
            correspPerm.setCanAdmin(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.ADMINISTRATION));
        }

        if (corresp.getTasks() != null) {
            for (Tasks t : corresp.getTasks()) {
                PermissionDTO p = new PermissionDTO();
                if (login != null) {
                    PrincipalSid principal = new PrincipalSid(login.trim().toLowerCase());
                    p.setCanRead(aclUtilService.hasPermissionForSid(t, principal, BasePermission.READ));
                    p.setCanWrite(aclUtilService.hasPermissionForSid(t, principal, BasePermission.WRITE));
                    p.setCanDelete(aclUtilService.hasPermissionForSid(t, principal, BasePermission.DELETE));
                    p.setCanAdmin(aclUtilService.hasPermissionForSid(t, principal, BasePermission.ADMINISTRATION));
                }
                taskPerms.put(t.getId(), p);
            }
        }

        // =====================================================================
        // FIX : Ne court-circuiter QUE si la CORRESP elle-même a des permissions.
        // Si seules les tâches ont des permissions, continuer la vérification
        // des départements pour résoudre les permissions de la corresp.
        // =====================================================================
        boolean correspHasDirectPerms = correspPerm.isCanRead() || correspPerm.isCanWrite()
            || correspPerm.isCanDelete() || correspPerm.isCanAdmin();

        if (correspHasDirectPerms) {
            // La corresp a des permissions directes pour le PrincipalSid => on peut retourner
            result.setCorresp(correspPerm);
            result.setTasksPermissions(taskPerms);
            return result;
        }

        // La corresp n'a PAS de permissions directes.
        // On doit vérifier les départements même si les tâches ont des permissions.

        // 2) check user's departments via OrgaCare using userId
        List<String> deptCodes = new ArrayList<>();
        if (userId != null) {
            try {
                List<Map<String,Object>> aff = orgaCareRestClient.getDepartementAffectationsByUserId(userId);
                if (aff != null) {
                    for (Map<String,Object> m : aff) {
                        Object codeObj = m.get("departementCode");
                        if (codeObj != null) {
                            String code = codeObj.toString().trim();
                            if (!code.isEmpty()) deptCodes.add(code);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("OrgaCare call failed for userId {}: {}", userId, e.getMessage());
            }
        }

        if (deptCodes.isEmpty()) {
            // Aucun département trouvé. Retourner les permissions telles quelles.
            result.setCorresp(correspPerm);
            result.setTasksPermissions(taskPerms);
            return result;
        }

        // 3) for each department, if dept has perms then apply same perms to current principal
        if (login != null) {
            PrincipalSid principal = new PrincipalSid(login.trim().toLowerCase());
            for (String dept : deptCodes) {
                if (dept == null || dept.isBlank()) continue;
                GrantedAuthoritySid auth = new GrantedAuthoritySid(dept.trim());

                // corresp perms
                boolean dRead = aclUtilService.hasPermissionForSid(corresp, auth, BasePermission.READ);
                boolean dWrite = aclUtilService.hasPermissionForSid(corresp, auth, BasePermission.WRITE);
                boolean dDelete = aclUtilService.hasPermissionForSid(corresp, auth, BasePermission.DELETE);
                boolean dAdmin = aclUtilService.hasPermissionForSid(corresp, auth, BasePermission.ADMINISTRATION);

                try {
                    if (dRead) aclUtilService.addPermissionIfNotExists(corresp, principal, BasePermission.READ);
                    if (dWrite) aclUtilService.addPermissionIfNotExists(corresp, principal, BasePermission.WRITE);
                    if (dDelete) aclUtilService.addPermissionIfNotExists(corresp, principal, BasePermission.DELETE);
                    if (dAdmin) aclUtilService.addPermissionIfNotExists(corresp, principal, BasePermission.ADMINISTRATION);
                } catch (Exception e) {
                    log.warn("Cannot apply corresp perms from dept {} to user {}: {}", dept, login, e.getMessage());
                }

                // tasks perms
                if (corresp.getTasks() != null) {
                    for (Tasks t : corresp.getTasks()) {
                        boolean tdRead = aclUtilService.hasPermissionForSid(t, auth, BasePermission.READ);
                        boolean tdWrite = aclUtilService.hasPermissionForSid(t, auth, BasePermission.WRITE);
                        boolean tdDelete = aclUtilService.hasPermissionForSid(t, auth, BasePermission.DELETE);
                        boolean tdAdmin = aclUtilService.hasPermissionForSid(t, auth, BasePermission.ADMINISTRATION);
                        try {
                            if (tdRead) aclUtilService.addPermissionIfNotExists(t, principal, BasePermission.READ);
                            if (tdWrite) aclUtilService.addPermissionIfNotExists(t, principal, BasePermission.WRITE);
                            if (tdDelete) aclUtilService.addPermissionIfNotExists(t, principal, BasePermission.DELETE);
                            if (tdAdmin) aclUtilService.addPermissionIfNotExists(t, principal, BasePermission.ADMINISTRATION);
                        } catch (Exception e) {
                            log.warn("Cannot apply task perms from dept {} to user {} for task {}: {}", dept, login, t.getId(), e.getMessage());
                        }
                    }
                }
            } // end for dept
        }

        // 4) recompute and return final permissions (after potential adds)
        PermissionDTO correspAfter = new PermissionDTO();
        if (login != null) {
            PrincipalSid principal = new PrincipalSid(login.trim().toLowerCase());
            correspAfter.setCanRead(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.READ));
            correspAfter.setCanWrite(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.WRITE));
            correspAfter.setCanDelete(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.DELETE));
            correspAfter.setCanAdmin(aclUtilService.hasPermissionForSid(corresp, principal, BasePermission.ADMINISTRATION));
        }

        Map<Long, PermissionDTO> taskPermsAfter = new HashMap<>();
        if (corresp.getTasks() != null) {
            for (Tasks t : corresp.getTasks()) {
                PermissionDTO p = new PermissionDTO();
                if (login != null) {
                    PrincipalSid principal = new PrincipalSid(login.trim().toLowerCase());
                    p.setCanRead(aclUtilService.hasPermissionForSid(t, principal, BasePermission.READ));
                    p.setCanWrite(aclUtilService.hasPermissionForSid(t, principal, BasePermission.WRITE));
                    p.setCanDelete(aclUtilService.hasPermissionForSid(t, principal, BasePermission.DELETE));
                    p.setCanAdmin(aclUtilService.hasPermissionForSid(t, principal, BasePermission.ADMINISTRATION));
                }
                taskPermsAfter.put(t.getId(), p);
            }
        }

        result.setCorresp(correspAfter);
        result.setTasksPermissions(taskPermsAfter);
        return result;
    }

    /**
     * Get all corresps with status DISPATCH.
     *
     * @return list of CorrespDTO
     */
    @Transactional(readOnly = true)
    public List<CorrespDTO> findAllByStatusDispatch() {
        log.debug("Request to get all Corresps with status DISPATCH");
        return correspRepository.findAllByStatusDispatch()
            .stream()
            .map(correspMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CorrespSummaryDTO findCorrespSummaryByTaskId(Long taskId) {
        return tasksRepository
            .findById(taskId)
            .map(task -> task.getCorresps().stream().findFirst()
                .map(c -> new CorrespSummaryDTO(c.getSubject(), c.getReference()))
                .orElse(new CorrespSummaryDTO()))
            .orElse(new CorrespSummaryDTO());
    }

    @Transactional(readOnly = true)
    public Map<Long, CorrespSummaryDTO> findCorrespSummariesByTaskIds(List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) return Collections.emptyMap();
        Map<Long, CorrespSummaryDTO> result = new HashMap<>();
        for (Long taskId : taskIds) {
            result.put(taskId, findCorrespSummaryByTaskId(taskId));
        }
        return result;
    }

}
