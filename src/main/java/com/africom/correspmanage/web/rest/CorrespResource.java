package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.CorrespService;
import com.africom.correspmanage.service.dto.CorrespDTO;
import com.africom.correspmanage.service.dto.FileUploadDTO;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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

    private final CorrespService correspService;

    private final CorrespRepository correspRepository;

    public CorrespResource(CorrespService correspService, CorrespRepository correspRepository) {
        this.correspService = correspService;
        this.correspRepository = correspRepository;
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
     * @param id the id of the correspDTO to save.
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
     * @param id the id of the correspDTO to save.
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
     * @param pageable the pagination information.
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
        @RequestPart("pjs") List<Pj> pjs
    ) {
        return correspService.saveCorrespWithTasksAndPj(correspDTO, tasksDTOS, pjs);
    }
    //    @PostMapping(value = "/saveFilesOfPjs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //    public void saveFilesOfPjs(
    //        @RequestPart(value = "files", required = false) List<FileUploadDTO> files
    //    ) {
    //        correspService.saveFilesOfPjs(files);
    //    }

}
