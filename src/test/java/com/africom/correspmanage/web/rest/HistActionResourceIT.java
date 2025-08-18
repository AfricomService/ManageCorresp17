package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.HistAction;
import com.africom.correspmanage.repository.HistActionRepository;
import com.africom.correspmanage.service.dto.HistActionDTO;
import com.africom.correspmanage.service.mapper.HistActionMapper;
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
 * Integration tests for the {@link HistActionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HistActionResourceIT {

    private static final ZonedDateTime DEFAULT_DATE_TRANSACTION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_TRANSACTION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LOGIN_USER = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN_USER = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSACTION = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION = "BBBBBBBBBB";

    private static final String DEFAULT_ACTION_REF = "AAAAAAAAAA";
    private static final String UPDATED_ACTION_REF = "BBBBBBBBBB";

    private static final String DEFAULT_ACTION_JSON = "AAAAAAAAAA";
    private static final String UPDATED_ACTION_JSON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/hist-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistActionRepository histActionRepository;

    @Autowired
    private HistActionMapper histActionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistActionMockMvc;

    private HistAction histAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistAction createEntity(EntityManager em) {
        HistAction histAction = new HistAction()
            .dateTransaction(DEFAULT_DATE_TRANSACTION)
            .loginUser(DEFAULT_LOGIN_USER)
            .transaction(DEFAULT_TRANSACTION)
            .actionRef(DEFAULT_ACTION_REF)
            .actionJson(DEFAULT_ACTION_JSON);
        return histAction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistAction createUpdatedEntity(EntityManager em) {
        HistAction histAction = new HistAction()
            .dateTransaction(UPDATED_DATE_TRANSACTION)
            .loginUser(UPDATED_LOGIN_USER)
            .transaction(UPDATED_TRANSACTION)
            .actionRef(UPDATED_ACTION_REF)
            .actionJson(UPDATED_ACTION_JSON);
        return histAction;
    }

    @BeforeEach
    public void initTest() {
        histAction = createEntity(em);
    }

    @Test
    @Transactional
    void createHistAction() throws Exception {
        int databaseSizeBeforeCreate = histActionRepository.findAll().size();
        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);
        restHistActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(histActionDTO)))
            .andExpect(status().isCreated());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeCreate + 1);
        HistAction testHistAction = histActionList.get(histActionList.size() - 1);
        assertThat(testHistAction.getDateTransaction()).isEqualTo(DEFAULT_DATE_TRANSACTION);
        assertThat(testHistAction.getLoginUser()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testHistAction.getTransaction()).isEqualTo(DEFAULT_TRANSACTION);
        assertThat(testHistAction.getActionRef()).isEqualTo(DEFAULT_ACTION_REF);
        assertThat(testHistAction.getActionJson()).isEqualTo(DEFAULT_ACTION_JSON);
    }

    @Test
    @Transactional
    void createHistActionWithExistingId() throws Exception {
        // Create the HistAction with an existing ID
        histAction.setId(1L);
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        int databaseSizeBeforeCreate = histActionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(histActionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHistActions() throws Exception {
        // Initialize the database
        histActionRepository.saveAndFlush(histAction);

        // Get all the histActionList
        restHistActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(histAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateTransaction").value(hasItem(sameInstant(DEFAULT_DATE_TRANSACTION))))
            .andExpect(jsonPath("$.[*].loginUser").value(hasItem(DEFAULT_LOGIN_USER)))
            .andExpect(jsonPath("$.[*].transaction").value(hasItem(DEFAULT_TRANSACTION)))
            .andExpect(jsonPath("$.[*].actionRef").value(hasItem(DEFAULT_ACTION_REF)))
            .andExpect(jsonPath("$.[*].actionJson").value(hasItem(DEFAULT_ACTION_JSON)));
    }

    @Test
    @Transactional
    void getHistAction() throws Exception {
        // Initialize the database
        histActionRepository.saveAndFlush(histAction);

        // Get the histAction
        restHistActionMockMvc
            .perform(get(ENTITY_API_URL_ID, histAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(histAction.getId().intValue()))
            .andExpect(jsonPath("$.dateTransaction").value(sameInstant(DEFAULT_DATE_TRANSACTION)))
            .andExpect(jsonPath("$.loginUser").value(DEFAULT_LOGIN_USER))
            .andExpect(jsonPath("$.transaction").value(DEFAULT_TRANSACTION))
            .andExpect(jsonPath("$.actionRef").value(DEFAULT_ACTION_REF))
            .andExpect(jsonPath("$.actionJson").value(DEFAULT_ACTION_JSON));
    }

    @Test
    @Transactional
    void getNonExistingHistAction() throws Exception {
        // Get the histAction
        restHistActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHistAction() throws Exception {
        // Initialize the database
        histActionRepository.saveAndFlush(histAction);

        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();

        // Update the histAction
        HistAction updatedHistAction = histActionRepository.findById(histAction.getId()).get();
        // Disconnect from session so that the updates on updatedHistAction are not directly saved in db
        em.detach(updatedHistAction);
        updatedHistAction
            .dateTransaction(UPDATED_DATE_TRANSACTION)
            .loginUser(UPDATED_LOGIN_USER)
            .transaction(UPDATED_TRANSACTION)
            .actionRef(UPDATED_ACTION_REF)
            .actionJson(UPDATED_ACTION_JSON);
        HistActionDTO histActionDTO = histActionMapper.toDto(updatedHistAction);

        restHistActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, histActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(histActionDTO))
            )
            .andExpect(status().isOk());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
        HistAction testHistAction = histActionList.get(histActionList.size() - 1);
        assertThat(testHistAction.getDateTransaction()).isEqualTo(UPDATED_DATE_TRANSACTION);
        assertThat(testHistAction.getLoginUser()).isEqualTo(UPDATED_LOGIN_USER);
        assertThat(testHistAction.getTransaction()).isEqualTo(UPDATED_TRANSACTION);
        assertThat(testHistAction.getActionRef()).isEqualTo(UPDATED_ACTION_REF);
        assertThat(testHistAction.getActionJson()).isEqualTo(UPDATED_ACTION_JSON);
    }

    @Test
    @Transactional
    void putNonExistingHistAction() throws Exception {
        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();
        histAction.setId(count.incrementAndGet());

        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, histActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(histActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistAction() throws Exception {
        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();
        histAction.setId(count.incrementAndGet());

        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(histActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistAction() throws Exception {
        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();
        histAction.setId(count.incrementAndGet());

        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistActionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(histActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistActionWithPatch() throws Exception {
        // Initialize the database
        histActionRepository.saveAndFlush(histAction);

        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();

        // Update the histAction using partial update
        HistAction partialUpdatedHistAction = new HistAction();
        partialUpdatedHistAction.setId(histAction.getId());

        partialUpdatedHistAction.dateTransaction(UPDATED_DATE_TRANSACTION).actionRef(UPDATED_ACTION_REF).actionJson(UPDATED_ACTION_JSON);

        restHistActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistAction))
            )
            .andExpect(status().isOk());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
        HistAction testHistAction = histActionList.get(histActionList.size() - 1);
        assertThat(testHistAction.getDateTransaction()).isEqualTo(UPDATED_DATE_TRANSACTION);
        assertThat(testHistAction.getLoginUser()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testHistAction.getTransaction()).isEqualTo(DEFAULT_TRANSACTION);
        assertThat(testHistAction.getActionRef()).isEqualTo(UPDATED_ACTION_REF);
        assertThat(testHistAction.getActionJson()).isEqualTo(UPDATED_ACTION_JSON);
    }

    @Test
    @Transactional
    void fullUpdateHistActionWithPatch() throws Exception {
        // Initialize the database
        histActionRepository.saveAndFlush(histAction);

        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();

        // Update the histAction using partial update
        HistAction partialUpdatedHistAction = new HistAction();
        partialUpdatedHistAction.setId(histAction.getId());

        partialUpdatedHistAction
            .dateTransaction(UPDATED_DATE_TRANSACTION)
            .loginUser(UPDATED_LOGIN_USER)
            .transaction(UPDATED_TRANSACTION)
            .actionRef(UPDATED_ACTION_REF)
            .actionJson(UPDATED_ACTION_JSON);

        restHistActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistAction))
            )
            .andExpect(status().isOk());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
        HistAction testHistAction = histActionList.get(histActionList.size() - 1);
        assertThat(testHistAction.getDateTransaction()).isEqualTo(UPDATED_DATE_TRANSACTION);
        assertThat(testHistAction.getLoginUser()).isEqualTo(UPDATED_LOGIN_USER);
        assertThat(testHistAction.getTransaction()).isEqualTo(UPDATED_TRANSACTION);
        assertThat(testHistAction.getActionRef()).isEqualTo(UPDATED_ACTION_REF);
        assertThat(testHistAction.getActionJson()).isEqualTo(UPDATED_ACTION_JSON);
    }

    @Test
    @Transactional
    void patchNonExistingHistAction() throws Exception {
        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();
        histAction.setId(count.incrementAndGet());

        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, histActionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(histActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistAction() throws Exception {
        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();
        histAction.setId(count.incrementAndGet());

        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(histActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistAction() throws Exception {
        int databaseSizeBeforeUpdate = histActionRepository.findAll().size();
        histAction.setId(count.incrementAndGet());

        // Create the HistAction
        HistActionDTO histActionDTO = histActionMapper.toDto(histAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistActionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(histActionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistAction in the database
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistAction() throws Exception {
        // Initialize the database
        histActionRepository.saveAndFlush(histAction);

        int databaseSizeBeforeDelete = histActionRepository.findAll().size();

        // Delete the histAction
        restHistActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, histAction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HistAction> histActionList = histActionRepository.findAll();
        assertThat(histActionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
