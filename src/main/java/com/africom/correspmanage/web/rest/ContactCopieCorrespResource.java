package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.ContactCopieCorrespRepository;
import com.africom.correspmanage.service.ContactCopieCorrespService;
import com.africom.correspmanage.service.dto.ContactCopieCorrespDTO;
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
 * REST controller for managing {@link com.africom.correspmanage.domain.ContactCopieCorresp}.
 */
@RestController
@RequestMapping("/api")
public class ContactCopieCorrespResource {

    private final Logger log = LoggerFactory.getLogger(ContactCopieCorrespResource.class);

    private static final String ENTITY_NAME = "correspManageContactCopieCorresp";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContactCopieCorrespService contactCopieCorrespService;

    private final ContactCopieCorrespRepository contactCopieCorrespRepository;

    public ContactCopieCorrespResource(
        ContactCopieCorrespService contactCopieCorrespService,
        ContactCopieCorrespRepository contactCopieCorrespRepository
    ) {
        this.contactCopieCorrespService = contactCopieCorrespService;
        this.contactCopieCorrespRepository = contactCopieCorrespRepository;
    }

    /**
     * {@code POST  /contact-copie-corresps} : Create a new contactCopieCorresp.
     *
     * @param contactCopieCorrespDTO the contactCopieCorrespDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contactCopieCorrespDTO, or with status {@code 400 (Bad Request)} if the contactCopieCorresp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/contact-copie-corresps")
    public ResponseEntity<ContactCopieCorrespDTO> createContactCopieCorresp(@RequestBody ContactCopieCorrespDTO contactCopieCorrespDTO)
        throws URISyntaxException {
        log.debug("REST request to save ContactCopieCorresp : {}", contactCopieCorrespDTO);
        if (contactCopieCorrespDTO.getId() != null) {
            throw new BadRequestAlertException("A new contactCopieCorresp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ContactCopieCorrespDTO result = contactCopieCorrespService.save(contactCopieCorrespDTO);
        return ResponseEntity
            .created(new URI("/api/contact-copie-corresps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /contact-copie-corresps/:id} : Updates an existing contactCopieCorresp.
     *
     * @param id the id of the contactCopieCorrespDTO to save.
     * @param contactCopieCorrespDTO the contactCopieCorrespDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactCopieCorrespDTO,
     * or with status {@code 400 (Bad Request)} if the contactCopieCorrespDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contactCopieCorrespDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/contact-copie-corresps/{id}")
    public ResponseEntity<ContactCopieCorrespDTO> updateContactCopieCorresp(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ContactCopieCorrespDTO contactCopieCorrespDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ContactCopieCorresp : {}, {}", id, contactCopieCorrespDTO);
        if (contactCopieCorrespDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contactCopieCorrespDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactCopieCorrespRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ContactCopieCorrespDTO result = contactCopieCorrespService.save(contactCopieCorrespDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contactCopieCorrespDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /contact-copie-corresps/:id} : Partial updates given fields of an existing contactCopieCorresp, field will ignore if it is null
     *
     * @param id the id of the contactCopieCorrespDTO to save.
     * @param contactCopieCorrespDTO the contactCopieCorrespDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactCopieCorrespDTO,
     * or with status {@code 400 (Bad Request)} if the contactCopieCorrespDTO is not valid,
     * or with status {@code 404 (Not Found)} if the contactCopieCorrespDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the contactCopieCorrespDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/contact-copie-corresps/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ContactCopieCorrespDTO> partialUpdateContactCopieCorresp(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ContactCopieCorrespDTO contactCopieCorrespDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ContactCopieCorresp partially : {}, {}", id, contactCopieCorrespDTO);
        if (contactCopieCorrespDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contactCopieCorrespDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactCopieCorrespRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ContactCopieCorrespDTO> result = contactCopieCorrespService.partialUpdate(contactCopieCorrespDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contactCopieCorrespDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /contact-copie-corresps} : get all the contactCopieCorresps.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contactCopieCorresps in body.
     */
    @GetMapping("/contact-copie-corresps")
    public List<ContactCopieCorrespDTO> getAllContactCopieCorresps() {
        log.debug("REST request to get all ContactCopieCorresps");
        return contactCopieCorrespService.findAll();
    }

    /**
     * {@code GET  /contact-copie-corresps/:id} : get the "id" contactCopieCorresp.
     *
     * @param id the id of the contactCopieCorrespDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contactCopieCorrespDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/contact-copie-corresps/{id}")
    public ResponseEntity<ContactCopieCorrespDTO> getContactCopieCorresp(@PathVariable Long id) {
        log.debug("REST request to get ContactCopieCorresp : {}", id);
        Optional<ContactCopieCorrespDTO> contactCopieCorrespDTO = contactCopieCorrespService.findOne(id);
        return ResponseUtil.wrapOrNotFound(contactCopieCorrespDTO);
    }

    /**
     * {@code DELETE  /contact-copie-corresps/:id} : delete the "id" contactCopieCorresp.
     *
     * @param id the id of the contactCopieCorrespDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/contact-copie-corresps/{id}")
    public ResponseEntity<Void> deleteContactCopieCorresp(@PathVariable Long id) {
        log.debug("REST request to delete ContactCopieCorresp : {}", id);
        contactCopieCorrespService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
