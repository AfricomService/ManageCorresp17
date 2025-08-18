package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.Note;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.NoteRepository;
import com.africom.correspmanage.service.dto.NoteDTO;
import com.africom.correspmanage.service.mapper.NoteMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NoteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NoteResourceIT {

    private static final UUID DEFAULT_IDINSTANCE = UUID.randomUUID();
    private static final UUID UPDATED_IDINSTANCE = UUID.randomUUID();

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DATE_PUBLICATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_PUBLICATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DATE_END_PUBLICATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_END_PUBLICATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final CorrespStatus DEFAULT_STATUS = CorrespStatus.DRAFT;
    private static final CorrespStatus UPDATED_STATUS = CorrespStatus.IN_PROGRESS;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/notes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNoteMockMvc;

    private Note note;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Note createEntity(EntityManager em) {
        Note note = new Note()
            .idinstance(DEFAULT_IDINSTANCE)
            .reference(DEFAULT_REFERENCE)
            .subject(DEFAULT_SUBJECT)
            .dateCreated(DEFAULT_DATE_CREATED)
            .datePublication(DEFAULT_DATE_PUBLICATION)
            .dateEndPublication(DEFAULT_DATE_END_PUBLICATION)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedDate(DEFAULT_UPDATED_DATE);
        return note;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Note createUpdatedEntity(EntityManager em) {
        Note note = new Note()
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateEndPublication(UPDATED_DATE_END_PUBLICATION)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);
        return note;
    }

    @BeforeEach
    public void initTest() {
        note = createEntity(em);
    }

    @Test
    @Transactional
    void createNote() throws Exception {
        int databaseSizeBeforeCreate = noteRepository.findAll().size();
        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);
        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isCreated());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeCreate + 1);
        Note testNote = noteList.get(noteList.size() - 1);
        assertThat(testNote.getIdinstance()).isEqualTo(DEFAULT_IDINSTANCE);
        assertThat(testNote.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testNote.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNote.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
        assertThat(testNote.getDatePublication()).isEqualTo(DEFAULT_DATE_PUBLICATION);
        assertThat(testNote.getDateEndPublication()).isEqualTo(DEFAULT_DATE_END_PUBLICATION);
        assertThat(testNote.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testNote.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testNote.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testNote.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
    }

    @Test
    @Transactional
    void createNoteWithExistingId() throws Exception {
        // Create the Note with an existing ID
        note.setId(1L);
        NoteDTO noteDTO = noteMapper.toDto(note);

        int databaseSizeBeforeCreate = noteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIdinstanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteRepository.findAll().size();
        // set the field null
        note.setIdinstance(null);

        // Create the Note, which fails.
        NoteDTO noteDTO = noteMapper.toDto(note);

        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteRepository.findAll().size();
        // set the field null
        note.setSubject(null);

        // Create the Note, which fails.
        NoteDTO noteDTO = noteMapper.toDto(note);

        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteRepository.findAll().size();
        // set the field null
        note.setDateCreated(null);

        // Create the Note, which fails.
        NoteDTO noteDTO = noteMapper.toDto(note);

        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatePublicationIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteRepository.findAll().size();
        // set the field null
        note.setDatePublication(null);

        // Create the Note, which fails.
        NoteDTO noteDTO = noteMapper.toDto(note);

        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateEndPublicationIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteRepository.findAll().size();
        // set the field null
        note.setDateEndPublication(null);

        // Create the Note, which fails.
        NoteDTO noteDTO = noteMapper.toDto(note);

        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = noteRepository.findAll().size();
        // set the field null
        note.setStatus(null);

        // Create the Note, which fails.
        NoteDTO noteDTO = noteMapper.toDto(note);

        restNoteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isBadRequest());

        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotes() throws Exception {
        // Initialize the database
        noteRepository.saveAndFlush(note);

        // Get all the noteList
        restNoteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(note.getId().intValue())))
            .andExpect(jsonPath("$.[*].idinstance").value(hasItem(DEFAULT_IDINSTANCE.toString())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(sameInstant(DEFAULT_DATE_CREATED))))
            .andExpect(jsonPath("$.[*].datePublication").value(hasItem(sameInstant(DEFAULT_DATE_PUBLICATION))))
            .andExpect(jsonPath("$.[*].dateEndPublication").value(hasItem(sameInstant(DEFAULT_DATE_END_PUBLICATION))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))));
    }

    @Test
    @Transactional
    void getNote() throws Exception {
        // Initialize the database
        noteRepository.saveAndFlush(note);

        // Get the note
        restNoteMockMvc
            .perform(get(ENTITY_API_URL_ID, note.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(note.getId().intValue()))
            .andExpect(jsonPath("$.idinstance").value(DEFAULT_IDINSTANCE.toString()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.dateCreated").value(sameInstant(DEFAULT_DATE_CREATED)))
            .andExpect(jsonPath("$.datePublication").value(sameInstant(DEFAULT_DATE_PUBLICATION)))
            .andExpect(jsonPath("$.dateEndPublication").value(sameInstant(DEFAULT_DATE_END_PUBLICATION)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingNote() throws Exception {
        // Get the note
        restNoteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNote() throws Exception {
        // Initialize the database
        noteRepository.saveAndFlush(note);

        int databaseSizeBeforeUpdate = noteRepository.findAll().size();

        // Update the note
        Note updatedNote = noteRepository.findById(note.getId()).get();
        // Disconnect from session so that the updates on updatedNote are not directly saved in db
        em.detach(updatedNote);
        updatedNote
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateEndPublication(UPDATED_DATE_END_PUBLICATION)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);
        NoteDTO noteDTO = noteMapper.toDto(updatedNote);

        restNoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, noteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(noteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
        Note testNote = noteList.get(noteList.size() - 1);
        assertThat(testNote.getIdinstance()).isEqualTo(UPDATED_IDINSTANCE);
        assertThat(testNote.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testNote.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testNote.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
        assertThat(testNote.getDatePublication()).isEqualTo(UPDATED_DATE_PUBLICATION);
        assertThat(testNote.getDateEndPublication()).isEqualTo(UPDATED_DATE_END_PUBLICATION);
        assertThat(testNote.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNote.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testNote.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testNote.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingNote() throws Exception {
        int databaseSizeBeforeUpdate = noteRepository.findAll().size();
        note.setId(count.incrementAndGet());

        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, noteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(noteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNote() throws Exception {
        int databaseSizeBeforeUpdate = noteRepository.findAll().size();
        note.setId(count.incrementAndGet());

        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(noteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNote() throws Exception {
        int databaseSizeBeforeUpdate = noteRepository.findAll().size();
        note.setId(count.incrementAndGet());

        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNoteWithPatch() throws Exception {
        // Initialize the database
        noteRepository.saveAndFlush(note);

        int databaseSizeBeforeUpdate = noteRepository.findAll().size();

        // Update the note using partial update
        Note partialUpdatedNote = new Note();
        partialUpdatedNote.setId(note.getId());

        partialUpdatedNote.idinstance(UPDATED_IDINSTANCE).status(UPDATED_STATUS);

        restNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNote.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNote))
            )
            .andExpect(status().isOk());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
        Note testNote = noteList.get(noteList.size() - 1);
        assertThat(testNote.getIdinstance()).isEqualTo(UPDATED_IDINSTANCE);
        assertThat(testNote.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testNote.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNote.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
        assertThat(testNote.getDatePublication()).isEqualTo(DEFAULT_DATE_PUBLICATION);
        assertThat(testNote.getDateEndPublication()).isEqualTo(DEFAULT_DATE_END_PUBLICATION);
        assertThat(testNote.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNote.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testNote.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testNote.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateNoteWithPatch() throws Exception {
        // Initialize the database
        noteRepository.saveAndFlush(note);

        int databaseSizeBeforeUpdate = noteRepository.findAll().size();

        // Update the note using partial update
        Note partialUpdatedNote = new Note();
        partialUpdatedNote.setId(note.getId());

        partialUpdatedNote
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateEndPublication(UPDATED_DATE_END_PUBLICATION)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);

        restNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNote.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNote))
            )
            .andExpect(status().isOk());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
        Note testNote = noteList.get(noteList.size() - 1);
        assertThat(testNote.getIdinstance()).isEqualTo(UPDATED_IDINSTANCE);
        assertThat(testNote.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testNote.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testNote.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
        assertThat(testNote.getDatePublication()).isEqualTo(UPDATED_DATE_PUBLICATION);
        assertThat(testNote.getDateEndPublication()).isEqualTo(UPDATED_DATE_END_PUBLICATION);
        assertThat(testNote.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testNote.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testNote.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testNote.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingNote() throws Exception {
        int databaseSizeBeforeUpdate = noteRepository.findAll().size();
        note.setId(count.incrementAndGet());

        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, noteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(noteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNote() throws Exception {
        int databaseSizeBeforeUpdate = noteRepository.findAll().size();
        note.setId(count.incrementAndGet());

        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(noteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNote() throws Exception {
        int databaseSizeBeforeUpdate = noteRepository.findAll().size();
        note.setId(count.incrementAndGet());

        // Create the Note
        NoteDTO noteDTO = noteMapper.toDto(note);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNoteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(noteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Note in the database
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNote() throws Exception {
        // Initialize the database
        noteRepository.saveAndFlush(note);

        int databaseSizeBeforeDelete = noteRepository.findAll().size();

        // Delete the note
        restNoteMockMvc
            .perform(delete(ENTITY_API_URL_ID, note.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Note> noteList = noteRepository.findAll();
        assertThat(noteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
