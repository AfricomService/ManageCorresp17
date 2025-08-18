package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.HistActionRepository;
import com.africom.correspmanage.service.HistActionService;
import com.africom.correspmanage.service.dto.HistActionDTO;
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
 * REST controller for managing {@link com.africom.correspmanage.domain.HistAction}.
 */
@RestController
@RequestMapping("/api")
public class HistActionResource {

    private final Logger log = LoggerFactory.getLogger(HistActionResource.class);

    private static final String ENTITY_NAME = "correspManageHistAction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HistActionService histActionService;

    private final HistActionRepository histActionRepository;

    public HistActionResource(HistActionService histActionService, HistActionRepository histActionRepository) {
        this.histActionService = histActionService;
        this.histActionRepository = histActionRepository;
    }

    /**
     * {@code POST  /hist-actions} : Create a new histAction.
     *
     * @param histActionDTO the histActionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new histActionDTO, or with status {@code 400 (Bad Request)} if the histAction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/hist-actions")
    public ResponseEntity<HistActionDTO> createHistAction(@RequestBody HistActionDTO histActionDTO) throws URISyntaxException {
        log.debug("REST request to save HistAction : {}", histActionDTO);
        if (histActionDTO.getId() != null) {
            throw new BadRequestAlertException("A new histAction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HistActionDTO result = histActionService.save(histActionDTO);
        return ResponseEntity
            .created(new URI("/api/hist-actions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /hist-actions/:id} : Updates an existing histAction.
     *
     * @param id the id of the histActionDTO to save.
     * @param histActionDTO the histActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated histActionDTO,
     * or with status {@code 400 (Bad Request)} if the histActionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the histActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/hist-actions/{id}")
    public ResponseEntity<HistActionDTO> updateHistAction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HistActionDTO histActionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update HistAction : {}, {}", id, histActionDTO);
        if (histActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, histActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!histActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HistActionDTO result = histActionService.update(histActionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, histActionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /hist-actions/:id} : Partial updates given fields of an existing histAction, field will ignore if it is null
     *
     * @param id the id of the histActionDTO to save.
     * @param histActionDTO the histActionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated histActionDTO,
     * or with status {@code 400 (Bad Request)} if the histActionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the histActionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the histActionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/hist-actions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HistActionDTO> partialUpdateHistAction(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HistActionDTO histActionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update HistAction partially : {}, {}", id, histActionDTO);
        if (histActionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, histActionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!histActionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HistActionDTO> result = histActionService.partialUpdate(histActionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, histActionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /hist-actions} : get all the histActions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of histActions in body.
     */
    @GetMapping("/hist-actions")
    public ResponseEntity<List<HistActionDTO>> getAllHistActions(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of HistActions");
        Page<HistActionDTO> page = histActionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /hist-actions/:id} : get the "id" histAction.
     *
     * @param id the id of the histActionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the histActionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/hist-actions/{id}")
    public ResponseEntity<HistActionDTO> getHistAction(@PathVariable Long id) {
        log.debug("REST request to get HistAction : {}", id);
        Optional<HistActionDTO> histActionDTO = histActionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(histActionDTO);
    }

    /**
     * {@code DELETE  /hist-actions/:id} : delete the "id" histAction.
     *
     * @param id the id of the histActionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/hist-actions/{id}")
    public ResponseEntity<Void> deleteHistAction(@PathVariable Long id) {
        log.debug("REST request to delete HistAction : {}", id);
        histActionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
