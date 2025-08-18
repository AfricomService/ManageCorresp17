package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.BordereauxRepository;
import com.africom.correspmanage.service.BordereauxService;
import com.africom.correspmanage.service.dto.BordereauxDTO;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.africom.correspmanage.domain.Bordereaux}.
 */
@RestController
@RequestMapping("/api")
public class BordereauxResource {

    private final Logger log = LoggerFactory.getLogger(BordereauxResource.class);

    private static final String ENTITY_NAME = "correspManageBordereaux";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BordereauxService bordereauxService;

    private final BordereauxRepository bordereauxRepository;

    public BordereauxResource(BordereauxService bordereauxService, BordereauxRepository bordereauxRepository) {
        this.bordereauxService = bordereauxService;
        this.bordereauxRepository = bordereauxRepository;
    }

    /**
     * {@code POST  /bordereaux} : Create a new bordereaux.
     *
     * @param bordereauxDTO the bordereauxDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bordereauxDTO, or with status {@code 400 (Bad Request)} if the bordereaux has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bordereaux")
    public ResponseEntity<BordereauxDTO> createBordereaux(@Valid @RequestBody BordereauxDTO bordereauxDTO) throws URISyntaxException {
        log.debug("REST request to save Bordereaux : {}", bordereauxDTO);
        if (bordereauxDTO.getId() != null) {
            throw new BadRequestAlertException("A new bordereaux cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BordereauxDTO result = bordereauxService.save(bordereauxDTO);
        return ResponseEntity
            .created(new URI("/api/bordereaux/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bordereaux/:id} : Updates an existing bordereaux.
     *
     * @param id the id of the bordereauxDTO to save.
     * @param bordereauxDTO the bordereauxDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bordereauxDTO,
     * or with status {@code 400 (Bad Request)} if the bordereauxDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bordereauxDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bordereaux/{id}")
    public ResponseEntity<BordereauxDTO> updateBordereaux(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BordereauxDTO bordereauxDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Bordereaux : {}, {}", id, bordereauxDTO);
        if (bordereauxDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bordereauxDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bordereauxRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BordereauxDTO result = bordereauxService.update(bordereauxDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bordereauxDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bordereaux/:id} : Partial updates given fields of an existing bordereaux, field will ignore if it is null
     *
     * @param id the id of the bordereauxDTO to save.
     * @param bordereauxDTO the bordereauxDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bordereauxDTO,
     * or with status {@code 400 (Bad Request)} if the bordereauxDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bordereauxDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bordereauxDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bordereaux/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BordereauxDTO> partialUpdateBordereaux(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BordereauxDTO bordereauxDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bordereaux partially : {}, {}", id, bordereauxDTO);
        if (bordereauxDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bordereauxDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bordereauxRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BordereauxDTO> result = bordereauxService.partialUpdate(bordereauxDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bordereauxDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bordereaux} : get all the bordereaux.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bordereaux in body.
     */
    @GetMapping("/bordereaux")
    public ResponseEntity<List<BordereauxDTO>> getAllBordereaux(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Bordereaux");
        Page<BordereauxDTO> page = bordereauxService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bordereaux/:id} : get the "id" bordereaux.
     *
     * @param id the id of the bordereauxDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bordereauxDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bordereaux/{id}")
    public ResponseEntity<BordereauxDTO> getBordereaux(@PathVariable Long id) {
        log.debug("REST request to get Bordereaux : {}", id);
        Optional<BordereauxDTO> bordereauxDTO = bordereauxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bordereauxDTO);
    }

    /**
     * {@code DELETE  /bordereaux/:id} : delete the "id" bordereaux.
     *
     * @param id the id of the bordereauxDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bordereaux/{id}")
    public ResponseEntity<Void> deleteBordereaux(@PathVariable Long id) {
        log.debug("REST request to delete Bordereaux : {}", id);
        bordereauxService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
