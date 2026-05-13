package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.client.ConfigManageRestClient;
import com.africom.correspmanage.client.ModelNotifDTO;
import com.africom.correspmanage.client.TypeDocumentFeignDTO;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.MessageRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.CorrespEmailService;
import com.africom.correspmanage.service.CorrespService;
import com.africom.correspmanage.service.EmailSendService;
import com.africom.correspmanage.service.TemplateRenderService;
import com.africom.correspmanage.service.dto.*;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * REST controller for managing {@link com.africom.correspmanage.domain.Corresp}.
 */
@RestController
@RequestMapping("/api")
public class CorrespResource {

    private final Logger log = LoggerFactory.getLogger(CorrespResource.class);

    private static final String ENTITY_NAME = "correspManageCorresp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConfigManageRestClient configManageRestClient;
    private final CorrespService correspService;

    private final CorrespRepository correspRepository;
    private final MessageRepository messageRepository;
    private final CorrespEmailService correspEmailService;
    private final TemplateRenderService templateRenderService;
    private final EmailSendService emailSendService;


    public CorrespResource(
        ConfigManageRestClient configManageRestClient,
        CorrespService correspService,
        CorrespRepository correspRepository,
        MessageRepository messageRepository,
        CorrespEmailService correspEmailService,
        TemplateRenderService templateRenderService,
        EmailSendService emailSendService
    ) {
        this.configManageRestClient = configManageRestClient;
        this.correspService = correspService;
        this.correspRepository = correspRepository;
        this.messageRepository = messageRepository;
        this.correspEmailService = correspEmailService;
        this.templateRenderService = templateRenderService;
        this.emailSendService = emailSendService;
    }

