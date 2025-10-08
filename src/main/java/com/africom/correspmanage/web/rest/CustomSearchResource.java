package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.CustomSearchRepository;
import com.africom.correspmanage.service.CustomSearchService;
import com.africom.correspmanage.service.dto.CustomSearchDTO;
import com.africom.correspmanage.service.dto.CustomSearchViewDTO;
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
 * REST controller for managing {@link com.africom.correspmanage.domain.CustomSearch}.
 */
@RestController
@RequestMapping("/api")
public class CustomSearchResource {

    private final Logger log = LoggerFactory.getLogger(CustomSearchResource.class);

    private static final String ENTITY_NAME = "correspManageCustomSearch";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomSearchService customSearchService;

    private final CustomSearchRepository customSearchRepository;

    public CustomSearchResource(CustomSearchService customSearchService, CustomSearchRepository customSearchRepository) {
        this.customSearchService = customSearchService;
        this.customSearchRepository = customSearchRepository;
    }

    /**
     * {@code POST  /custom-searches} : Create a new customSearch.
     *
     * @param customSearchDTO the customSearchDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customSearchDTO, or with status {@code 400 (Bad Request)} if the customSearch has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/custom-searches")
    public ResponseEntity<CustomSearchDTO> createCustomSearch(@RequestBody CustomSearchDTO customSearchDTO) throws URISyntaxException {
        log.debug("REST request to save CustomSearch : {}", customSearchDTO);
        if (customSearchDTO.getId() != null) {
            throw new BadRequestAlertException("A new customSearch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CustomSearchDTO result = customSearchService.save(customSearchDTO);
        return ResponseEntity
            .created(new URI("/api/custom-searches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /custom-searches/:id} : Updates an existing customSearch.
     *
     * @param id the id of the customSearchDTO to save.
     * @param customSearchDTO the customSearchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customSearchDTO,
     * or with status {@code 400 (Bad Request)} if the customSearchDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customSearchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/custom-searches/{id}")
    public ResponseEntity<CustomSearchDTO> updateCustomSearch(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CustomSearchDTO customSearchDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CustomSearch : {}, {}", id, customSearchDTO);
        if (customSearchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customSearchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customSearchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CustomSearchDTO result = customSearchService.update(customSearchDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customSearchDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /custom-searches/:id} : Partial updates given fields of an existing customSearch, field will ignore if it is null
     *
     * @param id the id of the customSearchDTO to save.
     * @param customSearchDTO the customSearchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customSearchDTO,
     * or with status {@code 400 (Bad Request)} if the customSearchDTO is not valid,
     * or with status {@code 404 (Not Found)} if the customSearchDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the customSearchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/custom-searches/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CustomSearchDTO> partialUpdateCustomSearch(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CustomSearchDTO customSearchDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CustomSearch partially : {}, {}", id, customSearchDTO);
        if (customSearchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, customSearchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!customSearchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CustomSearchDTO> result = customSearchService.partialUpdate(customSearchDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, customSearchDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /custom-searches} : get all the customSearches.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customSearches in body.
     */
    @GetMapping("/custom-searches")
    public ResponseEntity<List<CustomSearchDTO>> getAllCustomSearches(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of CustomSearches");
        Page<CustomSearchDTO> page = customSearchService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /custom-searches/:id} : get the "id" customSearch.
     *
     * @param id the id of the customSearchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customSearchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/custom-searches/{id}")
    public ResponseEntity<CustomSearchDTO> getCustomSearch(@PathVariable Long id) {
        log.debug("REST request to get CustomSearch : {}", id);
        Optional<CustomSearchDTO> customSearchDTO = customSearchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customSearchDTO);
    }

    /**
     * {@code DELETE  /custom-searches/:id} : delete the "id" customSearch.
     *
     * @param id the id of the customSearchDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/custom-searches/{id}")
    public ResponseEntity<Void> deleteCustomSearch(@PathVariable Long id) {
        log.debug("REST request to delete CustomSearch : {}", id);
        customSearchService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/custom-searches/saveRequest")
    public CustomSearchDTO saveReq(
        @RequestParam(name = "request") String request,
        @RequestParam(name = "entitySearching") String entitySearching,
        @RequestParam(name = "name") String name
    ) {
        log.debug("REST request to saveReq CustomSearch ");
        return customSearchService.saveCustomSearch(request, entitySearching, name);
    }

    @GetMapping("/custom-searches/findByUserLogin")
    public List<CustomSearchViewDTO> findByUserLogin() {
        log.debug("REST request to saveReq CustomSearch ");
        return customSearchService.findAllByUserLogin();
    }
}
