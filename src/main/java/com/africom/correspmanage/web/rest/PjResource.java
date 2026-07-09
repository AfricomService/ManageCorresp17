package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.PjRepository;
import com.africom.correspmanage.service.PjService;
import com.africom.correspmanage.service.dto.PjDTO;
import com.africom.correspmanage.web.rest.errors.BadRequestAlertException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.africom.correspmanage.domain.Pj}.
 */
@RestController
@RequestMapping("/api")
public class PjResource {

    private final Logger log = LoggerFactory.getLogger(PjResource.class);

    private static final String ENTITY_NAME = "correspManagePj";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PjService pjService;

    private final PjRepository pjRepository;

    private final ApplicationProperties applicationProperties;

    private final CorrespRepository correspRepository;

    public PjResource(
        PjService pjService,
        PjRepository pjRepository,
        ApplicationProperties applicationProperties,
        CorrespRepository correspRepository
    ) {
        this.pjService = pjService;
        this.pjRepository = pjRepository;
        this.applicationProperties = applicationProperties;
        this.correspRepository = correspRepository;
    }

    /**
     * {@code POST  /pjs} : Create a new pj.
     *
     * @param pjDTO the pjDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pjDTO, or with status {@code 400 (Bad Request)} if the pj has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pjs")
    public ResponseEntity<PjDTO> createPj(@RequestBody PjDTO pjDTO) throws URISyntaxException {
        log.debug("REST request to save Pj : {}", pjDTO);
        if (pjDTO.getId() != null) {
            throw new BadRequestAlertException("A new pj cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PjDTO result = pjService.save(pjDTO);
        return ResponseEntity
            .created(new URI("/api/pjs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pjs/:id} : Updates an existing pj.
     *
     * @param id the id of the pjDTO to save.
     * @param pjDTO the pjDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pjDTO,
     * or with status {@code 400 (Bad Request)} if the pjDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pjDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pjs/{id}")
    public ResponseEntity<PjDTO> updatePj(@PathVariable(value = "id", required = false) final Long id, @RequestBody PjDTO pjDTO)
        throws URISyntaxException {
        log.debug("REST request to update Pj : {}, {}", id, pjDTO);
        if (pjDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pjDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pjRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PjDTO result = pjService.update(pjDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pjDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pjs/:id} : Partial updates given fields of an existing pj, field will ignore if it is null
     *
     * @param id the id of the pjDTO to save.
     * @param pjDTO the pjDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pjDTO,
     * or with status {@code 400 (Bad Request)} if the pjDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pjDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pjDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pjs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PjDTO> partialUpdatePj(@PathVariable(value = "id", required = false) final Long id, @RequestBody PjDTO pjDTO)
        throws URISyntaxException {
        log.debug("REST request to partial update Pj partially : {}, {}", id, pjDTO);
        if (pjDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pjDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pjRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PjDTO> result = pjService.partialUpdate(pjDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pjDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pjs} : get all the pjs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pjs in body.
     */
    @GetMapping("/pjs")
    public ResponseEntity<List<PjDTO>> getAllPjs(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Pjs");
        Page<PjDTO> page = pjService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pjs/:id} : get the "id" pj.
     *
     * @param id the id of the pjDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pjDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pjs/{id}")
    public ResponseEntity<PjDTO> getPj(@PathVariable Long id) {
        log.debug("REST request to get Pj : {}", id);
        Optional<PjDTO> pjDTO = pjService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pjDTO);
    }

    /**
     * {@code DELETE  /pjs/:id} : delete the "id" pj.
     *
     * @param id the id of the pjDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pjs/{id}")
    public ResponseEntity<Void> deletePj(@PathVariable Long id) {
        log.debug("REST request to delete Pj : {}", id);
        pjService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/pjs/uploadPj")
    public ResponseEntity<String> uploadPj(@RequestParam("file") MultipartFile file, @RequestParam("uniqueName") String uniqueName)
        throws IOException {
        try {
            return ResponseEntity.ok(pjService.uploadFilePj(file, uniqueName));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @PostMapping("/pjs/uploadPjN8N")
    public ResponseEntity<PjDTO> uploadPjN8N(@RequestParam("file") MultipartFile file, @RequestParam("uniqueName") String uniqueName)
        throws IOException {
        try {
            // Save the file and get the final path
            String savedPath = pjService.uploadFilePj(file, uniqueName);

            // Build the DTO to send back to n8n
            PjDTO dto = new PjDTO();
            dto.setPath(savedPath);
            dto.setUniqueName(uniqueName);
            dto.setFileType(file.getContentType());
            dto.setName(file.getOriginalFilename());

            return ResponseEntity.ok(dto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pjs/getFile")
    public ResponseEntity<Resource> getFile(@RequestParam(name = "pjId") Long pjId) {
        return pjService.getFile(pjId);
    }

    @GetMapping("/pjsByCorrespId")
    public List<PjDTO> getAllPjsByCorrespId(@RequestParam(name = "correspId") Long correspId) {
        return pjService.getAllPjsByCorrespId(correspId);
    }

    @DeleteMapping("/pjs")
    public ResponseEntity<Void> deletePj(@RequestParam String uniqueName, @RequestParam String fileType) {
        log.debug("REST request to soft delete Pj : {}", uniqueName);
        pjService.softDelete(uniqueName);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, uniqueName))
            .build();
    }

    @DeleteMapping("/pjsWithCorresp")
    @Transactional
    public ResponseEntity<Void> deletePjWithCorresp(@RequestParam String uniqueName, @RequestParam String fileType) {
        log.debug("REST request to soft delete Pj with corresp : {}", uniqueName);
        pjService.softDelete(uniqueName);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PATCH /pjs/:id/rename} : Renomme une pièce jointe.
     */
    @PatchMapping("/pjs/{id}/rename")
    public ResponseEntity<PjDTO> renamePj(
        @PathVariable Long id,
        @RequestBody com.africom.correspmanage.service.dto.PjRenameDTO renameDTO
    ) {
        log.debug("REST request to rename Pj {} : {}", id, renameDTO.getName());

        if (!pjRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PjDTO result = pjService.renamePj(id, renameDTO.getName());
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }
}
