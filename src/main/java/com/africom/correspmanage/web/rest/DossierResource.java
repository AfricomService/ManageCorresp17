package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.DossierRepository;
import com.africom.correspmanage.service.DossierService;
import com.africom.correspmanage.service.dto.DossierDTO;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.africom.correspmanage.domain.Dossier}.
 */
@RestController
@RequestMapping("/api")
public class DossierResource {

    private final Logger log = LoggerFactory.getLogger(DossierResource.class);

    private static final String ENTITY_NAME = "correspManageDossier";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DossierService dossierService;

    private final DossierRepository dossierRepository;

    public DossierResource(DossierService dossierService, DossierRepository dossierRepository) {
        this.dossierService = dossierService;
        this.dossierRepository = dossierRepository;
    }

    /**
     * {@code POST  /dossiers} : Create a new dossier.
     *
     * @param dossierDTO the dossierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dossierDTO, or with status {@code 400 (Bad Request)} if the dossier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dossiers")
    public ResponseEntity<DossierDTO> createDossier(@RequestBody DossierDTO dossierDTO) throws URISyntaxException {
        log.debug("REST request to save Dossier : {}", dossierDTO);
        if (dossierDTO.getId() != null) {
            throw new BadRequestAlertException("A new dossier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DossierDTO result = dossierService.save(dossierDTO);
        return ResponseEntity
            .created(new URI("/api/dossiers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dossiers/:id} : Updates an existing dossier.
     *
     * @param id the id of the dossierDTO to save.
     * @param dossierDTO the dossierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dossierDTO,
     * or with status {@code 400 (Bad Request)} if the dossierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dossierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dossiers/{id}")
    public ResponseEntity<DossierDTO> updateDossier(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DossierDTO dossierDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Dossier : {}, {}", id, dossierDTO);
        if (dossierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dossierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dossierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DossierDTO result = dossierService.update(dossierDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dossierDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dossiers/:id} : Partial updates given fields of an existing dossier, field will ignore if it is null
     *
     * @param id the id of the dossierDTO to save.
     * @param dossierDTO the dossierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dossierDTO,
     * or with status {@code 400 (Bad Request)} if the dossierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dossierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dossierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dossiers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DossierDTO> partialUpdateDossier(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DossierDTO dossierDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Dossier partially : {}, {}", id, dossierDTO);
        if (dossierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dossierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dossierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DossierDTO> result = dossierService.partialUpdate(dossierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dossierDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /dossiers} : get all the dossiers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dossiers in body.
     */
    @GetMapping("/dossiers")
    public ResponseEntity<List<DossierDTO>> getAllDossiers(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Dossiers");
        Page<DossierDTO> page = dossierService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dossiers/:id} : get the "id" dossier.
     *
     * @param id the id of the dossierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dossierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dossiers/{id}")
    public ResponseEntity<DossierDTO> getDossier(@PathVariable Long id) {
        log.debug("REST request to get Dossier : {}", id);
        Optional<DossierDTO> dossierDTO = dossierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dossierDTO);
    }

    /**
     * {@code DELETE  /dossiers/:id} : delete the "id" dossier.
     *
     * @param id the id of the dossierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dossiers/{id}")
    public ResponseEntity<Void> deleteDossier(@PathVariable Long id) {
        log.debug("REST request to delete Dossier : {}", id);
        dossierService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
