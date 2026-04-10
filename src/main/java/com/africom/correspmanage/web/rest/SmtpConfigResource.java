package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.SmtpConfigRepository;
import com.africom.correspmanage.service.SmtpConfigService;
import com.africom.correspmanage.service.dto.SmtpConfigDTO;
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
 * REST controller for managing {@link com.africom.correspmanage.domain.SmtpConfig}.
 */
@RestController
@RequestMapping("/api")
public class SmtpConfigResource {

    private final Logger log = LoggerFactory.getLogger(SmtpConfigResource.class);

    private static final String ENTITY_NAME = "correspManageSmtpConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SmtpConfigService smtpConfigService;

    private final SmtpConfigRepository smtpConfigRepository;

    public SmtpConfigResource(SmtpConfigService smtpConfigService, SmtpConfigRepository smtpConfigRepository) {
        this.smtpConfigService = smtpConfigService;
        this.smtpConfigRepository = smtpConfigRepository;
    }

    /**
     * {@code POST  /smtp-configs} : Create a new smtpConfig.
     *
     * @param smtpConfigDTO the smtpConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new smtpConfigDTO, or with status {@code 400 (Bad Request)} if the smtpConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/smtp-configs")
    public ResponseEntity<SmtpConfigDTO> createSmtpConfig(@RequestBody SmtpConfigDTO smtpConfigDTO) throws URISyntaxException {
        log.debug("REST request to save SmtpConfig : {}", smtpConfigDTO);
        if (smtpConfigDTO.getId() != null) {
            throw new BadRequestAlertException("A new smtpConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SmtpConfigDTO result = smtpConfigService.save(smtpConfigDTO);
        return ResponseEntity
            .created(new URI("/api/smtp-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /smtp-configs/:id} : Updates an existing smtpConfig.
     *
     * @param id the id of the smtpConfigDTO to save.
     * @param smtpConfigDTO the smtpConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smtpConfigDTO,
     * or with status {@code 400 (Bad Request)} if the smtpConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the smtpConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/smtp-configs/{id}")
    public ResponseEntity<SmtpConfigDTO> updateSmtpConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SmtpConfigDTO smtpConfigDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SmtpConfig : {}, {}", id, smtpConfigDTO);
        if (smtpConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smtpConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!smtpConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SmtpConfigDTO result = smtpConfigService.save(smtpConfigDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, smtpConfigDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /smtp-configs/:id} : Partial updates given fields of an existing smtpConfig, field will ignore if it is null
     *
     * @param id the id of the smtpConfigDTO to save.
     * @param smtpConfigDTO the smtpConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smtpConfigDTO,
     * or with status {@code 400 (Bad Request)} if the smtpConfigDTO is not valid,
     * or with status {@code 404 (Not Found)} if the smtpConfigDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the smtpConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/smtp-configs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SmtpConfigDTO> partialUpdateSmtpConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SmtpConfigDTO smtpConfigDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SmtpConfig partially : {}, {}", id, smtpConfigDTO);
        if (smtpConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smtpConfigDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!smtpConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SmtpConfigDTO> result = smtpConfigService.partialUpdate(smtpConfigDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, smtpConfigDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /smtp-configs} : get all the smtpConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of smtpConfigs in body.
     */
    @GetMapping("/smtp-configs")
    public List<SmtpConfigDTO> getAllSmtpConfigs() {
        log.debug("REST request to get all SmtpConfigs");
        return smtpConfigService.findAll();
    }

    /**
     * {@code GET  /smtp-configs/:id} : get the "id" smtpConfig.
     *
     * @param id the id of the smtpConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the smtpConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/smtp-configs/{id}")
    public ResponseEntity<SmtpConfigDTO> getSmtpConfig(@PathVariable Long id) {
        log.debug("REST request to get SmtpConfig : {}", id);
        Optional<SmtpConfigDTO> smtpConfigDTO = smtpConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(smtpConfigDTO);
    }

    /**
     * {@code DELETE  /smtp-configs/:id} : delete the "id" smtpConfig.
     *
     * @param id the id of the smtpConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/smtp-configs/{id}")
    public ResponseEntity<Void> deleteSmtpConfig(@PathVariable Long id) {
        log.debug("REST request to delete SmtpConfig : {}", id);
        smtpConfigService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
