package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.Dossier;
import com.africom.correspmanage.domain.enumeration.UsualStatus;
import com.africom.correspmanage.repository.DossierRepository;
import com.africom.correspmanage.service.dto.DossierDTO;
import com.africom.correspmanage.service.mapper.DossierMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link DossierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DossierResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String DEFAULT_RESPONSIBLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSIBLE = "BBBBBBBBBB";

    private static final UsualStatus DEFAULT_STATUS = UsualStatus.ACTIVE;
    private static final UsualStatus UPDATED_STATUS = UsualStatus.DELETED;

    private static final byte[] DEFAULT_DOSSIER_LIST = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DOSSIER_LIST = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DOSSIER_LIST_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DOSSIER_LIST_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/dossiers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private DossierMapper dossierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDossierMockMvc;

    private Dossier dossier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dossier createEntity(EntityManager em) {
        Dossier dossier = new Dossier()
            .name(DEFAULT_NAME)
            .isPublic(DEFAULT_IS_PUBLIC)
            .responsible(DEFAULT_RESPONSIBLE)
            .status(DEFAULT_STATUS)
            .dossierList(DEFAULT_DOSSIER_LIST)
            .dossierListContentType(DEFAULT_DOSSIER_LIST_CONTENT_TYPE)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedDate(DEFAULT_UPDATED_DATE);
        return dossier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dossier createUpdatedEntity(EntityManager em) {
        Dossier dossier = new Dossier()
            .name(UPDATED_NAME)
            .isPublic(UPDATED_IS_PUBLIC)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS)
            .dossierList(UPDATED_DOSSIER_LIST)
            .dossierListContentType(UPDATED_DOSSIER_LIST_CONTENT_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);
        return dossier;
    }

    @BeforeEach
    public void initTest() {
        dossier = createEntity(em);
    }

    @Test
    @Transactional
    void createDossier() throws Exception {
        int databaseSizeBeforeCreate = dossierRepository.findAll().size();
        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);
        restDossierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dossierDTO)))
            .andExpect(status().isCreated());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeCreate + 1);
        Dossier testDossier = dossierList.get(dossierList.size() - 1);
        assertThat(testDossier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDossier.getIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testDossier.getResponsible()).isEqualTo(DEFAULT_RESPONSIBLE);
        assertThat(testDossier.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDossier.getDossierList()).isEqualTo(DEFAULT_DOSSIER_LIST);
        assertThat(testDossier.getDossierListContentType()).isEqualTo(DEFAULT_DOSSIER_LIST_CONTENT_TYPE);
        assertThat(testDossier.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDossier.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testDossier.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
    }

    @Test
    @Transactional
    void createDossierWithExistingId() throws Exception {
        // Create the Dossier with an existing ID
        dossier.setId(1L);
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        int databaseSizeBeforeCreate = dossierRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDossierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dossierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDossiers() throws Exception {
        // Initialize the database
        dossierRepository.saveAndFlush(dossier);

        // Get all the dossierList
        restDossierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dossier.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].responsible").value(hasItem(DEFAULT_RESPONSIBLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dossierListContentType").value(hasItem(DEFAULT_DOSSIER_LIST_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].dossierList").value(hasItem(Base64Utils.encodeToString(DEFAULT_DOSSIER_LIST))))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))));
    }

    @Test
    @Transactional
    void getDossier() throws Exception {
        // Initialize the database
        dossierRepository.saveAndFlush(dossier);

        // Get the dossier
        restDossierMockMvc
            .perform(get(ENTITY_API_URL_ID, dossier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dossier.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()))
            .andExpect(jsonPath("$.responsible").value(DEFAULT_RESPONSIBLE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.dossierListContentType").value(DEFAULT_DOSSIER_LIST_CONTENT_TYPE))
            .andExpect(jsonPath("$.dossierList").value(Base64Utils.encodeToString(DEFAULT_DOSSIER_LIST)))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingDossier() throws Exception {
        // Get the dossier
        restDossierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDossier() throws Exception {
        // Initialize the database
        dossierRepository.saveAndFlush(dossier);

        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();

        // Update the dossier
        Dossier updatedDossier = dossierRepository.findById(dossier.getId()).get();
        // Disconnect from session so that the updates on updatedDossier are not directly saved in db
        em.detach(updatedDossier);
        updatedDossier
            .name(UPDATED_NAME)
            .isPublic(UPDATED_IS_PUBLIC)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS)
            .dossierList(UPDATED_DOSSIER_LIST)
            .dossierListContentType(UPDATED_DOSSIER_LIST_CONTENT_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);
        DossierDTO dossierDTO = dossierMapper.toDto(updatedDossier);

        restDossierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dossierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dossierDTO))
            )
            .andExpect(status().isOk());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
        Dossier testDossier = dossierList.get(dossierList.size() - 1);
        assertThat(testDossier.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDossier.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testDossier.getResponsible()).isEqualTo(UPDATED_RESPONSIBLE);
        assertThat(testDossier.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDossier.getDossierList()).isEqualTo(UPDATED_DOSSIER_LIST);
        assertThat(testDossier.getDossierListContentType()).isEqualTo(UPDATED_DOSSIER_LIST_CONTENT_TYPE);
        assertThat(testDossier.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDossier.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testDossier.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingDossier() throws Exception {
        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();
        dossier.setId(count.incrementAndGet());

        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDossierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dossierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dossierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDossier() throws Exception {
        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();
        dossier.setId(count.incrementAndGet());

        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDossierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dossierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDossier() throws Exception {
        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();
        dossier.setId(count.incrementAndGet());

        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDossierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dossierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDossierWithPatch() throws Exception {
        // Initialize the database
        dossierRepository.saveAndFlush(dossier);

        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();

        // Update the dossier using partial update
        Dossier partialUpdatedDossier = new Dossier();
        partialUpdatedDossier.setId(dossier.getId());

        partialUpdatedDossier
            .responsible(UPDATED_RESPONSIBLE)
            .dossierList(UPDATED_DOSSIER_LIST)
            .dossierListContentType(UPDATED_DOSSIER_LIST_CONTENT_TYPE)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);

        restDossierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDossier.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDossier))
            )
            .andExpect(status().isOk());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
        Dossier testDossier = dossierList.get(dossierList.size() - 1);
        assertThat(testDossier.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDossier.getIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testDossier.getResponsible()).isEqualTo(UPDATED_RESPONSIBLE);
        assertThat(testDossier.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDossier.getDossierList()).isEqualTo(UPDATED_DOSSIER_LIST);
        assertThat(testDossier.getDossierListContentType()).isEqualTo(UPDATED_DOSSIER_LIST_CONTENT_TYPE);
        assertThat(testDossier.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDossier.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testDossier.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateDossierWithPatch() throws Exception {
        // Initialize the database
        dossierRepository.saveAndFlush(dossier);

        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();

        // Update the dossier using partial update
        Dossier partialUpdatedDossier = new Dossier();
        partialUpdatedDossier.setId(dossier.getId());

        partialUpdatedDossier
            .name(UPDATED_NAME)
            .isPublic(UPDATED_IS_PUBLIC)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS)
            .dossierList(UPDATED_DOSSIER_LIST)
            .dossierListContentType(UPDATED_DOSSIER_LIST_CONTENT_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE);

        restDossierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDossier.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDossier))
            )
            .andExpect(status().isOk());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
        Dossier testDossier = dossierList.get(dossierList.size() - 1);
        assertThat(testDossier.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDossier.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testDossier.getResponsible()).isEqualTo(UPDATED_RESPONSIBLE);
        assertThat(testDossier.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDossier.getDossierList()).isEqualTo(UPDATED_DOSSIER_LIST);
        assertThat(testDossier.getDossierListContentType()).isEqualTo(UPDATED_DOSSIER_LIST_CONTENT_TYPE);
        assertThat(testDossier.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDossier.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testDossier.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingDossier() throws Exception {
        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();
        dossier.setId(count.incrementAndGet());

        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDossierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dossierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dossierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDossier() throws Exception {
        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();
        dossier.setId(count.incrementAndGet());

        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDossierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dossierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDossier() throws Exception {
        int databaseSizeBeforeUpdate = dossierRepository.findAll().size();
        dossier.setId(count.incrementAndGet());

        // Create the Dossier
        DossierDTO dossierDTO = dossierMapper.toDto(dossier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDossierMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dossierDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dossier in the database
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDossier() throws Exception {
        // Initialize the database
        dossierRepository.saveAndFlush(dossier);

        int databaseSizeBeforeDelete = dossierRepository.findAll().size();

        // Delete the dossier
        restDossierMockMvc
            .perform(delete(ENTITY_API_URL_ID, dossier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dossier> dossierList = dossierRepository.findAll();
        assertThat(dossierList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
