package com.africom.correspmanage.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.ResponsableTache;
import com.africom.correspmanage.repository.ResponsableTacheRepository;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import com.africom.correspmanage.service.mapper.ResponsableTacheMapper;
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
 * Integration tests for the {@link ResponsableTacheResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResponsableTacheResourceIT {

    private static final Long DEFAULT_TACHE_ID = 1L;
    private static final Long UPDATED_TACHE_ID = 2L;

    private static final String DEFAULT_CODE_DEPT = "AAAAAAAAAA";
    private static final String UPDATED_CODE_DEPT = "BBBBBBBBBB";

    private static final String DEFAULT_MATRICULE_PERS = "AAAAAAAAAA";
    private static final String UPDATED_MATRICULE_PERS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/responsable-taches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResponsableTacheRepository responsableTacheRepository;

    @Autowired
    private ResponsableTacheMapper responsableTacheMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResponsableTacheMockMvc;

    private ResponsableTache responsableTache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResponsableTache createEntity(EntityManager em) {
        ResponsableTache responsableTache = new ResponsableTache()
            .tacheId(DEFAULT_TACHE_ID)
            .codeDept(DEFAULT_CODE_DEPT)
            .matriculePers(DEFAULT_MATRICULE_PERS);
        return responsableTache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResponsableTache createUpdatedEntity(EntityManager em) {
        ResponsableTache responsableTache = new ResponsableTache()
            .tacheId(UPDATED_TACHE_ID)
            .codeDept(UPDATED_CODE_DEPT)
            .matriculePers(UPDATED_MATRICULE_PERS);
        return responsableTache;
    }

    @BeforeEach
    public void initTest() {
        responsableTache = createEntity(em);
    }

    @Test
    @Transactional
    void createResponsableTache() throws Exception {
        int databaseSizeBeforeCreate = responsableTacheRepository.findAll().size();
        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);
        restResponsableTacheMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeCreate + 1);
        ResponsableTache testResponsableTache = responsableTacheList.get(responsableTacheList.size() - 1);
        assertThat(testResponsableTache.getTacheId()).isEqualTo(DEFAULT_TACHE_ID);
        assertThat(testResponsableTache.getCodeDept()).isEqualTo(DEFAULT_CODE_DEPT);
        assertThat(testResponsableTache.getMatriculePers()).isEqualTo(DEFAULT_MATRICULE_PERS);
    }

    @Test
    @Transactional
    void createResponsableTacheWithExistingId() throws Exception {
        // Create the ResponsableTache with an existing ID
        responsableTache.setId(1L);
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        int databaseSizeBeforeCreate = responsableTacheRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResponsableTacheMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllResponsableTaches() throws Exception {
        // Initialize the database
        responsableTacheRepository.saveAndFlush(responsableTache);

        // Get all the responsableTacheList
        restResponsableTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(responsableTache.getId().intValue())))
            .andExpect(jsonPath("$.[*].tacheId").value(hasItem(DEFAULT_TACHE_ID.intValue())))
            .andExpect(jsonPath("$.[*].codeDept").value(hasItem(DEFAULT_CODE_DEPT)))
            .andExpect(jsonPath("$.[*].matriculePers").value(hasItem(DEFAULT_MATRICULE_PERS)));
    }

    @Test
    @Transactional
    void getResponsableTache() throws Exception {
        // Initialize the database
        responsableTacheRepository.saveAndFlush(responsableTache);

        // Get the responsableTache
        restResponsableTacheMockMvc
            .perform(get(ENTITY_API_URL_ID, responsableTache.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(responsableTache.getId().intValue()))
            .andExpect(jsonPath("$.tacheId").value(DEFAULT_TACHE_ID.intValue()))
            .andExpect(jsonPath("$.codeDept").value(DEFAULT_CODE_DEPT))
            .andExpect(jsonPath("$.matriculePers").value(DEFAULT_MATRICULE_PERS));
    }

    @Test
    @Transactional
    void getNonExistingResponsableTache() throws Exception {
        // Get the responsableTache
        restResponsableTacheMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewResponsableTache() throws Exception {
        // Initialize the database
        responsableTacheRepository.saveAndFlush(responsableTache);

        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();

        // Update the responsableTache
        ResponsableTache updatedResponsableTache = responsableTacheRepository.findById(responsableTache.getId()).get();
        // Disconnect from session so that the updates on updatedResponsableTache are not directly saved in db
        em.detach(updatedResponsableTache);
        updatedResponsableTache.tacheId(UPDATED_TACHE_ID).codeDept(UPDATED_CODE_DEPT).matriculePers(UPDATED_MATRICULE_PERS);
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(updatedResponsableTache);

        restResponsableTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, responsableTacheDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isOk());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
        ResponsableTache testResponsableTache = responsableTacheList.get(responsableTacheList.size() - 1);
        assertThat(testResponsableTache.getTacheId()).isEqualTo(UPDATED_TACHE_ID);
        assertThat(testResponsableTache.getCodeDept()).isEqualTo(UPDATED_CODE_DEPT);
        assertThat(testResponsableTache.getMatriculePers()).isEqualTo(UPDATED_MATRICULE_PERS);
    }

    @Test
    @Transactional
    void putNonExistingResponsableTache() throws Exception {
        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();
        responsableTache.setId(count.incrementAndGet());

        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResponsableTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, responsableTacheDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResponsableTache() throws Exception {
        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();
        responsableTache.setId(count.incrementAndGet());

        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResponsableTache() throws Exception {
        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();
        responsableTache.setId(count.incrementAndGet());

        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableTacheMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResponsableTacheWithPatch() throws Exception {
        // Initialize the database
        responsableTacheRepository.saveAndFlush(responsableTache);

        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();

        // Update the responsableTache using partial update
        ResponsableTache partialUpdatedResponsableTache = new ResponsableTache();
        partialUpdatedResponsableTache.setId(responsableTache.getId());

        partialUpdatedResponsableTache.tacheId(UPDATED_TACHE_ID).codeDept(UPDATED_CODE_DEPT);

        restResponsableTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResponsableTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResponsableTache))
            )
            .andExpect(status().isOk());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
        ResponsableTache testResponsableTache = responsableTacheList.get(responsableTacheList.size() - 1);
        assertThat(testResponsableTache.getTacheId()).isEqualTo(UPDATED_TACHE_ID);
        assertThat(testResponsableTache.getCodeDept()).isEqualTo(UPDATED_CODE_DEPT);
        assertThat(testResponsableTache.getMatriculePers()).isEqualTo(DEFAULT_MATRICULE_PERS);
    }

    @Test
    @Transactional
    void fullUpdateResponsableTacheWithPatch() throws Exception {
        // Initialize the database
        responsableTacheRepository.saveAndFlush(responsableTache);

        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();

        // Update the responsableTache using partial update
        ResponsableTache partialUpdatedResponsableTache = new ResponsableTache();
        partialUpdatedResponsableTache.setId(responsableTache.getId());

        partialUpdatedResponsableTache.tacheId(UPDATED_TACHE_ID).codeDept(UPDATED_CODE_DEPT).matriculePers(UPDATED_MATRICULE_PERS);

        restResponsableTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResponsableTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResponsableTache))
            )
            .andExpect(status().isOk());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
        ResponsableTache testResponsableTache = responsableTacheList.get(responsableTacheList.size() - 1);
        assertThat(testResponsableTache.getTacheId()).isEqualTo(UPDATED_TACHE_ID);
        assertThat(testResponsableTache.getCodeDept()).isEqualTo(UPDATED_CODE_DEPT);
        assertThat(testResponsableTache.getMatriculePers()).isEqualTo(UPDATED_MATRICULE_PERS);
    }

    @Test
    @Transactional
    void patchNonExistingResponsableTache() throws Exception {
        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();
        responsableTache.setId(count.incrementAndGet());

        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResponsableTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, responsableTacheDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResponsableTache() throws Exception {
        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();
        responsableTache.setId(count.incrementAndGet());

        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResponsableTache() throws Exception {
        int databaseSizeBeforeUpdate = responsableTacheRepository.findAll().size();
        responsableTache.setId(count.incrementAndGet());

        // Create the ResponsableTache
        ResponsableTacheDTO responsableTacheDTO = responsableTacheMapper.toDto(responsableTache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableTacheMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(responsableTacheDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResponsableTache in the database
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResponsableTache() throws Exception {
        // Initialize the database
        responsableTacheRepository.saveAndFlush(responsableTache);

        int databaseSizeBeforeDelete = responsableTacheRepository.findAll().size();

        // Delete the responsableTache
        restResponsableTacheMockMvc
            .perform(delete(ENTITY_API_URL_ID, responsableTache.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ResponsableTache> responsableTacheList = responsableTacheRepository.findAll();
        assertThat(responsableTacheList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
