package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.CustomSearch;
import com.africom.correspmanage.repository.CustomSearchRepository;
import com.africom.correspmanage.service.dto.CustomSearchDTO;
import com.africom.correspmanage.service.mapper.CustomSearchMapper;
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
 * Integration tests for the {@link CustomSearchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CustomSearchResourceIT {

    private static final ZonedDateTime DEFAULT_DATE_CREATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_CREATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_READ_ACCESS = "AAAAAAAAAA";
    private static final String UPDATED_READ_ACCESS = "BBBBBBBBBB";

    private static final String DEFAULT_VISIBILITY = "AAAAAAAAAA";
    private static final String UPDATED_VISIBILITY = "BBBBBBBBBB";

    private static final String DEFAULT_REQUEST = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITY_SEARCHING = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_SEARCHING = "BBBBBBBBBB";

    private static final String DEFAULT_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/custom-searches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CustomSearchRepository customSearchRepository;

    @Autowired
    private CustomSearchMapper customSearchMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomSearchMockMvc;

    private CustomSearch customSearch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomSearch createEntity(EntityManager em) {
        CustomSearch customSearch = new CustomSearch()
            .dateCreation(DEFAULT_DATE_CREATION)
            .readAccess(DEFAULT_READ_ACCESS)
            .visibility(DEFAULT_VISIBILITY)
            .request(DEFAULT_REQUEST)
            .entitySearching(DEFAULT_ENTITY_SEARCHING)
            .userLogin(DEFAULT_USER_LOGIN)
            .name(DEFAULT_NAME);
        return customSearch;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomSearch createUpdatedEntity(EntityManager em) {
        CustomSearch customSearch = new CustomSearch()
            .dateCreation(UPDATED_DATE_CREATION)
            .readAccess(UPDATED_READ_ACCESS)
            .visibility(UPDATED_VISIBILITY)
            .request(UPDATED_REQUEST)
            .entitySearching(UPDATED_ENTITY_SEARCHING)
            .userLogin(UPDATED_USER_LOGIN)
            .name(UPDATED_NAME);
        return customSearch;
    }

    @BeforeEach
    public void initTest() {
        customSearch = createEntity(em);
    }

    @Test
    @Transactional
    void createCustomSearch() throws Exception {
        int databaseSizeBeforeCreate = customSearchRepository.findAll().size();
        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);
        restCustomSearchMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeCreate + 1);
        CustomSearch testCustomSearch = customSearchList.get(customSearchList.size() - 1);
        assertThat(testCustomSearch.getDateCreation()).isEqualTo(DEFAULT_DATE_CREATION);
        assertThat(testCustomSearch.getReadAccess()).isEqualTo(DEFAULT_READ_ACCESS);
        assertThat(testCustomSearch.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testCustomSearch.getRequest()).isEqualTo(DEFAULT_REQUEST);
        assertThat(testCustomSearch.getEntitySearching()).isEqualTo(DEFAULT_ENTITY_SEARCHING);
        assertThat(testCustomSearch.getUserLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(testCustomSearch.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createCustomSearchWithExistingId() throws Exception {
        // Create the CustomSearch with an existing ID
        customSearch.setId(1L);
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        int databaseSizeBeforeCreate = customSearchRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomSearchMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCustomSearches() throws Exception {
        // Initialize the database
        customSearchRepository.saveAndFlush(customSearch);

        // Get all the customSearchList
        restCustomSearchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customSearch.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(sameInstant(DEFAULT_DATE_CREATION))))
            .andExpect(jsonPath("$.[*].readAccess").value(hasItem(DEFAULT_READ_ACCESS)))
            .andExpect(jsonPath("$.[*].visibility").value(hasItem(DEFAULT_VISIBILITY)))
            .andExpect(jsonPath("$.[*].request").value(hasItem(DEFAULT_REQUEST)))
            .andExpect(jsonPath("$.[*].entitySearching").value(hasItem(DEFAULT_ENTITY_SEARCHING)))
            .andExpect(jsonPath("$.[*].userLogin").value(hasItem(DEFAULT_USER_LOGIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getCustomSearch() throws Exception {
        // Initialize the database
        customSearchRepository.saveAndFlush(customSearch);

        // Get the customSearch
        restCustomSearchMockMvc
            .perform(get(ENTITY_API_URL_ID, customSearch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customSearch.getId().intValue()))
            .andExpect(jsonPath("$.dateCreation").value(sameInstant(DEFAULT_DATE_CREATION)))
            .andExpect(jsonPath("$.readAccess").value(DEFAULT_READ_ACCESS))
            .andExpect(jsonPath("$.visibility").value(DEFAULT_VISIBILITY))
            .andExpect(jsonPath("$.request").value(DEFAULT_REQUEST))
            .andExpect(jsonPath("$.entitySearching").value(DEFAULT_ENTITY_SEARCHING))
            .andExpect(jsonPath("$.userLogin").value(DEFAULT_USER_LOGIN))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingCustomSearch() throws Exception {
        // Get the customSearch
        restCustomSearchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCustomSearch() throws Exception {
        // Initialize the database
        customSearchRepository.saveAndFlush(customSearch);

        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();

        // Update the customSearch
        CustomSearch updatedCustomSearch = customSearchRepository.findById(customSearch.getId()).get();
        // Disconnect from session so that the updates on updatedCustomSearch are not directly saved in db
        em.detach(updatedCustomSearch);
        updatedCustomSearch
            .dateCreation(UPDATED_DATE_CREATION)
            .readAccess(UPDATED_READ_ACCESS)
            .visibility(UPDATED_VISIBILITY)
            .request(UPDATED_REQUEST)
            .entitySearching(UPDATED_ENTITY_SEARCHING)
            .userLogin(UPDATED_USER_LOGIN)
            .name(UPDATED_NAME);
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(updatedCustomSearch);

        restCustomSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customSearchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isOk());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
        CustomSearch testCustomSearch = customSearchList.get(customSearchList.size() - 1);
        assertThat(testCustomSearch.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
        assertThat(testCustomSearch.getReadAccess()).isEqualTo(UPDATED_READ_ACCESS);
        assertThat(testCustomSearch.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testCustomSearch.getRequest()).isEqualTo(UPDATED_REQUEST);
        assertThat(testCustomSearch.getEntitySearching()).isEqualTo(UPDATED_ENTITY_SEARCHING);
        assertThat(testCustomSearch.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testCustomSearch.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingCustomSearch() throws Exception {
        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();
        customSearch.setId(count.incrementAndGet());

        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, customSearchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCustomSearch() throws Exception {
        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();
        customSearch.setId(count.incrementAndGet());

        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCustomSearch() throws Exception {
        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();
        customSearch.setId(count.incrementAndGet());

        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomSearchMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCustomSearchWithPatch() throws Exception {
        // Initialize the database
        customSearchRepository.saveAndFlush(customSearch);

        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();

        // Update the customSearch using partial update
        CustomSearch partialUpdatedCustomSearch = new CustomSearch();
        partialUpdatedCustomSearch.setId(customSearch.getId());

        partialUpdatedCustomSearch.dateCreation(UPDATED_DATE_CREATION).userLogin(UPDATED_USER_LOGIN);

        restCustomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomSearch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomSearch))
            )
            .andExpect(status().isOk());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
        CustomSearch testCustomSearch = customSearchList.get(customSearchList.size() - 1);
        assertThat(testCustomSearch.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
        assertThat(testCustomSearch.getReadAccess()).isEqualTo(DEFAULT_READ_ACCESS);
        assertThat(testCustomSearch.getVisibility()).isEqualTo(DEFAULT_VISIBILITY);
        assertThat(testCustomSearch.getRequest()).isEqualTo(DEFAULT_REQUEST);
        assertThat(testCustomSearch.getEntitySearching()).isEqualTo(DEFAULT_ENTITY_SEARCHING);
        assertThat(testCustomSearch.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testCustomSearch.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateCustomSearchWithPatch() throws Exception {
        // Initialize the database
        customSearchRepository.saveAndFlush(customSearch);

        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();

        // Update the customSearch using partial update
        CustomSearch partialUpdatedCustomSearch = new CustomSearch();
        partialUpdatedCustomSearch.setId(customSearch.getId());

        partialUpdatedCustomSearch
            .dateCreation(UPDATED_DATE_CREATION)
            .readAccess(UPDATED_READ_ACCESS)
            .visibility(UPDATED_VISIBILITY)
            .request(UPDATED_REQUEST)
            .entitySearching(UPDATED_ENTITY_SEARCHING)
            .userLogin(UPDATED_USER_LOGIN)
            .name(UPDATED_NAME);

        restCustomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCustomSearch.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCustomSearch))
            )
            .andExpect(status().isOk());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
        CustomSearch testCustomSearch = customSearchList.get(customSearchList.size() - 1);
        assertThat(testCustomSearch.getDateCreation()).isEqualTo(UPDATED_DATE_CREATION);
        assertThat(testCustomSearch.getReadAccess()).isEqualTo(UPDATED_READ_ACCESS);
        assertThat(testCustomSearch.getVisibility()).isEqualTo(UPDATED_VISIBILITY);
        assertThat(testCustomSearch.getRequest()).isEqualTo(UPDATED_REQUEST);
        assertThat(testCustomSearch.getEntitySearching()).isEqualTo(UPDATED_ENTITY_SEARCHING);
        assertThat(testCustomSearch.getUserLogin()).isEqualTo(UPDATED_USER_LOGIN);
        assertThat(testCustomSearch.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingCustomSearch() throws Exception {
        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();
        customSearch.setId(count.incrementAndGet());

        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, customSearchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCustomSearch() throws Exception {
        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();
        customSearch.setId(count.incrementAndGet());

        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCustomSearch() throws Exception {
        int databaseSizeBeforeUpdate = customSearchRepository.findAll().size();
        customSearch.setId(count.incrementAndGet());

        // Create the CustomSearch
        CustomSearchDTO customSearchDTO = customSearchMapper.toDto(customSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCustomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(customSearchDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CustomSearch in the database
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCustomSearch() throws Exception {
        // Initialize the database
        customSearchRepository.saveAndFlush(customSearch);

        int databaseSizeBeforeDelete = customSearchRepository.findAll().size();

        // Delete the customSearch
        restCustomSearchMockMvc
            .perform(delete(ENTITY_API_URL_ID, customSearch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CustomSearch> customSearchList = customSearchRepository.findAll();
        assertThat(customSearchList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