    /**
     * {@code POST  /corresps} : Create a new corresp.
     *
     * @param correspDTO the correspDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new correspDTO, or with status {@code 400 (Bad Request)} if the corresp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/corresps")
    public ResponseEntity<CorrespDTO> createCorresp(@Valid @RequestBody CorrespDTO correspDTO) throws URISyntaxException {
        log.debug("REST request to save Corresp : {}", correspDTO);
        if (correspDTO.getId() != null) {
            throw new BadRequestAlertException("A new corresp cannot already have an ID", ENTITY_NAME, "idexists");
        }

        correspDTO.setIdinstance(UUID.randomUUID());
        correspDTO.setStatus(CorrespStatus.DRAFT);
        correspDTO.setDateCreated(ZonedDateTime.now());
        correspDTO.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
        //        correspDTO.setStatus(CorrespStatus.IN_PROGRESS);

        CorrespDTO result = correspService.save(correspDTO);
        return ResponseEntity
            .created(new URI("/api/corresps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /corresps/:id} : Updates an existing corresp.
     *
     * @param id         the id of the correspDTO to save.
     * @param correspDTO the correspDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated correspDTO,
     * or with status {@code 400 (Bad Request)} if the correspDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the correspDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/corresps/{id}")
    public ResponseEntity<CorrespDTO> updateCorresp(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CorrespDTO correspDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Corresp : {}, {}", id, correspDTO);
        if (correspDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, correspDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!correspRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CorrespDTO result = correspService.update(correspDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, correspDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /corresps/:id} : Partial updates given fields of an existing corresp, field will ignore if it is null
     *
     * @param id         the id of the correspDTO to save.
     * @param correspDTO the correspDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated correspDTO,
     * or with status {@code 400 (Bad Request)} if the correspDTO is not valid,
     * or with status {@code 404 (Not Found)} if the correspDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the correspDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/corresps/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CorrespDTO> partialUpdateCorresp(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CorrespDTO correspDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Corresp partially : {}, {}", id, correspDTO);
        if (correspDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, correspDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!correspRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CorrespDTO> result = correspService.partialUpdate(correspDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, correspDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /corresps} : get all the corresps.
     *
     * @param pageable  the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of corresps in body.
     */
    @GetMapping("/corresps")
    public ResponseEntity<List<CorrespDTO>> getAllCorresps(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Corresps");
        Page<CorrespDTO> page;
        if (eagerload) {
            page = correspService.findAllWithEagerRelationships(pageable);
        } else {
            page = correspService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /corresps/:id} : get the "id" corresp.
     *
     * @param id the id of the correspDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the correspDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/corresps/{id}")
    public ResponseEntity<CorrespDTO> getCorresp(@PathVariable Long id) {
        log.debug("REST request to get Corresp : {}", id);
        Optional<CorrespDTO> correspDTO = correspService.findOne(id);
        return ResponseUtil.wrapOrNotFound(correspDTO);
    }

    @GetMapping("/correspsByType-page")
    public Page<CorrespDTO> getCorrespByType(
        @RequestParam(name = "correspType") String correspType,
        @RequestParam(name = "pageIndex") Integer pageIndex,
        @RequestParam(name = "pageSize") Integer pageSize
    ) {
        log.debug("REST request to get Corresps by type : {}", correspType);
        return correspService.findAllCorrespByType(correspType, pageIndex, pageSize);
    }

    @GetMapping("/corresps-page")
    public Page<CorrespDTO> getCorrespPage(
        @RequestParam(name = "pageIndex") Integer pageIndex,
        @RequestParam(name = "pageSize") Integer pageSize
    ) {
        log.debug("REST request to get Corresps by type");
        return correspService.findAllCorresps(pageIndex, pageSize);
    }

//    @GetMapping("/corresps-filter")
//    public Page<CorrespDTO> getRapportRequestSpec(
//        @RequestParam(name = "pageIndex") Integer pageIndex,
//        @RequestParam(name = "pageSize") Integer pageSize,
//        @RequestParam(name = "reference", required = false) String reference,
//        @RequestParam(name = "status", required = false) String status,
//        @RequestParam(name = "dispatchType", required = false) String dispatchType,
//        @RequestParam(name = "startDate", required = false) String startDate,
//        @RequestParam(name = "endDate", required = false) String endDate
//    ) {
//        return correspService.findAllCorrespByFilter(reference, status, dispatchType, startDate, endDate, pageIndex, pageSize);
//    }
@GetMapping("/corresps-filter")
public ResponseEntity<Map<String, Object>> getRapportRequestSpec(
    @RequestParam(name = "userId") Long userId,
    @RequestParam(name = "pageIndex", defaultValue = "1") Integer pageIndex,
    @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize,
    @RequestParam(name = "reference", required = false) String reference,
    @RequestParam(name = "status", required = false) String status,
    @RequestParam(name = "dispatchType", required = false) String dispatchType,
    @RequestParam(name = "startDate", required = false) String startDate,
    @RequestParam(name = "endDate", required = false) String endDate
) {
    log.debug("REST request corresps-filter userId={} page={} size={}", userId, pageIndex, pageSize);

    Page<CorrespDTO> page = correspService.findAllCorrespByFilter(
        userId, reference, status, dispatchType, startDate, endDate, pageIndex, pageSize
    );

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("content",     page.getContent());
    response.put("currentPage", pageIndex);
    response.put("pageSize",    pageSize);
    response.put("totalItems",  page.getTotalElements());
    response.put("totalPages",  page.getTotalPages());
    response.put("hasNext",     page.hasNext());
    response.put("hasPrevious", page.hasPrevious());

    return ResponseEntity.ok(response);
}

    /**
     * {@code DELETE  /corresps/:id} : delete the "id" corresp.
     *
     * @param id the id of the correspDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/corresps/{id}")
    public ResponseEntity<Void> deleteCorresp(@PathVariable Long id) {
        log.debug("REST request to delete Corresp : {}", id);
        correspService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping(value = "/correspsWithTasksAndPjs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Corresp saveCorrespWithTasksAndPj(
        @RequestPart("correspDTO") CorrespDTO correspDTO,
        @RequestPart("tasksDTOS") List<TasksDTO> tasksDTOS,
        @RequestPart("pjs") List<Pj> pjs,
        @RequestPart("typeDoc") TypeDocumentFeignDTO typeDocumentDTO,
        @RequestPart("withRef") Boolean withRef
    ) {
        boolean generateRef = withRef != null && withRef;
        return correspService.saveCorrespWithTasksAndPj(correspDTO, tasksDTOS, pjs, typeDocumentDTO, generateRef);
    }

    //    @PostMapping(value = "/correspsWithTasksAndPjsN8N", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //    public void saveCorrespWithTasksAndPj(
    //        @RequestPart("pjs") String pjs,
    //        @RequestPart("subject") String subject,
    //        @RequestPart("from") String from,
    //        @RequestPart("bodyHtml") String bodyHtml
    //    ) {
    ////        return correspService.saveCorrespWithPjN8N(pjs,subject,from,bodyHtml);
    //        log.debug("pjs {}",pjs);
    //        log.debug("pjs {}",subject);
    //        log.debug("pjs {}",from);
    //        log.debug("pjs {}",bodyHtml);
    //    }

    /**
     * {@code GET  /corresps/:correspId/message} : get receiver and copyTo of the message linked to a corresp.
     *
     * @param correspId the id of the corresp.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing receiver and copyTo,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/corresps/{correspId}/message")
    public ResponseEntity<Map<String, Object>> getMessageByCorrespId(@PathVariable Long correspId) {
        log.debug("REST request to get Message (receiver, copyTo) by correspId : {}", correspId);
        Map<String, Object> result = correspService.getMessageByCorrespId(correspId);
        return ResponseEntity.ok(result);
    }


    @PostMapping(value = "/correspsWithTasksAndPjsN8N", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveCorrespWithTasksAndPj(
        @RequestPart(value = "pjs", required = false) String pjs,
        @RequestPart("subject") String subject,
        @RequestPart("from") String from,
        @RequestPart("bodyHtml") String bodyHtml,
        @RequestPart(value = "receiver", required = false) String receiverRaw,
        @RequestPart(value = "copyTo", required = false) String copyToRaw,
        @RequestPart(value = "status", required = false) String status,
        @RequestPart(value = "corresptype", required = false) String corresptype,
        @RequestPart(value = "dispatchType", required = false) String dispatchType,
        @RequestPart(value = "taskObject", required = false) String taskObject,
        @RequestPart(value = "taskType", required = false) String taskType,
        @RequestPart(value = "taskResponsableCodeDept", required = false) String taskResponsableCodeDept,
        @RequestPart(value = "codeUniqueSociete", required = false) String codeUniqueSociete,
        @RequestPart(value = "nomSociete", required = false) String nomSociete,      // ← AJOUT
        @RequestPart(value = "idSociete", required = false) String idSocieteStr      // ← AJOUT (String car multipart)
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<PjDTO> pjList = Collections.emptyList();
            if (pjs != null && !pjs.trim().isEmpty()) {
                pjList = objectMapper.readValue(pjs, new TypeReference<List<PjDTO>>() {});
            }

            Object receiverObj = null;
            Object copyToObj = null;

            if (receiverRaw != null && !receiverRaw.trim().isEmpty()) {
                String t = receiverRaw.trim();
                if (t.startsWith("[") || t.startsWith("{")) {
                    receiverObj = objectMapper.readValue(t, new TypeReference<List<Map<String, String>>>() {});
                } else {
                    receiverObj = receiverRaw;
                }
            }

            if (copyToRaw != null && !copyToRaw.trim().isEmpty()) {
                String t = copyToRaw.trim();
                if (t.startsWith("[") || t.startsWith("{")) {
                    copyToObj = objectMapper.readValue(t, new TypeReference<List<Map<String, String>>>() {});
                } else {
                    copyToObj = copyToRaw;
                }
            }

            Long idSociete = null;
            if (idSocieteStr != null && !idSocieteStr.isBlank()) {
                try { idSociete = Long.parseLong(idSocieteStr.trim()); }
                catch (NumberFormatException e) {
                    log.warn("idSociete invalide reçu de N8N: {}", idSocieteStr);
                }
            }

            Corresp saved = correspService.saveCorrespWithPjN8N(
                pjList, subject, from, bodyHtml,
                receiverObj, copyToObj, status, corresptype,
                dispatchType, taskObject, taskType, taskResponsableCodeDept,
                codeUniqueSociete, nomSociete, idSociete     // ← AJOUT
            );
            log.debug("Corresp N8N créée avec id={}", saved.getId());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Erreur lors du traitement du webhook N8N", ex);
            return ResponseEntity.status(500).build();
        }
    }


    @GetMapping("/correspAcl")
    public List<Corresp> getCorrespAcl(@RequestParam(required = false) String deptCode) {
        return correspService.getListByAcl(deptCode);
    }

    @PatchMapping("/corresps/{id}/generateReference")
    public ResponseEntity<CorrespDTO> generateReference(@PathVariable Long id) {
        Optional<Corresp> correspOpt = correspRepository.findById(id);
        if (correspOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Corresp corresp = correspOpt.get();

        // ✅ Passer le codeUniqueSociete au service de génération
        String codeUniqueSociete = corresp.getCodeUniqueSociete();
        String generatedReference;
        try {
            generatedReference = configManageRestClient.getNextNumber(
                corresp.getCorresptype().name(),
                codeUniqueSociete
            );
        } catch (Exception e) {
            log.error("Erreur génération référence pour correspId={} type={} societe={} : {}",
                id, corresp.getCorresptype().name(), codeUniqueSociete, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        corresp.setReference(generatedReference);
        corresp.setUpdatedDate(ZonedDateTime.now());
        corresp.setUpdatedBy(SecurityUtils.getCurrentUserLogin().get());

        Corresp saved = correspRepository.save(corresp);

        CorrespDTO dto = new CorrespDTO();
        dto.setId(saved.getId());
        dto.setReference(saved.getReference());

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/corresps/update/{id}")
    public ResponseEntity<Corresp> updateCorrespDraft(
        @PathVariable Long id,
        @RequestPart("correspDTO") CorrespDTO correspDTO,
        @RequestPart("typeDocumentDTO") TypeDocumentFeignDTO typeDocumentDTO
    ) {
        Corresp updated = correspService.updateCorrespDraft(id, correspDTO, typeDocumentDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/correspAclPage")
    public Page<Corresp> getCorrespAcl(
        @RequestParam(required = false, name = "deptCode") String deptCode,
        @RequestParam(name = "page") int page,
        @RequestParam(name = "size") int size
    ) {
        return correspService.getListByAclPage(deptCode, page, size);
    }

//    @PostMapping(value = "/correspsAddTaskToCorresp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Corresp addTaskToCorresp(@RequestParam("correspId") Long correspId, @RequestPart("tasksDTO") TasksDTO tasksJson)
//        throws JsonProcessingException {
//        //        ObjectMapper mapper = new ObjectMapper();
//        //        TasksDTO tasksDTO = mapper.readValue(tasksJson, TasksDTO.class);
//
//        return correspService.addTaskToExistingCorresp(tasksJson, correspId);
//    }
//@PostMapping(value = "/corresps/{correspId}/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
//public ResponseEntity<Corresp> addTaskToCorrespJson(
//    @PathVariable("correspId") Long correspId,
//    @RequestBody TasksDTO tasksDTO
//) {
//    Corresp saved = correspService.addTaskToExistingCorresp(tasksDTO, correspId);
//    return ResponseEntity.ok(saved);
//}
@PostMapping("/corresps/{correspId}/tasks")
public ResponseEntity<?> addTaskToCorresp(
    @PathVariable Long correspId,
    @Valid @RequestBody TasksDTO tasksDTO
) {
    try {
        Corresp updated = correspService.addTaskToExistingCorresp(tasksDTO, correspId);
        // Option 1 : retourner l'entité mise à jour
        return ResponseEntity.ok(updated);
        // Option 2 (préférée si tu as un CorrespMapper) :
        // CorrespDTO dto = correspMapper.toDto(updated);
        // return ResponseEntity.ok(dto);
    } catch (RuntimeException e) {
        // exemple: "Corresp not found with id ..." => 404
        String msg = e.getMessage() != null ? e.getMessage() : "Internal error";
        if (msg.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }
}



    @PostMapping(value = "/correspsAddPjToCorresp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Corresp addPjToCorresp(@RequestParam("correspId") Long correspId, @RequestPart("pjDTO") PjDTO pjJson)
        throws JsonProcessingException {
        //        ObjectMapper mapper = new ObjectMapper();
        //        TasksDTO tasksDTO = mapper.readValue(tasksJson, TasksDTO.class);

        return correspService.addPjToExistingCorresp(pjJson, correspId);
    }

    @PatchMapping("/correspsChangeTaskStatus")
    public Corresp changeTaskStatus(
        @RequestParam(name = "correspId") Long correspId,
        @RequestParam(name = "taskId") Long taskId,
        @RequestParam(name = "status") String status
    ) {
        return correspService.changeTaskStatus(correspId, taskId, status);
    }

    @PatchMapping("/correspsSoumettreCorresp")
    public Corresp soumettreCorresp(@RequestParam(name = "correspId") Long correspId) {
        return correspService.soumettreCorresp(correspId);
    }

    @PatchMapping("/correspsCloturerCorresp")
    public Corresp cloturerCorresp(@RequestParam(name = "correspId") Long correspId) {
        return correspService.cloturerCorresp(correspId);
    }

    @PostMapping("/correspondencesFromN8n")
    public ResponseEntity<?> receiveCorrespondence(@RequestBody Map<String, Object> payload) {
        log.debug("📩 Received correspondence payload from n8n:");
        log.debug(payload.toString());

        return ResponseEntity.ok("Received");
    }

    //    @PatchMapping("/corresps/saveBodyToFile")
    //    public void saveBodyToFile(@RequestParam(name = "richTextContent") String richTextContent,@RequestParam(name = "correspId") Long correspId) {
    //        correspService.saveBodyToFile(richTextContent, correspId,"D:\\pjs\\2025\\DECEMBER\\2025-12-09\\3502.html");
    //    }

    @GetMapping("/corresps/loadBodyFromFile")
    public ResponseEntity<String> loadBodyFromFile(@RequestParam(name = "correspId") Long correspId) {
        return messageRepository.findByCorrespIdAndMessageType(correspId, "COURRIER")
            .map(msg -> ResponseEntity.ok(correspService.loadBodyFromFile(msg.getPath())))
            .orElseGet(() ->
                // Fallback anciens enregistrements sans messageType
                messageRepository.findByCorrespId(correspId)
                    .map(msg -> ResponseEntity.ok(correspService.loadBodyFromFile(msg.getPath())))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found"))
            );
    }

    @DeleteMapping("/correspsDelete/{correspId}")
    public void deleteCorrespWithTasksAndPjs(@PathVariable(name = "correspId") Long correspId) {
        correspService.deleteCorrespWithTasksAndPjs(correspId);
    }

    @GetMapping("/corresps/by-task/{taskId}")
    public ResponseEntity<List<CorrespDTO>> getCorrespsByTask(@PathVariable Long taskId) {
        log.debug("REST request to get Corresps by taskId : {}", taskId);
        List<CorrespDTO> corresps = correspService.findCorrespsByTaskId(taskId);
        return ResponseEntity.ok(corresps);
    }

    /**
     * GET  /corresps/with-reference?page=1&size=20
     * Retourne une page de corresps ayant une reference non-nulle.
     */
    @GetMapping("/corresps/with-reference")
    public ResponseEntity<Page<CorrespDTO>> getCorrespsWithReference(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<CorrespDTO> result = correspService.findAllWithReference(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * GET  /corresps/with-reference/list
     * Retourne la liste complète (non paginée) de corresps ayant une reference non-nulle.
     * Attention : peut être volumineux en production.
     */
    @GetMapping("/corresps/with-reference/list")
    public ResponseEntity<List<CorrespDTO>> getAllCorrespsWithReference() {
        List<CorrespDTO> list = correspService.findAllWithReferenceList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/corresps/references")
    public ResponseEntity<Map<String, Object>> getCorrespReferences(
        @RequestParam(value = "page",   defaultValue = "1")  int page,
        @RequestParam(value = "size",   defaultValue = "20") int size,
        @RequestParam(value = "search", required = false)    String search
    ) {
        Page<CorrespReferenceDTO> result =
            correspService.findReferencesPagedProjected(page, size, search);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content",     result.getContent());
        response.put("currentPage", page);
        response.put("pageSize",    size);
        response.put("totalItems",  result.getTotalElements());
        response.put("totalPages",  result.getTotalPages());
        response.put("hasNext",     result.hasNext());
        response.put("hasPrevious", result.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/corresps/{id}/permissions")
    public ResponseEntity<CorrespPermissionsDTO> getPermissions(@PathVariable Long id) {
        CorrespPermissionsDTO dto = correspService.getAndEnsurePermissionsForCurrentUser(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * GET /corresps/correspsByUserAcl?userId={userId}
     * Retourne les correspondances accessibles par l'utilisateur via ses ACLs (départements + login).
     */
    @GetMapping("/corresps/correspsByUserAcl")
    public ResponseEntity<List<CorrespDTO>> getCorrespsByUserAcl(@RequestParam(name = "userId") Long userId) {
        log.debug("REST request to get Corresps by userId (ACL) : {}", userId);
        List<CorrespDTO> corresps = correspService.getAllCorrespsByUserAcl(userId);
        return ResponseEntity.ok(corresps != null ? corresps : Collections.emptyList());
    }

    /**
     * GET /corresps/correspsByUserAclPaged?userId={userId}&page=1&size=15
     * Retourne les correspondances accessibles paginées (ACL + DRAFT).
     */
    @GetMapping("/corresps/correspsByUserAclPaged")
    public ResponseEntity<Map<String, Object>> getCorrespsByUserAclPaged(
        @RequestParam(name = "userId") Long userId,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "15") int size
    ) {
        log.debug("REST request to get Corresps paged by userId (ACL) : {}, page={}, size={}", userId, page, size);

        Page<CorrespDTO> correspPage = correspService.getAllCorrespsByUserAclPaged(userId, page, size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", correspPage.getContent());
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalItems", correspPage.getTotalElements());
        response.put("totalPages", correspPage.getTotalPages());
        response.put("hasNext", correspPage.hasNext());
        response.put("hasPrevious", correspPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /corresps/dispatched
     * Retourne la liste des correspondances avec le statut DISPATCH.
     */
    @GetMapping("/corresps/dispatched")
    public ResponseEntity<List<CorrespDTO>> getAllDispatchedCorresps() {
        log.debug("REST request to get all Corresps with status DISPATCH");
        List<CorrespDTO> corresps = correspService.findAllByStatusDispatch();
        return ResponseEntity.ok(corresps);
    }

    @GetMapping("/corresps/by-task/{taskId}/summary")
    public ResponseEntity<CorrespSummaryDTO> getCorrespSummaryByTask(@PathVariable Long taskId) {
        log.debug("REST request to get Corresp summary (subject+reference) by taskId : {}", taskId);
        CorrespSummaryDTO summary = correspService.findCorrespSummaryByTaskId(taskId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/corresps/summaries-by-tasks")
    public ResponseEntity<Map<Long, CorrespSummaryDTO>> getCorrespSummariesByTasks(
        @RequestBody List<Long> taskIds
    ) {
        return ResponseEntity.ok(correspService.findCorrespSummariesByTaskIds(taskIds));
    }

    @PostMapping("/corresps/send-email")
    public ResponseEntity<Map<String, Object>> prepareCorrespEmail(
        @RequestBody SendCorrespEmailRequestDTO request
    ) {
        log.debug("REST request to prepare email for correspId={}", request.getCorrespId());
        try {
            Message message = correspEmailService.prepareAndPersistEmail(request);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("messageId", message.getId());
            response.put("status", "PENDING");
            response.put("correspId", request.getCorrespId());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la préparation de l'email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de la préparation de l'email : " + e.getMessage()));
        }
    }

    @PostMapping("/corresps/preview-email")
    public ResponseEntity<Map<String, Object>> previewCorrespEmail(
        @RequestBody SendCorrespEmailRequestDTO request
    ) {
        log.debug("REST request to preview email for modelNotifId={}", request.getModelNotifId());
        try {
            // 1) Récupérer le ModelNotif
            ModelNotifDTO modelNotif = configManageRestClient.getModelNotifById(request.getModelNotifId());
            if (modelNotif == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ModelNotif introuvable"));
            }

            // 2) Construire le contexte FreeMarker à partir du request
            Map<String, Object> ctx = new HashMap<>();
            ctx.put("reference",         request.getCorrespReference() != null ? request.getCorrespReference() : "");
            ctx.put("objet",             request.getCorrespObjet() != null ? request.getCorrespObjet() : "");
            ctx.put("date",              request.getCorrespDate() != null ? request.getCorrespDate() : "");
            ctx.put("destinataire",      request.getDestinataireLabel() != null ? request.getDestinataireLabel() : "");
            ctx.put("corps",             request.getCorrespBody() != null ? request.getCorrespBody() : "");
            ctx.put("expediteur",        request.getSenderDepartement() != null ? request.getSenderDepartement() : "");
            ctx.put("nomComplet",        request.getSenderFullName() != null ? request.getSenderFullName() : "");
            ctx.put("departement",       request.getSenderDepartement() != null ? request.getSenderDepartement() : "");
            ctx.put("emailEmetteur",     request.getSenderEmail() != null ? request.getSenderEmail() : "");
            ctx.put("emailDestinataire", request.getReceiver() != null ? request.getReceiver() : "");
            ctx.put("emailCopie",        request.getCopyTo() != null ? request.getCopyTo() : "");
            ctx.put("dateAujourdhui",    java.time.ZonedDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // 3) Rendre le template
            // Utiliser le bean injecté (déjà disponible dans le controller)
// templateRenderService est déjà un champ injecté — supprimer le "new"
            String renderedHtml  = templateRenderService.render(modelNotif.getContent(), ctx);
            String renderedSubject = modelNotif.getSubjectFormula() != null
                ? templateRenderService.render(modelNotif.getSubjectFormula(), ctx)
                : request.getCorrespObjet();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("html",    renderedHtml);
            response.put("subject", renderedSubject);
            response.put("from",    request.getSenderEmail());
            response.put("to",      request.getReceiver());
            response.put("cc",      request.getCopyTo());
            response.put("modelName", modelNotif.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur preview email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur : " + e.getMessage()));
        }
    }

    // Endpoint principal d'envoi (depuis message persisté)
    @PostMapping("/corresps/send-email/{messageId}")
    public ResponseEntity<Map<String, Object>> sendEmailFromMessage(
        @PathVariable Long messageId,
        @RequestBody Map<String, String> body
    ) {
        String subject = body.getOrDefault("subject", "Correspondance");
        Message sent = emailSendService.sendEmailFromMessage(messageId, subject);
        return ResponseEntity.ok(Map.of(
            "messageId", sent.getId(),
            "status",    sent.getEtat(),
            "sender",    sent.getSender(),
            "msgID",     sent.getMsgID() != null ? sent.getMsgID() : "",
            "sent",      "SENT".equals(sent.getEtat())
        ));
    }

    // Endpoint de test (temporaire)
// Endpoint de test — utilise la config SMTP en base
    @PostMapping("/corresps/test-email")
    public ResponseEntity<Map<String, Object>> sendTestEmail(
        @RequestParam(defaultValue = "trabelsimedamine999@gmail.com") String to
    ) {
        try {
            emailSendService.sendTestEmail(to);
            return ResponseEntity.ok(Map.of(
                "sent",   true,
                "to",     to,
                "status", "SUCCESS"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "sent",   false,
                "to",     to,
                "status", "ERROR",
                "error",  e.getMessage()
            ));
        }
    }

    /**
     * GET /api/corresps/{correspId}/body
     * Retourne le corps HTML brut (message DRAFT) de la correspondance.
     */
    @GetMapping("/corresps/{correspId}/body")
    public ResponseEntity<String> getCorrespBody(@PathVariable Long correspId) {
        return messageRepository.findByCorrespIdAndMessageType(correspId, "COURRIER")
            .map(msg -> ResponseEntity.ok(correspService.loadBodyFromFile(msg.getPath())))
            .orElseGet(() ->
                // fallback : essayer sans type pour les anciens enregistrements
                messageRepository.findByCorrespId(correspId)
                    .map(msg -> ResponseEntity.ok(correspService.loadBodyFromFile(msg.getPath())))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(""))
            );
    }

    /**
     * POST /corresps/send-email-direct
     * Envoie un email sans message persisté (saveCopy=false).
     * Prépare et envoie en une seule opération sans sauvegarder en base.
     */
    @PostMapping("/corresps/send-email-direct")
    public ResponseEntity<Map<String, Object>> sendEmailDirect(
        @RequestBody SendCorrespEmailRequestDTO request
    ) {
        log.debug("REST request to send email direct (no persist) for correspId={}", request.getCorrespId());
        try {
            // Préparer le message (sans persister)
            Message message = correspEmailService.prepareAndPersistEmail(request);

            // Envoyer directement
            String subject = request.getCorrespObjet() != null
                ? "[Correspondance] " + request.getCorrespObjet()
                : "Correspondance";
            emailSendService.sendEmailDirect(message, subject);

            return ResponseEntity.ok(Map.of(
                "sent", true,
                "correspId", request.getCorrespId(),
                "status", "SENT"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur envoi email direct", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur : " + e.getMessage()));
        }
    }

    /**
     * GET /api/corresps/my-societes
     * Retourne les sociétés (id + raisonSociale) du user connecté via son login/matricule.
     */
    @GetMapping("/corresps/my-societes")
    public ResponseEntity<List<Map<String, Object>>> getMySocietes() {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<Map<String, Object>> societes = correspService.getSocietesByCurrentUserLogin(login);
        return ResponseEntity.ok(societes);
    }

    /**
     * GET /api/corresps/search?q=...&userId=...&page=1&size=15
     * Recherche rapide par reference, subject ou sender.
     */
    @GetMapping("/corresps/search")
    public ResponseEntity<Map<String, Object>> searchCorresps(
        @RequestParam(name = "q",       required = false, defaultValue = "") String q,
        @RequestParam(name = "userId")  Long userId,
        @RequestParam(name = "page",    defaultValue = "1")  int page,
        @RequestParam(name = "size",    defaultValue = "15") int size
    ) {
        log.debug("REST search corresps q='{}' userId={} page={} size={}", q, userId, page, size);

        Page<CorrespDTO> result = correspService.searchByKeyword(q, userId, page, size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content",     result.getContent());
        response.put("currentPage", page);
        response.put("pageSize",    size);
        response.put("totalItems",  result.getTotalElements());
        response.put("totalPages",  result.getTotalPages());
        response.put("hasNext",     result.hasNext());
        response.put("hasPrevious", result.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/corresps/list
     * Liste paginée avec les champs d'affichage optimisés (inclut sender/receiver du message).
     * Utilisé par Admin et BOC (accès complet : ACL + DRAFT + DISPATCH).
     */
    @GetMapping("/corresps/list")
    public ResponseEntity<Map<String, Object>> getCorrespList(
        @RequestParam(name = "userId")                          Long userId,
        @RequestParam(name = "page",         defaultValue = "1")  int page,
        @RequestParam(name = "size",         defaultValue = "15") int size,
        @RequestParam(name = "q",            required = false, defaultValue = "") String q,
        @RequestParam(name = "corresptype",  required = false)  String corresptype,
        @RequestParam(name = "reference",    required = false)  String reference,
        @RequestParam(name = "status",       required = false)  String status,
        @RequestParam(name = "dispatchType", required = false)  String dispatchType,
        @RequestParam(name = "priority",     required = false)  Boolean priority,
        @RequestParam(name = "startDate",    required = false)  String startDate,
        @RequestParam(name = "endDate",      required = false)  String endDate,
        @RequestParam(name = "nomSociete",   required = false)  String nomSociete
    ) {
        Page<CorrespListItemDTO> result = correspService.getCorrespListPaged(
            userId, q, corresptype, reference, status, dispatchType, priority, startDate, endDate, nomSociete, page, size
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content",     result.getContent());
        response.put("currentPage", page);
        response.put("pageSize",    size);
        response.put("totalItems",  result.getTotalElements());
        response.put("totalPages",  result.getTotalPages());
        response.put("hasNext",     result.hasNext());
        response.put("hasPrevious", result.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/corresps/list-by-acl
     * Liste paginée pour un utilisateur simple (ACL + DRAFT uniquement).
     */
    @GetMapping("/corresps/list-by-acl")
    public ResponseEntity<Map<String, Object>> getCorrespListByAcl(
        @RequestParam(name = "userId")                          Long userId,
        @RequestParam(name = "page",         defaultValue = "1")  int page,
        @RequestParam(name = "size",         defaultValue = "15") int size,
        @RequestParam(name = "q",            required = false, defaultValue = "") String q,
        @RequestParam(name = "corresptype",  required = false)  String corresptype,
        @RequestParam(name = "reference",    required = false)  String reference,
        @RequestParam(name = "status",       required = false)  String status,
        @RequestParam(name = "dispatchType", required = false)  String dispatchType,
        @RequestParam(name = "priority",     required = false)  Boolean priority,
        @RequestParam(name = "startDate",    required = false)  String startDate,
        @RequestParam(name = "endDate",      required = false)  String endDate,
        @RequestParam(name = "nomSociete",   required = false)  String nomSociete
    ) {
        log.debug("REST /corresps/list-by-acl userId={} q='{}' page={} size={}", userId, q, page, size);

        Page<CorrespListItemDTO> result =
            correspService.getCorrespListPagedByUserAcl(userId, q, corresptype, reference, status, dispatchType, priority, startDate, endDate, nomSociete, page, size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content",     result.getContent());
        response.put("currentPage", page);
        response.put("pageSize",    size);
        response.put("totalItems",  result.getTotalElements());
        response.put("totalPages",  result.getTotalPages());
        response.put("hasNext",     result.hasNext());
        response.put("hasPrevious", result.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/corresps/export
     * Exporte la liste filtrée des correspondances en fichier Excel (.xlsx).
     * Accepte les mêmes paramètres que /api/corresps/list et /api/corresps/list-by-acl.
     */
    @GetMapping("/corresps/export")
    public ResponseEntity<byte[]> exportCorresps(
        @RequestParam(name = "userId")                           Long userId,
        @RequestParam(name = "isAdmin",      defaultValue = "false") boolean isAdmin,
        @RequestParam(name = "q",            required = false, defaultValue = "") String q,
        @RequestParam(name = "corresptype",  required = false)   String corresptype,
        @RequestParam(name = "reference",    required = false)   String reference,
        @RequestParam(name = "status",       required = false)   String status,
        @RequestParam(name = "dispatchType", required = false)   String dispatchType,
        @RequestParam(name = "priority",     required = false)   Boolean priority,
        @RequestParam(name = "startDate",    required = false)   String startDate,
        @RequestParam(name = "endDate",      required = false)   String endDate,
        @RequestParam(name = "nomSociete",   required = false)   String nomSociete
    ) {
        log.debug("REST export Excel userId={} isAdmin={}", userId, isAdmin);
        try {
            byte[] excelBytes = correspService.exportCorrespsToExcel(
                userId, isAdmin, q, corresptype, reference, status,
                dispatchType, priority, startDate, endDate, nomSociete
            );

            String filename = "correspondances_"
                + java.time.LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + ".xlsx";

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelBytes);

        } catch (IOException e) {
            log.error("Erreur génération Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
