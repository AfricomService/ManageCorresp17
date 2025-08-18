package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.enumeration.UsualStatus;
import com.africom.correspmanage.repository.PjRepository;
import com.africom.correspmanage.service.dto.PjDTO;
import com.africom.correspmanage.service.mapper.PjMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
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
 * Integration tests for the {@link PjResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PjResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PATH = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_TRANSFERABLE = false;
    private static final Boolean UPDATED_TRANSFERABLE = true;

    private static final String DEFAULT_UNIQUE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_UNIQUE_NAME = "BBBBBBBBBB";

    private static final UsualStatus DEFAULT_STATUS = UsualStatus.ACTIVE;
    private static final UsualStatus UPDATED_STATUS = UsualStatus.DELETED;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/pjs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PjRepository pjRepository;

    @Autowired
    private PjMapper pjMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPjMockMvc;

    private Pj pj;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pj createEntity(EntityManager em) {
        Pj pj = new Pj()
            .name(DEFAULT_NAME)
            .fileType(DEFAULT_FILE_TYPE)
            .path(DEFAULT_PATH)
            .creationDate(DEFAULT_CREATION_DATE)
            .password(DEFAULT_PASSWORD)
            .transferable(DEFAULT_TRANSFERABLE)
            .uniqueName(DEFAULT_UNIQUE_NAME)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedDate(DEFAULT_UPDATED_DATE);
        return pj;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pj createUpdatedEntity(EntityManager em) {
        Pj pj = new Pj()
            .name(UPDATED_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .path(UPDATED_PATH)
            .creationDate(UPDATED_CREATION_DATE)
            .password(UPDATED_PASSWORD)
            .transferable(UPDATED_TRANSFERABLE)
            .uniqueName(UPDATED_UNIQUE_NAME)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);
        return pj;
    }

    @BeforeEach
    public void initTest() {
        pj = createEntity(em);
    }

    @Test
    @Transactional
    void createPj() throws Exception {
        int databaseSizeBeforeCreate = pjRepository.findAll().size();
        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);
        restPjMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pjDTO)))
            .andExpect(status().isCreated());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeCreate + 1);
        Pj testPj = pjList.get(pjList.size() - 1);
        assertThat(testPj.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPj.getFileType()).isEqualTo(DEFAULT_FILE_TYPE);
        assertThat(testPj.getPath()).isEqualTo(DEFAULT_PATH);
        assertThat(testPj.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testPj.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testPj.getTransferable()).isEqualTo(DEFAULT_TRANSFERABLE);
        assertThat(testPj.getUniqueName()).isEqualTo(DEFAULT_UNIQUE_NAME);
        assertThat(testPj.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPj.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testPj.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPj.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
    }

    @Test
    @Transactional
    void createPjWithExistingId() throws Exception {
        // Create the Pj with an existing ID
        pj.setId(1L);
        PjDTO pjDTO = pjMapper.toDto(pj);

        int databaseSizeBeforeCreate = pjRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPjMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pjDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPjs() throws Exception {
        // Initialize the database
        pjRepository.saveAndFlush(pj);

        // Get all the pjList
        restPjMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pj.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].transferable").value(hasItem(DEFAULT_TRANSFERABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].uniqueName").value(hasItem(DEFAULT_UNIQUE_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))));
    }

    @Test
    @Transactional
    void getPj() throws Exception {
        // Initialize the database
        pjRepository.saveAndFlush(pj);

        // Get the pj
        restPjMockMvc
            .perform(get(ENTITY_API_URL_ID, pj.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pj.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH))
            .andExpect(jsonPath("$.creationDate").value(sameInstant(DEFAULT_CREATION_DATE)))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.transferable").value(DEFAULT_TRANSFERABLE.booleanValue()))
            .andExpect(jsonPath("$.uniqueName").value(DEFAULT_UNIQUE_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingPj() throws Exception {
        // Get the pj
        restPjMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPj() throws Exception {
        // Initialize the database
        pjRepository.saveAndFlush(pj);

        int databaseSizeBeforeUpdate = pjRepository.findAll().size();

        // Update the pj
        Pj updatedPj = pjRepository.findById(pj.getId()).get();
        // Disconnect from session so that the updates on updatedPj are not directly saved in db
        em.detach(updatedPj);
        updatedPj
            .name(UPDATED_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .path(UPDATED_PATH)
            .creationDate(UPDATED_CREATION_DATE)
            .password(UPDATED_PASSWORD)
            .transferable(UPDATED_TRANSFERABLE)
            .uniqueName(UPDATED_UNIQUE_NAME)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);
        PjDTO pjDTO = pjMapper.toDto(updatedPj);

        restPjMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pjDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pjDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
        Pj testPj = pjList.get(pjList.size() - 1);
        assertThat(testPj.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPj.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testPj.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testPj.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testPj.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testPj.getTransferable()).isEqualTo(UPDATED_TRANSFERABLE);
        assertThat(testPj.getUniqueName()).isEqualTo(UPDATED_UNIQUE_NAME);
        assertThat(testPj.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPj.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPj.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPj.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingPj() throws Exception {
        int databaseSizeBeforeUpdate = pjRepository.findAll().size();
        pj.setId(count.incrementAndGet());

        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPjMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pjDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pjDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPj() throws Exception {
        int databaseSizeBeforeUpdate = pjRepository.findAll().size();
        pj.setId(count.incrementAndGet());

        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPjMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pjDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPj() throws Exception {
        int databaseSizeBeforeUpdate = pjRepository.findAll().size();
        pj.setId(count.incrementAndGet());

        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPjMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pjDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePjWithPatch() throws Exception {
        // Initialize the database
        pjRepository.saveAndFlush(pj);

        int databaseSizeBeforeUpdate = pjRepository.findAll().size();

        // Update the pj using partial update
        Pj partialUpdatedPj = new Pj();
        partialUpdatedPj.setId(pj.getId());

        partialUpdatedPj
            .name(UPDATED_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .path(UPDATED_PATH)
            .creationDate(UPDATED_CREATION_DATE)
            .transferable(UPDATED_TRANSFERABLE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);

        restPjMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPj.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPj))
            )
            .andExpect(status().isOk());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
        Pj testPj = pjList.get(pjList.size() - 1);
        assertThat(testPj.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPj.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testPj.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testPj.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testPj.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testPj.getTransferable()).isEqualTo(UPDATED_TRANSFERABLE);
        assertThat(testPj.getUniqueName()).isEqualTo(DEFAULT_UNIQUE_NAME);
        assertThat(testPj.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPj.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPj.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testPj.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdatePjWithPatch() throws Exception {
        // Initialize the database
        pjRepository.saveAndFlush(pj);

        int databaseSizeBeforeUpdate = pjRepository.findAll().size();

        // Update the pj using partial update
        Pj partialUpdatedPj = new Pj();
        partialUpdatedPj.setId(pj.getId());

        partialUpdatedPj
            .name(UPDATED_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .path(UPDATED_PATH)
            .creationDate(UPDATED_CREATION_DATE)
            .password(UPDATED_PASSWORD)
            .transferable(UPDATED_TRANSFERABLE)
            .uniqueName(UPDATED_UNIQUE_NAME)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);

        restPjMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPj.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPj))
            )
            .andExpect(status().isOk());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
        Pj testPj = pjList.get(pjList.size() - 1);
        assertThat(testPj.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPj.getFileType()).isEqualTo(UPDATED_FILE_TYPE);
        assertThat(testPj.getPath()).isEqualTo(UPDATED_PATH);
        assertThat(testPj.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testPj.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testPj.getTransferable()).isEqualTo(UPDATED_TRANSFERABLE);
        assertThat(testPj.getUniqueName()).isEqualTo(UPDATED_UNIQUE_NAME);
        assertThat(testPj.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPj.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testPj.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testPj.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingPj() throws Exception {
        int databaseSizeBeforeUpdate = pjRepository.findAll().size();
        pj.setId(count.incrementAndGet());

        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPjMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pjDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pjDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPj() throws Exception {
        int databaseSizeBeforeUpdate = pjRepository.findAll().size();
        pj.setId(count.incrementAndGet());

        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPjMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pjDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPj() throws Exception {
        int databaseSizeBeforeUpdate = pjRepository.findAll().size();
        pj.setId(count.incrementAndGet());

        // Create the Pj
        PjDTO pjDTO = pjMapper.toDto(pj);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPjMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pjDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pj in the database
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePj() throws Exception {
        // Initialize the database
        pjRepository.saveAndFlush(pj);

        int databaseSizeBeforeDelete = pjRepository.findAll().size();

        // Delete the pj
        restPjMockMvc.perform(delete(ENTITY_API_URL_ID, pj.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pj> pjList = pjRepository.findAll();
        assertThat(pjList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
