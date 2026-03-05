package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.client.ConfigManageRestClient;
import com.africom.correspmanage.client.TypeDocumentFeignDTO;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.MessageRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.CorrespService;
import com.africom.correspmanage.service.dto.*;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
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

    public CorrespResource(
        ConfigManageRestClient configManageRestClient,
        CorrespService correspService,
        CorrespRepository correspRepository,
        MessageRepository messageRepository
    ) {
        this.configManageRestClient = configManageRestClient;
        this.correspService = correspService;
        this.correspRepository = correspRepository;
        this.messageRepository = messageRepository;
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

    @GetMapping("/corresps-filter")
    public Page<CorrespDTO> getRapportRequestSpec(
        @RequestParam(name = "pageIndex") Integer pageIndex,
        @RequestParam(name = "pageSize") Integer pageSize,
        @RequestParam(name = "reference", required = false) String reference,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "dispatchType", required = false) String dispatchType,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate
    ) throws JsonProcessingException, JSONException {
        return correspService.findAllCorrespByFilter(reference, status, dispatchType, startDate, endDate, pageIndex, pageSize);
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
        // nouveaux champs reçus depuis n8n (optionnels)
        @RequestPart(value = "status", required = false) String status,
        @RequestPart(value = "corresptype", required = false) String corresptype,
        @RequestPart(value = "dispatchType", required = false) String dispatchType
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<PjDTO> pjList = Collections.emptyList();
            if (pjs != null && !pjs.trim().isEmpty()) {
                pjList = objectMapper.readValue(pjs, new TypeReference<List<PjDTO>>() {});
            }

            // passer les strings d'enum au service (le service fera la conversion sûre)
            correspService.saveCorrespWithPjN8N(
                pjList,
                subject,
                from,
                bodyHtml,
                receiverRaw,
                copyToRaw,
                status,
                corresptype,
                dispatchType
            );

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

        // Génération de la référence via ton service
        String generatedReference = configManageRestClient.getNextNumber(corresp.getCorresptype().name());
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
        return messageRepository.findByCorrespId(correspId)
            .map(msg -> ResponseEntity.ok(correspService.loadBodyFromFile(msg.getPath())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found"));
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

    @GetMapping("/corresps/{id}/permissions")
    public ResponseEntity<CorrespPermissionsDTO> getPermissions(@PathVariable Long id) {
        CorrespPermissionsDTO dto = correspService.getAndEnsurePermissionsForCurrentUser(id);
        return ResponseEntity.ok(dto);
    }


}
