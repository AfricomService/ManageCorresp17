package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.ResponsableTacheRepository;
import com.africom.correspmanage.service.ResponsableTacheService;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.africom.correspmanage.domain.ResponsableTache}.
 */
@RestController
@RequestMapping("/api")
public class ResponsableTacheResource {

    private final Logger log = LoggerFactory.getLogger(ResponsableTacheResource.class);

    private static final String ENTITY_NAME = "correspManageResponsableTache";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResponsableTacheService responsableTacheService;

    private final ResponsableTacheRepository responsableTacheRepository;

    public ResponsableTacheResource(
        ResponsableTacheService responsableTacheService,
        ResponsableTacheRepository responsableTacheRepository
    ) {
        this.responsableTacheService = responsableTacheService;
        this.responsableTacheRepository = responsableTacheRepository;
    }

    /**
     * {@code POST  /responsable-taches} : Create a new responsableTache.
     *
     * @param responsableTacheDTO the responsableTacheDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new responsableTacheDTO, or with status {@code 400 (Bad Request)} if the responsableTache has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/responsable-taches")
    public ResponseEntity<ResponsableTacheDTO> createResponsableTache(@RequestBody ResponsableTacheDTO responsableTacheDTO)
        throws URISyntaxException {
        log.debug("REST request to save ResponsableTache : {}", responsableTacheDTO);
        if (responsableTacheDTO.getId() != null) {
            throw new BadRequestAlertException("A new responsableTache cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResponsableTacheDTO result = responsableTacheService.save(responsableTacheDTO);
        return ResponseEntity
            .created(new URI("/api/responsable-taches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /responsable-taches/:id} : Updates an existing responsableTache.
     *
     * @param id the id of the responsableTacheDTO to save.
     * @param responsableTacheDTO the responsableTacheDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated responsableTacheDTO,
     * or with status {@code 400 (Bad Request)} if the responsableTacheDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the responsableTacheDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/responsable-taches/{id}")
    public ResponseEntity<ResponsableTacheDTO> updateResponsableTache(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResponsableTacheDTO responsableTacheDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ResponsableTache : {}, {}", id, responsableTacheDTO);
        if (responsableTacheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, responsableTacheDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!responsableTacheRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ResponsableTacheDTO result = responsableTacheService.save(responsableTacheDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, responsableTacheDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /responsable-taches/:id} : Partial updates given fields of an existing responsableTache, field will ignore if it is null
     *
     * @param id the id of the responsableTacheDTO to save.
     * @param responsableTacheDTO the responsableTacheDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated responsableTacheDTO,
     * or with status {@code 400 (Bad Request)} if the responsableTacheDTO is not valid,
     * or with status {@code 404 (Not Found)} if the responsableTacheDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the responsableTacheDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/responsable-taches/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResponsableTacheDTO> partialUpdateResponsableTache(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResponsableTacheDTO responsableTacheDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ResponsableTache partially : {}, {}", id, responsableTacheDTO);
        if (responsableTacheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, responsableTacheDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!responsableTacheRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ResponsableTacheDTO> result = responsableTacheService.partialUpdate(responsableTacheDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, responsableTacheDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /responsable-taches} : get all the responsableTaches.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of responsableTaches in body.
     */
    @GetMapping("/responsable-taches")
    public List<ResponsableTacheDTO> getAllResponsableTaches() {
        log.debug("REST request to get all ResponsableTaches");
        return responsableTacheService.findAll();
    }

    /**
     * {@code GET  /responsable-taches/:id} : get the "id" responsableTache.
     *
     * @param id the id of the responsableTacheDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the responsableTacheDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/responsable-taches/{id}")
    public ResponseEntity<ResponsableTacheDTO> getResponsableTache(@PathVariable Long id) {
        log.debug("REST request to get ResponsableTache : {}", id);
        Optional<ResponsableTacheDTO> responsableTacheDTO = responsableTacheService.findOne(id);
        return ResponseUtil.wrapOrNotFound(responsableTacheDTO);
    }

    /**
     * {@code DELETE  /responsable-taches/:id} : delete the "id" responsableTache.
     *
     * @param id the id of the responsableTacheDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/responsable-taches/{id}")
    public ResponseEntity<Void> deleteResponsableTache(@PathVariable Long id) {
        log.debug("REST request to delete ResponsableTache : {}", id);
        responsableTacheService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
