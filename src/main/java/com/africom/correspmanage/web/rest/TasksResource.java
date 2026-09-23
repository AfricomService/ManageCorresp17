package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.client.CorrespGatewayRestClient;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.service.TasksService;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.africom.correspmanage.domain.Tasks}.
 */
@RestController
@RequestMapping("/api")
public class TasksResource {

    private final Logger log = LoggerFactory.getLogger(TasksResource.class);

    private static final String ENTITY_NAME = "correspManageTasks";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TasksService tasksService;

    private final TasksRepository tasksRepository;
    private final ObjectMapper objectMapper;
    private final CorrespGatewayRestClient correspGatewayRestClient;

    public TasksResource(TasksService tasksService, TasksRepository tasksRepository, ObjectMapper objectMapper, CorrespGatewayRestClient correspGatewayRestClient) {
        this.tasksService = tasksService;
        this.tasksRepository = tasksRepository;
        this.objectMapper = objectMapper;
        this.correspGatewayRestClient = correspGatewayRestClient;
    }

    /**
     * {@code POST  /tasks} : Create a new tasks.
     *
     * @param tasksDTO the tasksDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tasksDTO, or with status {@code 400 (Bad Request)} if the tasks has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tasks")
    public ResponseEntity<TasksDTO> createTasks(@Valid @RequestBody TasksDTO tasksDTO) throws URISyntaxException {
        log.debug("REST request to save Tasks : {}", tasksDTO);
        if (tasksDTO.getId() != null) {
            throw new BadRequestAlertException("A new tasks cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TasksDTO result = tasksService.save(tasksDTO);
        return ResponseEntity
            .created(new URI("/api/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tasks/:id} : Updates an existing tasks.
     *
     * @param id the id of the tasksDTO to save.
     * @param tasksDTO the tasksDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tasksDTO,
     * or with status {@code 400 (Bad Request)} if the tasksDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tasksDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TasksDTO> updateTasks(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TasksDTO tasksDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Tasks : {}, {}", id, tasksDTO);
        if (tasksDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tasksDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tasksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TasksDTO result = tasksService.update(tasksDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tasksDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tasks/:id} : Partial updates given fields of an existing tasks, field will ignore if it is null
     *
     * @param id the id of the tasksDTO to save.
     * @param tasksDTO the tasksDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tasksDTO,
     * or with status {@code 400 (Bad Request)} if the tasksDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tasksDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tasksDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tasks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TasksDTO> partialUpdateTasks(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TasksDTO tasksDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tasks partially : {}, {}", id, tasksDTO);
        if (tasksDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tasksDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tasksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TasksDTO> result = tasksService.partialUpdate(tasksDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tasksDTO.getId().toString())
        );
    }

    /**
     * {@code PATCH  /tasks/:id/prise-en-charge} : marque la tâche comme prise en charge (persiste la date actuelle).
     *
     * @param id l'id de la tâche.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tasksDTO,
     * or with status {@code 404 (Not Found)}.
     */
    @PatchMapping("/tasks/{id}/prise-en-charge")
    public ResponseEntity<TasksDTO> markTaskPriseEnCharge(@PathVariable Long id) {
        log.debug("REST request to mark Tasks prise en charge : {}", id);
        Optional<TasksDTO> result = tasksService.markPriseEnCharge(id);
        return ResponseUtil.wrapOrNotFound(result);
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<TasksDTO>> getAllTasks(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Tasks");
        Page<TasksDTO> page = tasksService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tasks/:id} : get the "id" tasks.
     *
     * @param id the id of the tasksDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tasksDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TasksDTO> getTasks(@PathVariable Long id) {
        log.debug("REST request to get Tasks : {}", id);
        Optional<TasksDTO> tasksDTO = tasksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tasksDTO);
    }

    @GetMapping("/tasks/tasksByEtat")
    public List<TasksDTO> getAllTasksByEtat(@RequestParam(name = "etat") String etat) {
        log.debug("REST request to get a page of Tasks");
        return tasksService.findAllByEtat(etat);
    }

    @GetMapping("/tasks/tasksByCorrespId")
    public List<TasksDTO> getAllTasksByCorrespId(@RequestParam(name = "correspId") Long correspId) {
        log.debug("REST request to get a page of Tasks");
        return tasksService.getAllTasksByCorrespId(correspId);
    }

//    @GetMapping("/tasks/tasksDeptCode")
//    public List<TasksDTO> getTasksByAcl(@RequestParam(name = "deptCode") String deptCode) {
//        log.debug("REST request to get a page of Tasks");
//        return tasksService.getAllTasksByAcl(deptCode);
//    }

