package com.africom.correspmanage.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.SmtpConfig;
import com.africom.correspmanage.repository.SmtpConfigRepository;
import com.africom.correspmanage.service.dto.SmtpConfigDTO;
import com.africom.correspmanage.service.mapper.SmtpConfigMapper;
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
 * Integration tests for the {@link SmtpConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SmtpConfigResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SMTP_HOST = "AAAAAAAAAA";
    private static final String UPDATED_SMTP_HOST = "BBBBBBBBBB";

    private static final Integer DEFAULT_SMTP_PORT = 1;
    private static final Integer UPDATED_SMTP_PORT = 2;

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_USE_AUTH = false;
    private static final Boolean UPDATED_USE_AUTH = true;

    private static final Boolean DEFAULT_USE_TLS = false;
    private static final Boolean UPDATED_USE_TLS = true;

    private static final Boolean DEFAULT_USE_SSL = false;
    private static final Boolean UPDATED_USE_SSL = true;

    private static final String DEFAULT_FROM_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_FROM_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_FROM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FROM_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LACALHOST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LACALHOST_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_CONNECTION_TIMEOUT = 1;
    private static final Integer UPDATED_CONNECTION_TIMEOUT = 2;

    private static final Integer DEFAULT_TIMEOUT = 1;
    private static final Integer UPDATED_TIMEOUT = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/smtp-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SmtpConfigRepository smtpConfigRepository;

    @Autowired
    private SmtpConfigMapper smtpConfigMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSmtpConfigMockMvc;

    private SmtpConfig smtpConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SmtpConfig createEntity(EntityManager em) {
        SmtpConfig smtpConfig = new SmtpConfig()
            .name(DEFAULT_NAME)
            .smtpHost(DEFAULT_SMTP_HOST)
            .smtpPort(DEFAULT_SMTP_PORT)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .useAuth(DEFAULT_USE_AUTH)
            .useTls(DEFAULT_USE_TLS)
            .useSsl(DEFAULT_USE_SSL)
            .fromAddress(DEFAULT_FROM_ADDRESS)
            .fromName(DEFAULT_FROM_NAME)
            .lacalhostName(DEFAULT_LACALHOST_NAME)
            .connectionTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .timeout(DEFAULT_TIMEOUT)
            .active(DEFAULT_ACTIVE)
            .description(DEFAULT_DESCRIPTION);
        return smtpConfig;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SmtpConfig createUpdatedEntity(EntityManager em) {
        SmtpConfig smtpConfig = new SmtpConfig()
            .name(UPDATED_NAME)
            .smtpHost(UPDATED_SMTP_HOST)
            .smtpPort(UPDATED_SMTP_PORT)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .useAuth(UPDATED_USE_AUTH)
            .useTls(UPDATED_USE_TLS)
            .useSsl(UPDATED_USE_SSL)
            .fromAddress(UPDATED_FROM_ADDRESS)
            .fromName(UPDATED_FROM_NAME)
            .lacalhostName(UPDATED_LACALHOST_NAME)
            .connectionTimeout(UPDATED_CONNECTION_TIMEOUT)
            .timeout(UPDATED_TIMEOUT)
            .active(UPDATED_ACTIVE)
            .description(UPDATED_DESCRIPTION);
        return smtpConfig;
    }

    @BeforeEach
    public void initTest() {
        smtpConfig = createEntity(em);
    }

    @Test
    @Transactional
    void createSmtpConfig() throws Exception {
        int databaseSizeBeforeCreate = smtpConfigRepository.findAll().size();
        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);
        restSmtpConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO)))
            .andExpect(status().isCreated());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeCreate + 1);
        SmtpConfig testSmtpConfig = smtpConfigList.get(smtpConfigList.size() - 1);
        assertThat(testSmtpConfig.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSmtpConfig.getSmtpHost()).isEqualTo(DEFAULT_SMTP_HOST);
        assertThat(testSmtpConfig.getSmtpPort()).isEqualTo(DEFAULT_SMTP_PORT);
        assertThat(testSmtpConfig.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testSmtpConfig.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testSmtpConfig.getUseAuth()).isEqualTo(DEFAULT_USE_AUTH);
        assertThat(testSmtpConfig.getUseTls()).isEqualTo(DEFAULT_USE_TLS);
        assertThat(testSmtpConfig.getUseSsl()).isEqualTo(DEFAULT_USE_SSL);
        assertThat(testSmtpConfig.getFromAddress()).isEqualTo(DEFAULT_FROM_ADDRESS);
        assertThat(testSmtpConfig.getFromName()).isEqualTo(DEFAULT_FROM_NAME);
        assertThat(testSmtpConfig.getLacalhostName()).isEqualTo(DEFAULT_LACALHOST_NAME);
        assertThat(testSmtpConfig.getConnectionTimeout()).isEqualTo(DEFAULT_CONNECTION_TIMEOUT);
        assertThat(testSmtpConfig.getTimeout()).isEqualTo(DEFAULT_TIMEOUT);
        assertThat(testSmtpConfig.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testSmtpConfig.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSmtpConfigWithExistingId() throws Exception {
        // Create the SmtpConfig with an existing ID
        smtpConfig.setId(1L);
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        int databaseSizeBeforeCreate = smtpConfigRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSmtpConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSmtpConfigs() throws Exception {
        // Initialize the database
        smtpConfigRepository.saveAndFlush(smtpConfig);

        // Get all the smtpConfigList
        restSmtpConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(smtpConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].smtpHost").value(hasItem(DEFAULT_SMTP_HOST)))
            .andExpect(jsonPath("$.[*].smtpPort").value(hasItem(DEFAULT_SMTP_PORT)))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].useAuth").value(hasItem(DEFAULT_USE_AUTH.booleanValue())))
            .andExpect(jsonPath("$.[*].useTls").value(hasItem(DEFAULT_USE_TLS.booleanValue())))
            .andExpect(jsonPath("$.[*].useSsl").value(hasItem(DEFAULT_USE_SSL.booleanValue())))
            .andExpect(jsonPath("$.[*].fromAddress").value(hasItem(DEFAULT_FROM_ADDRESS)))
            .andExpect(jsonPath("$.[*].fromName").value(hasItem(DEFAULT_FROM_NAME)))
            .andExpect(jsonPath("$.[*].lacalhostName").value(hasItem(DEFAULT_LACALHOST_NAME)))
            .andExpect(jsonPath("$.[*].connectionTimeout").value(hasItem(DEFAULT_CONNECTION_TIMEOUT)))
            .andExpect(jsonPath("$.[*].timeout").value(hasItem(DEFAULT_TIMEOUT)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSmtpConfig() throws Exception {
        // Initialize the database
        smtpConfigRepository.saveAndFlush(smtpConfig);

        // Get the smtpConfig
        restSmtpConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, smtpConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(smtpConfig.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.smtpHost").value(DEFAULT_SMTP_HOST))
            .andExpect(jsonPath("$.smtpPort").value(DEFAULT_SMTP_PORT))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.useAuth").value(DEFAULT_USE_AUTH.booleanValue()))
            .andExpect(jsonPath("$.useTls").value(DEFAULT_USE_TLS.booleanValue()))
            .andExpect(jsonPath("$.useSsl").value(DEFAULT_USE_SSL.booleanValue()))
            .andExpect(jsonPath("$.fromAddress").value(DEFAULT_FROM_ADDRESS))
            .andExpect(jsonPath("$.fromName").value(DEFAULT_FROM_NAME))
            .andExpect(jsonPath("$.lacalhostName").value(DEFAULT_LACALHOST_NAME))
            .andExpect(jsonPath("$.connectionTimeout").value(DEFAULT_CONNECTION_TIMEOUT))
            .andExpect(jsonPath("$.timeout").value(DEFAULT_TIMEOUT))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingSmtpConfig() throws Exception {
        // Get the smtpConfig
        restSmtpConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSmtpConfig() throws Exception {
        // Initialize the database
        smtpConfigRepository.saveAndFlush(smtpConfig);

        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();

        // Update the smtpConfig
        SmtpConfig updatedSmtpConfig = smtpConfigRepository.findById(smtpConfig.getId()).get();
        // Disconnect from session so that the updates on updatedSmtpConfig are not directly saved in db
        em.detach(updatedSmtpConfig);
        updatedSmtpConfig
            .name(UPDATED_NAME)
            .smtpHost(UPDATED_SMTP_HOST)
            .smtpPort(UPDATED_SMTP_PORT)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .useAuth(UPDATED_USE_AUTH)
            .useTls(UPDATED_USE_TLS)
            .useSsl(UPDATED_USE_SSL)
            .fromAddress(UPDATED_FROM_ADDRESS)
            .fromName(UPDATED_FROM_NAME)
            .lacalhostName(UPDATED_LACALHOST_NAME)
            .connectionTimeout(UPDATED_CONNECTION_TIMEOUT)
            .timeout(UPDATED_TIMEOUT)
            .active(UPDATED_ACTIVE)
            .description(UPDATED_DESCRIPTION);
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(updatedSmtpConfig);

        restSmtpConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, smtpConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO))
            )
            .andExpect(status().isOk());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
        SmtpConfig testSmtpConfig = smtpConfigList.get(smtpConfigList.size() - 1);
        assertThat(testSmtpConfig.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSmtpConfig.getSmtpHost()).isEqualTo(UPDATED_SMTP_HOST);
        assertThat(testSmtpConfig.getSmtpPort()).isEqualTo(UPDATED_SMTP_PORT);
        assertThat(testSmtpConfig.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testSmtpConfig.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testSmtpConfig.getUseAuth()).isEqualTo(UPDATED_USE_AUTH);
        assertThat(testSmtpConfig.getUseTls()).isEqualTo(UPDATED_USE_TLS);
        assertThat(testSmtpConfig.getUseSsl()).isEqualTo(UPDATED_USE_SSL);
        assertThat(testSmtpConfig.getFromAddress()).isEqualTo(UPDATED_FROM_ADDRESS);
        assertThat(testSmtpConfig.getFromName()).isEqualTo(UPDATED_FROM_NAME);
        assertThat(testSmtpConfig.getLacalhostName()).isEqualTo(UPDATED_LACALHOST_NAME);
        assertThat(testSmtpConfig.getConnectionTimeout()).isEqualTo(UPDATED_CONNECTION_TIMEOUT);
        assertThat(testSmtpConfig.getTimeout()).isEqualTo(UPDATED_TIMEOUT);
        assertThat(testSmtpConfig.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSmtpConfig.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingSmtpConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();
        smtpConfig.setId(count.incrementAndGet());

        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmtpConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, smtpConfigDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSmtpConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();
        smtpConfig.setId(count.incrementAndGet());

        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmtpConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSmtpConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();
        smtpConfig.setId(count.incrementAndGet());

        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmtpConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSmtpConfigWithPatch() throws Exception {
        // Initialize the database
        smtpConfigRepository.saveAndFlush(smtpConfig);

        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();

        // Update the smtpConfig using partial update
        SmtpConfig partialUpdatedSmtpConfig = new SmtpConfig();
        partialUpdatedSmtpConfig.setId(smtpConfig.getId());

        partialUpdatedSmtpConfig
            .name(UPDATED_NAME)
            .smtpPort(UPDATED_SMTP_PORT)
            .useAuth(UPDATED_USE_AUTH)
            .useSsl(UPDATED_USE_SSL)
            .fromName(UPDATED_FROM_NAME)
            .connectionTimeout(UPDATED_CONNECTION_TIMEOUT)
            .timeout(UPDATED_TIMEOUT)
            .active(UPDATED_ACTIVE);

        restSmtpConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSmtpConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSmtpConfig))
            )
            .andExpect(status().isOk());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
        SmtpConfig testSmtpConfig = smtpConfigList.get(smtpConfigList.size() - 1);
        assertThat(testSmtpConfig.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSmtpConfig.getSmtpHost()).isEqualTo(DEFAULT_SMTP_HOST);
        assertThat(testSmtpConfig.getSmtpPort()).isEqualTo(UPDATED_SMTP_PORT);
        assertThat(testSmtpConfig.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testSmtpConfig.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testSmtpConfig.getUseAuth()).isEqualTo(UPDATED_USE_AUTH);
        assertThat(testSmtpConfig.getUseTls()).isEqualTo(DEFAULT_USE_TLS);
        assertThat(testSmtpConfig.getUseSsl()).isEqualTo(UPDATED_USE_SSL);
        assertThat(testSmtpConfig.getFromAddress()).isEqualTo(DEFAULT_FROM_ADDRESS);
        assertThat(testSmtpConfig.getFromName()).isEqualTo(UPDATED_FROM_NAME);
        assertThat(testSmtpConfig.getLacalhostName()).isEqualTo(DEFAULT_LACALHOST_NAME);
        assertThat(testSmtpConfig.getConnectionTimeout()).isEqualTo(UPDATED_CONNECTION_TIMEOUT);
        assertThat(testSmtpConfig.getTimeout()).isEqualTo(UPDATED_TIMEOUT);
        assertThat(testSmtpConfig.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSmtpConfig.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSmtpConfigWithPatch() throws Exception {
        // Initialize the database
        smtpConfigRepository.saveAndFlush(smtpConfig);

        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();

        // Update the smtpConfig using partial update
        SmtpConfig partialUpdatedSmtpConfig = new SmtpConfig();
        partialUpdatedSmtpConfig.setId(smtpConfig.getId());

        partialUpdatedSmtpConfig
            .name(UPDATED_NAME)
            .smtpHost(UPDATED_SMTP_HOST)
            .smtpPort(UPDATED_SMTP_PORT)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .useAuth(UPDATED_USE_AUTH)
            .useTls(UPDATED_USE_TLS)
            .useSsl(UPDATED_USE_SSL)
            .fromAddress(UPDATED_FROM_ADDRESS)
            .fromName(UPDATED_FROM_NAME)
            .lacalhostName(UPDATED_LACALHOST_NAME)
            .connectionTimeout(UPDATED_CONNECTION_TIMEOUT)
            .timeout(UPDATED_TIMEOUT)
            .active(UPDATED_ACTIVE)
            .description(UPDATED_DESCRIPTION);

        restSmtpConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSmtpConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSmtpConfig))
            )
            .andExpect(status().isOk());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
        SmtpConfig testSmtpConfig = smtpConfigList.get(smtpConfigList.size() - 1);
        assertThat(testSmtpConfig.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSmtpConfig.getSmtpHost()).isEqualTo(UPDATED_SMTP_HOST);
        assertThat(testSmtpConfig.getSmtpPort()).isEqualTo(UPDATED_SMTP_PORT);
        assertThat(testSmtpConfig.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testSmtpConfig.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testSmtpConfig.getUseAuth()).isEqualTo(UPDATED_USE_AUTH);
        assertThat(testSmtpConfig.getUseTls()).isEqualTo(UPDATED_USE_TLS);
        assertThat(testSmtpConfig.getUseSsl()).isEqualTo(UPDATED_USE_SSL);
        assertThat(testSmtpConfig.getFromAddress()).isEqualTo(UPDATED_FROM_ADDRESS);
        assertThat(testSmtpConfig.getFromName()).isEqualTo(UPDATED_FROM_NAME);
        assertThat(testSmtpConfig.getLacalhostName()).isEqualTo(UPDATED_LACALHOST_NAME);
        assertThat(testSmtpConfig.getConnectionTimeout()).isEqualTo(UPDATED_CONNECTION_TIMEOUT);
        assertThat(testSmtpConfig.getTimeout()).isEqualTo(UPDATED_TIMEOUT);
        assertThat(testSmtpConfig.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testSmtpConfig.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSmtpConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();
        smtpConfig.setId(count.incrementAndGet());

        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSmtpConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, smtpConfigDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSmtpConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();
        smtpConfig.setId(count.incrementAndGet());

        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmtpConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSmtpConfig() throws Exception {
        int databaseSizeBeforeUpdate = smtpConfigRepository.findAll().size();
        smtpConfig.setId(count.incrementAndGet());

        // Create the SmtpConfig
        SmtpConfigDTO smtpConfigDTO = smtpConfigMapper.toDto(smtpConfig);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSmtpConfigMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(smtpConfigDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SmtpConfig in the database
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSmtpConfig() throws Exception {
        // Initialize the database
        smtpConfigRepository.saveAndFlush(smtpConfig);

        int databaseSizeBeforeDelete = smtpConfigRepository.findAll().size();

        // Delete the smtpConfig
        restSmtpConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, smtpConfig.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SmtpConfig> smtpConfigList = smtpConfigRepository.findAll();
        assertThat(smtpConfigList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
