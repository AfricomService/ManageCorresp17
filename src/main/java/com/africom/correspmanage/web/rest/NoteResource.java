package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.repository.NoteRepository;
import com.africom.correspmanage.service.NoteService;
import com.africom.correspmanage.service.dto.NoteDTO;
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
 * REST controller for managing {@link com.africom.correspmanage.domain.Note}.
 */
@RestController
@RequestMapping("/api")
public class NoteResource {

    private final Logger log = LoggerFactory.getLogger(NoteResource.class);

    private static final String ENTITY_NAME = "correspManageNote";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NoteService noteService;

    private final NoteRepository noteRepository;

    public NoteResource(NoteService noteService, NoteRepository noteRepository) {
        this.noteService = noteService;
        this.noteRepository = noteRepository;
    }

    /**
     * {@code POST  /notes} : Create a new note.
     *
     * @param noteDTO the noteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new noteDTO, or with status {@code 400 (Bad Request)} if the note has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notes")
    public ResponseEntity<NoteDTO> createNote(@Valid @RequestBody NoteDTO noteDTO) throws URISyntaxException {
        log.debug("REST request to save Note : {}", noteDTO);
        if (noteDTO.getId() != null) {
            throw new BadRequestAlertException("A new note cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NoteDTO result = noteService.save(noteDTO);
        return ResponseEntity
            .created(new URI("/api/notes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notes/:id} : Updates an existing note.
     *
     * @param id the id of the noteDTO to save.
     * @param noteDTO the noteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated noteDTO,
     * or with status {@code 400 (Bad Request)} if the noteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the noteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notes/{id}")
    public ResponseEntity<NoteDTO> updateNote(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NoteDTO noteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Note : {}, {}", id, noteDTO);
        if (noteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, noteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        NoteDTO result = noteService.update(noteDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, noteDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /notes/:id} : Partial updates given fields of an existing note, field will ignore if it is null
     *
     * @param id the id of the noteDTO to save.
     * @param noteDTO the noteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated noteDTO,
     * or with status {@code 400 (Bad Request)} if the noteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the noteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the noteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NoteDTO> partialUpdateNote(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NoteDTO noteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Note partially : {}, {}", id, noteDTO);
        if (noteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, noteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!noteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NoteDTO> result = noteService.partialUpdate(noteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, noteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notes} : get all the notes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notes in body.
     */
    @GetMapping("/notes")
    public ResponseEntity<List<NoteDTO>> getAllNotes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Notes");
        Page<NoteDTO> page = noteService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notes/:id} : get the "id" note.
     *
     * @param id the id of the noteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the noteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notes/{id}")
    public ResponseEntity<NoteDTO> getNote(@PathVariable Long id) {
        log.debug("REST request to get Note : {}", id);
        Optional<NoteDTO> noteDTO = noteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(noteDTO);
    }

    /**
     * {@code DELETE  /notes/:id} : delete the "id" note.
     *
     * @param id the id of the noteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.debug("REST request to delete Note : {}", id);
        noteService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