    @DeleteMapping("/tasks/deleteTaskFromCorrespAndDB/{correspId}/{tasksId}")
    public void deleteTask(@PathVariable(name = "correspId") Long correspId, @PathVariable(name = "tasksId") Long tasksId) {
        log.debug("REST request to get a page of Tasks");
        tasksService.deleteTask(tasksId, correspId);
    }

    /**
     * {@code DELETE  /tasks/:id} : delete the "id" tasks.
     *
     * @param id the id of the tasksDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTasks(@PathVariable Long id) {
        log.debug("REST request to delete Tasks : {}", id);
        tasksService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // retourne tasks filtrées par une liste de codes passée en params (ex: ?deptCode=IT&deptCode=HR)
    @GetMapping("/tasks/tasksDeptCodes")
    public List<TasksDTO> getTasksByAclForDeptCodes(@RequestParam(name = "deptCode") List<String> deptCodes) {
        log.debug("REST request to get Tasks by deptCodes : {}", deptCodes);
        return tasksService.getAllTasksByAcl(deptCodes);
    }

    // retourne tasks pour les départements associés à un user (via OrgaCare)
    @GetMapping("/tasks/tasksByUserAcl")
    public ResponseEntity<List<TasksDTO>> getTasksByUserAcl(
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "status", required = false) String status
    ) {
        log.debug("REST request to get Tasks by userId (ACL) : {} with status: {}", userId, status);

        CorrespStatus cs = null;
        if (status != null && !status.isBlank()) {
            try {
                cs = CorrespStatus.valueOf(status.trim());
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid status parameter supplied: {}", status);
                // Retourner 400 Bad Request si status invalide
                return ResponseEntity.badRequest().build();
            }
        }

        List<TasksDTO> tasks = tasksService.getAllTasksByUserAcl(userId, cs);
        if (tasks == null || tasks.isEmpty()) {
            // On peut renvoyer 200 OK + liste vide (comportement courant pour liste vide)
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/tasksByUserAclPaged")
    public ResponseEntity<Map<String, Object>> getTasksByUserAclPaged(
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.debug("REST request to get Tasks paged by userId (ACL) : {}, status={}, page={}, size={}",
            userId, status, page, size);

        CorrespStatus cs = null;
        if (status != null && !status.isBlank()) {
            try {
                cs = CorrespStatus.valueOf(status.trim());
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid status parameter: {}", status);
                return ResponseEntity.badRequest().build();
            }
        }

        Map<String, Object> result = tasksService.getAllTasksByUserAclPaged(userId, cs, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/tasksByUserAclAllStatuses")
    public List<TasksDTO> getTasksByUserAclAllStatuses(@RequestParam(name = "userId") String userId) {
        log.debug("REST request to get Tasks by userId (ACL) all statuses : {}", userId);
        return tasksService.getAllTasksByUserAclAllStatuses(userId);
    }

    @GetMapping("/tasks/searchByUserAcl")
    public ResponseEntity<List<TasksDTO>> searchTasksByUserAcl(
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "reference", required = false) String reference,
        @RequestParam(name = "subject",   required = false) String subject
    ) {
        log.debug("REST request to search Tasks by userId (ACL) : {} - reference: {}", userId, reference);

        boolean referenceBlank = (reference == null || reference.isBlank());
        boolean subjectBlank   = (subject   == null || subject.isBlank());

        if (userId == null || (referenceBlank && subjectBlank)) {
            // Au moins un critère de recherche doit être fourni
            return ResponseEntity.badRequest().build();
        }

        try {
            List<TasksDTO> results = tasksService.searchTasksByUserAcl(userId, reference, subject);

            if (results == null) {
                results = Collections.emptyList();
            }
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid parameter for searchTasksByUserAcl: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            log.error("Erreur lors de la recherche des tâches pour user {} : {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /tasks/advancedSearchByUserAcl
     * Recherche avancée multi-critères sur les tâches accessibles par l'utilisateur (ACL).
     * Tous les paramètres sont optionnels sauf userId.
     * - subject       : sous-chaîne insensible à la casse sur corresp.subject
     * - responsible   : sous-chaîne insensible à la casse sur le nom du responsable (dept ou personne)
     * - reference     : sous-chaîne insensible à la casse sur corresp.reference
     * - creationDateFrom / creationDateTo : plage ISO-8601 sur tasks.creationDate
     * - dueDateFrom / dueDateTo           : plage ISO-8601 sur tasks.dueDate
     */
    @GetMapping("/tasks/advancedSearchByUserAcl")
    public ResponseEntity<List<TasksDTO>> advancedSearchTasksByUserAcl(
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "subject",           required = false) String subject,
        @RequestParam(name = "responsible",        required = false) String responsible,
        @RequestParam(name = "reference",          required = false) String reference,
        @RequestParam(name = "creationDateFrom",   required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime creationDateFrom,
        @RequestParam(name = "creationDateTo",     required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime creationDateTo,
        @RequestParam(name = "dueDateFrom",        required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dueDateFrom,
        @RequestParam(name = "dueDateTo",          required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dueDateTo
    ) {
        log.debug("REST advancedSearchTasksByUserAcl userId={} subject={} responsible={} reference={} "
                + "creationDateFrom={} creationDateTo={} dueDateFrom={} dueDateTo={}",
            userId, subject, responsible, reference, creationDateFrom, creationDateTo, dueDateFrom, dueDateTo);

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<TasksDTO> results = tasksService.searchAdvancedTasksByUserAcl(
                userId, subject, responsible, reference,
                creationDateFrom, creationDateTo, dueDateFrom, dueDateTo
            );
            return ResponseEntity.ok(results != null ? results : Collections.emptyList());
        } catch (Exception ex) {
            log.error("Erreur advancedSearchTasksByUserAcl pour user {} : {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/tasks/tasksByUserAcl/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportTasksByUserAclToExcel(
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "status", required = false) String status
    ) {
        log.debug("REST request to export Tasks by userId (ACL) : {} with status: {}", userId, status);

        CorrespStatus cs = null;
        if (status != null && !status.isBlank()) {
            try {
                cs = CorrespStatus.valueOf(status.trim());
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid status parameter supplied for export: {}", status);
                return ResponseEntity.badRequest().build();
            }
        }

        List<TasksDTO> tasks = tasksService.getAllTasksByUserAcl(userId, cs);
        if (tasks == null) tasks = Collections.emptyList();

        ObjectMapper mapper = this.objectMapper != null ? this.objectMapper.copy() : new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final List<String> desiredHeaders = Arrays.asList(
            "object", "creationDate", "startDate", "dueDate", "closeDate",
            "status", "createdBy", "updatedBy", "updatedDate", "taskType", "responsables"
        );

        final Map<String, String> headerLabels = new LinkedHashMap<>();
        headerLabels.put("object", "Objet");
        headerLabels.put("creationDate", "Date de création");
        headerLabels.put("startDate", "Date de début");
        headerLabels.put("dueDate", "Date limite");
        headerLabels.put("closeDate", "Date de clôture");
        headerLabels.put("status", "Statut");
        headerLabels.put("createdBy", "Créé par");
        headerLabels.put("updatedBy", "Mis à jour par");
        headerLabels.put("updatedDate", "Date MAJ");
        headerLabels.put("taskType", "Type de tâche");
        headerLabels.put("responsables", "Responsable");

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tasks");

            List<Map<String, Object>> maps = tasks.stream().map(t -> {
                Map<String, Object> original = mapper.convertValue(t, new TypeReference<Map<String, Object>>() {});
                Map<String, Object> trimmed = new LinkedHashMap<>();
                for (String h : desiredHeaders) {
                    if (!"responsables".equals(h)) {
                        trimmed.put(h, original.get(h));
                    }
                }

                Object responsablesObj = original.get("responsables");
                List<String> responsablesDisplay = new ArrayList<>();

                if (responsablesObj instanceof Collection) {
                    for (Object o : (Collection<?>) responsablesObj) {
                        if (o == null) continue;
                        if (o instanceof Map) {
                            Map<?,?> mm = (Map<?,?>) o;
                            Object dn = mm.get("deptName");
                            Object pn = mm.get("personneName");
                            if (dn != null && dn.toString().trim().length() > 0) {
                                responsablesDisplay.add(dn.toString().trim());
                            } else if (pn != null && pn.toString().trim().length() > 0) {
                                responsablesDisplay.add(pn.toString().trim());
                            }
                        } else {
                            String s = o.toString().trim();
                            if (!s.isEmpty()) responsablesDisplay.add(s);
                        }
                    }
                } else if (responsablesObj instanceof String) {
                    String s = ((String) responsablesObj).trim();
                    if (!s.isEmpty()) {
                        try {
                            List<Map<String, Object>> parsed = mapper.readValue(s, new TypeReference<List<Map<String, Object>>>() {});
                            for (Map<String, Object> mm : parsed) {
                                Object dn = mm.get("deptName");
                                Object pn = mm.get("personneName");
                                if (dn != null && dn.toString().trim().length() > 0) {
                                    responsablesDisplay.add(dn.toString().trim());
                                } else if (pn != null && pn.toString().trim().length() > 0) {
                                    responsablesDisplay.add(pn.toString().trim());
                                }
                            }
                        } catch (Exception ex) {
                            if (!s.isEmpty()) responsablesDisplay.add(s);
                        }
                    }
                } else if (responsablesObj != null) {
                    try {
                        String json = mapper.writeValueAsString(responsablesObj);
                        if (json != null && !json.isBlank()) responsablesDisplay.add(json);
                    } catch (Exception ex) {
                        responsablesDisplay.add(responsablesObj.toString());
                    }
                }

                List<String> uniq = responsablesDisplay.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

                trimmed.put("responsables", String.join(", ", uniq));
                return trimmed;
            }).collect(Collectors.toList());

            List<String> headers = new ArrayList<>(desiredHeaders);

            int rowIdx = 0;
            Row headerRow = sheet.createRow(rowIdx++);
            CellStyle headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            for (int c = 0; c < headers.size(); c++) {
                String key = headers.get(c);
                String label = headerLabels.getOrDefault(key, key);
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(label);
                cell.setCellStyle(headerStyle);
            }

            for (Map<String, Object> m : maps) {
                Row r = sheet.createRow(rowIdx++);
                for (int c = 0; c < headers.size(); c++) {
                    String key = headers.get(c);
                    Object v = m.get(key);
                    Cell cell = r.createCell(c);
                    if (v == null) {
                        cell.setCellValue("");
                        continue;
                    }
                    if (v instanceof Number) {
                        cell.setCellValue(((Number) v).doubleValue());
                    } else if (v instanceof Boolean) {
                        cell.setCellValue((Boolean) v);
                    } else if (v instanceof java.util.Date) {
                        cell.setCellValue((java.util.Date) v);
                    } else {
                        try {
                            if (v instanceof Map || v instanceof Collection) {
                                cell.setCellValue(mapper.writeValueAsString(v));
                            } else {
                                cell.setCellValue(String.valueOf(v));
                            }
                        } catch (Exception ex) {
                            cell.setCellValue(String.valueOf(v));
                        }
                    }
                }
            }

            for (int c = 0; c < headers.size(); c++) {
                sheet.autoSizeColumn(c);
            }

            workbook.write(out);
            byte[] bytes = out.toByteArray();

            // -------------------- construire fileName avec firstName/lastName via Feign --------------------
            String timestamp = java.time.ZonedDateTime.now().toString().replaceAll("[:]", "-");
            String userLabel = null;

            try {
                Map<String, Object> userMap = null;
                try {
                    userMap = correspGatewayRestClient.getUserById(userId);
                } catch (Exception ex) {
                    log.debug("Erreur appel gateway pour getUserById({}): {}", userId, ex.getMessage());
                    userMap = null;
                }

                if (userMap != null && !userMap.isEmpty()) {
                    String first = userMap.get("firstName") != null ? userMap.get("firstName").toString().trim() : "";
                    String last = userMap.get("lastName") != null ? userMap.get("lastName").toString().trim() : "";
                    String login = userMap.get("login") != null ? userMap.get("login").toString().trim() : "";

                    if (!first.isEmpty() || !last.isEmpty()) {
                        userLabel = (first + "_" + last).replaceAll("\\s+", "_");
                    } else if (!login.isEmpty()) {
                        userLabel = login;
                    }
                }
            } catch (Exception ex) {
                log.debug("Erreur récup user pour nom fichier: {}", ex.getMessage());
            }

            if (userLabel == null || userLabel.isBlank()) {
                userLabel = "user_" + userId;
            }

            userLabel = userLabel.replaceAll("[^A-Za-z0-9_\\-]", "_");
            if (userLabel.length() > 60) userLabel = userLabel.substring(0, 60);

            String fileName = String.format("tasks_export_%s_%s.xlsx", userLabel, timestamp);
            // -----------------------------------------------------------------------------------------------

            HttpHeaders headersHttp = new HttpHeaders();
            headersHttp.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headersHttp.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());
            headersHttp.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(headersHttp).body(bytes);
        } catch (Exception e) {
            log.error("Erreur lors de l'export Excel des tâches pour user {} : {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/tasks/{id}/can-modify
    @GetMapping("/tasks/{id}/can-modify")
    public ResponseEntity<Boolean> canCurrentUserModifyTasksEndpoint(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(false);
        }
        boolean can = tasksService.canCurrentUserModifyTask(id);
        return ResponseEntity.ok(can);
    }

    // Alternative : accepter un TasksDTO (si tu veux vérifier un objet complet)
    @PostMapping("/tasks/can-modify")
    public ResponseEntity<Boolean> canCurrentUserModifyTasksByDto(@RequestBody TasksDTO tasksDTO) {
        if (tasksDTO == null || tasksDTO.getId() == null) {
            return ResponseEntity.badRequest().body(false);
        }
        // si tu as une méthode service prenant Tasks ou TasksDTO
        boolean can = tasksService.canCurrentUserModifyTask(tasksDTO.getId());
        return ResponseEntity.ok(can);
    }


}
