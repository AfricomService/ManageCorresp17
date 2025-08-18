package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static com.africom.correspmanage.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.Bordereaux;
import com.africom.correspmanage.repository.BordereauxRepository;
import com.africom.correspmanage.service.dto.BordereauxDTO;
import com.africom.correspmanage.service.mapper.BordereauxMapper;
import java.math.BigDecimal;
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
 * Integration tests for the {@link BordereauxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BordereauxResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_ISSUED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ISSUED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bordereaux";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BordereauxRepository bordereauxRepository;

    @Autowired
    private BordereauxMapper bordereauxMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBordereauxMockMvc;

    private Bordereaux bordereaux;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bordereaux createEntity(EntityManager em) {
        Bordereaux bordereaux = new Bordereaux()
            .reference(DEFAULT_REFERENCE)
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .status(DEFAULT_STATUS)
            .issuedDate(DEFAULT_ISSUED_DATE)
            .dueDate(DEFAULT_DUE_DATE)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedBy(DEFAULT_UPDATED_BY);
        return bordereaux;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bordereaux createUpdatedEntity(EntityManager em) {
        Bordereaux bordereaux = new Bordereaux()
            .reference(UPDATED_REFERENCE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .status(UPDATED_STATUS)
            .issuedDate(UPDATED_ISSUED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY);
        return bordereaux;
    }

    @BeforeEach
    public void initTest() {
        bordereaux = createEntity(em);
    }

    @Test
    @Transactional
    void createBordereaux() throws Exception {
        int databaseSizeBeforeCreate = bordereauxRepository.findAll().size();
        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);
        restBordereauxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isCreated());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeCreate + 1);
        Bordereaux testBordereaux = bordereauxList.get(bordereauxList.size() - 1);
        assertThat(testBordereaux.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testBordereaux.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBordereaux.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testBordereaux.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testBordereaux.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBordereaux.getIssuedDate()).isEqualTo(DEFAULT_ISSUED_DATE);
        assertThat(testBordereaux.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testBordereaux.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testBordereaux.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testBordereaux.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testBordereaux.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
    }

    @Test
    @Transactional
    void createBordereauxWithExistingId() throws Exception {
        // Create the Bordereaux with an existing ID
        bordereaux.setId(1L);
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        int databaseSizeBeforeCreate = bordereauxRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBordereauxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        int databaseSizeBeforeTest = bordereauxRepository.findAll().size();
        // set the field null
        bordereaux.setReference(null);

        // Create the Bordereaux, which fails.
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        restBordereauxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isBadRequest());

        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = bordereauxRepository.findAll().size();
        // set the field null
        bordereaux.setAmount(null);

        // Create the Bordereaux, which fails.
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        restBordereauxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isBadRequest());

        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = bordereauxRepository.findAll().size();
        // set the field null
        bordereaux.setStatus(null);

        // Create the Bordereaux, which fails.
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        restBordereauxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isBadRequest());

        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssuedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bordereauxRepository.findAll().size();
        // set the field null
        bordereaux.setIssuedDate(null);

        // Create the Bordereaux, which fails.
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        restBordereauxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isBadRequest());

        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBordereaux() throws Exception {
        // Initialize the database
        bordereauxRepository.saveAndFlush(bordereaux);

        // Get all the bordereauxList
        restBordereauxMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bordereaux.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].issuedDate").value(hasItem(sameInstant(DEFAULT_ISSUED_DATE))))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)));
    }

    @Test
    @Transactional
    void getBordereaux() throws Exception {
        // Initialize the database
        bordereauxRepository.saveAndFlush(bordereaux);

        // Get the bordereaux
        restBordereauxMockMvc
            .perform(get(ENTITY_API_URL_ID, bordereaux.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bordereaux.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.issuedDate").value(sameInstant(DEFAULT_ISSUED_DATE)))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingBordereaux() throws Exception {
        // Get the bordereaux
        restBordereauxMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBordereaux() throws Exception {
        // Initialize the database
        bordereauxRepository.saveAndFlush(bordereaux);

        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();

        // Update the bordereaux
        Bordereaux updatedBordereaux = bordereauxRepository.findById(bordereaux.getId()).get();
        // Disconnect from session so that the updates on updatedBordereaux are not directly saved in db
        em.detach(updatedBordereaux);
        updatedBordereaux
            .reference(UPDATED_REFERENCE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .status(UPDATED_STATUS)
            .issuedDate(UPDATED_ISSUED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY);
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(updatedBordereaux);

        restBordereauxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bordereauxDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bordereauxDTO))
            )
            .andExpect(status().isOk());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
        Bordereaux testBordereaux = bordereauxList.get(bordereauxList.size() - 1);
        assertThat(testBordereaux.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testBordereaux.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBordereaux.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testBordereaux.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testBordereaux.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBordereaux.getIssuedDate()).isEqualTo(UPDATED_ISSUED_DATE);
        assertThat(testBordereaux.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBordereaux.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBordereaux.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testBordereaux.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testBordereaux.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void putNonExistingBordereaux() throws Exception {
        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();
        bordereaux.setId(count.incrementAndGet());

        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBordereauxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bordereauxDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bordereauxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBordereaux() throws Exception {
        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();
        bordereaux.setId(count.incrementAndGet());

        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBordereauxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bordereauxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBordereaux() throws Exception {
        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();
        bordereaux.setId(count.incrementAndGet());

        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBordereauxMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bordereauxDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBordereauxWithPatch() throws Exception {
        // Initialize the database
        bordereauxRepository.saveAndFlush(bordereaux);

        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();

        // Update the bordereaux using partial update
        Bordereaux partialUpdatedBordereaux = new Bordereaux();
        partialUpdatedBordereaux.setId(bordereaux.getId());

        partialUpdatedBordereaux
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .issuedDate(UPDATED_ISSUED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE);

        restBordereauxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBordereaux.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBordereaux))
            )
            .andExpect(status().isOk());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
        Bordereaux testBordereaux = bordereauxList.get(bordereauxList.size() - 1);
        assertThat(testBordereaux.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testBordereaux.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBordereaux.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testBordereaux.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testBordereaux.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBordereaux.getIssuedDate()).isEqualTo(UPDATED_ISSUED_DATE);
        assertThat(testBordereaux.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBordereaux.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBordereaux.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testBordereaux.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testBordereaux.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
    }

    @Test
    @Transactional
    void fullUpdateBordereauxWithPatch() throws Exception {
        // Initialize the database
        bordereauxRepository.saveAndFlush(bordereaux);

        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();

        // Update the bordereaux using partial update
        Bordereaux partialUpdatedBordereaux = new Bordereaux();
        partialUpdatedBordereaux.setId(bordereaux.getId());

        partialUpdatedBordereaux
            .reference(UPDATED_REFERENCE)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .status(UPDATED_STATUS)
            .issuedDate(UPDATED_ISSUED_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY);

        restBordereauxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBordereaux.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBordereaux))
            )
            .andExpect(status().isOk());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
        Bordereaux testBordereaux = bordereauxList.get(bordereauxList.size() - 1);
        assertThat(testBordereaux.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testBordereaux.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBordereaux.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testBordereaux.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testBordereaux.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBordereaux.getIssuedDate()).isEqualTo(UPDATED_ISSUED_DATE);
        assertThat(testBordereaux.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testBordereaux.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testBordereaux.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testBordereaux.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testBordereaux.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingBordereaux() throws Exception {
        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();
        bordereaux.setId(count.incrementAndGet());

        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBordereauxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bordereauxDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bordereauxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBordereaux() throws Exception {
        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();
        bordereaux.setId(count.incrementAndGet());

        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBordereauxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bordereauxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBordereaux() throws Exception {
        int databaseSizeBeforeUpdate = bordereauxRepository.findAll().size();
        bordereaux.setId(count.incrementAndGet());

        // Create the Bordereaux
        BordereauxDTO bordereauxDTO = bordereauxMapper.toDto(bordereaux);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBordereauxMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bordereauxDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bordereaux in the database
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBordereaux() throws Exception {
        // Initialize the database
        bordereauxRepository.saveAndFlush(bordereaux);

        int databaseSizeBeforeDelete = bordereauxRepository.findAll().size();

        // Delete the bordereaux
        restBordereauxMockMvc
            .perform(delete(ENTITY_API_URL_ID, bordereaux.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bordereaux> bordereauxList = bordereauxRepository.findAll();
        assertThat(bordereauxList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
