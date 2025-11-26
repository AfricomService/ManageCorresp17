package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import com.africom.correspmanage.domain.enumeration.DispatchType;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.service.CorrespService;
import com.africom.correspmanage.service.dto.CorrespDTO;
import com.africom.correspmanage.service.mapper.CorrespMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link CorrespResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CorrespResourceIT {

    private static final UUID DEFAULT_IDINSTANCE = UUID.randomUUID();
    private static final UUID UPDATED_IDINSTANCE = UUID.randomUUID();

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_COURRIER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_COURRIER_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final CorrespStatus DEFAULT_STATUS = CorrespStatus.DRAFT;
    private static final CorrespStatus UPDATED_STATUS = CorrespStatus.IN_PROGRESS;

    private static final Boolean DEFAULT_PRIORITY = false;
    private static final Boolean UPDATED_PRIORITY = true;

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final CorrespType DEFAULT_CORRESPTYPE = CorrespType.INCOMING;
    private static final CorrespType UPDATED_CORRESPTYPE = CorrespType.OUTGOING;

    private static final String DEFAULT_SENDER = "AAAAAAAAAA";
    private static final String UPDATED_SENDER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_NEEDS_RESPONSE = false;
    private static final Boolean UPDATED_NEEDS_RESPONSE = true;

    private static final String DEFAULT_RESPONSE_TO = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_TO = "BBBBBBBBBB";

    private static final String DEFAULT_STEP = "AAAAAAAAAA";
    private static final String UPDATED_STEP = "BBBBBBBBBB";

    private static final String DEFAULT_STEP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STEP_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_PROCESS_ID = 1L;
    private static final Long UPDATED_PROCESS_ID = 2L;

    private static final String DEFAULT_PROCESS_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_PROCESS_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_REFERENCE = "BBBBBBBBBB";

    private static final DispatchType DEFAULT_DISPATCH_TYPE = DispatchType.LETTER;
    private static final DispatchType UPDATED_DISPATCH_TYPE = DispatchType.REGISTERED_LETTER;

    private static final String DEFAULT_CONTACT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_PERSON_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_PERSON_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_EMAIL_CC = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_EMAIL_CC = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Long DEFAULT_TYPE_DOCUMENT_ID = 1L;
    private static final Long UPDATED_TYPE_DOCUMENT_ID = 2L;

    private static final String DEFAULT_LABEL_DESTINATAIRE = "AAAAAAAAAA";
    private static final String UPDATED_LABEL_DESTINATAIRE = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL_CONTACT_CC = "AAAAAAAAAA";
    private static final String UPDATED_LABEL_CONTACT_CC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/corresps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CorrespRepository correspRepository;

    @Mock
    private CorrespRepository correspRepositoryMock;

    @Autowired
    private CorrespMapper correspMapper;

    @Mock
    private CorrespService correspServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCorrespMockMvc;

    private Corresp corresp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Corresp createEntity(EntityManager em) {
        Corresp corresp = new Corresp()
            .idinstance(DEFAULT_IDINSTANCE)
            .reference(DEFAULT_REFERENCE)
            .subject(DEFAULT_SUBJECT)
            .dateCreated(DEFAULT_DATE_CREATED)
            .courrierDate(DEFAULT_COURRIER_DATE)
            .status(DEFAULT_STATUS)
            .priority(DEFAULT_PRIORITY)
            .body(DEFAULT_BODY)
            .corresptype(DEFAULT_CORRESPTYPE)
            .sender(DEFAULT_SENDER)
            .needsResponse(DEFAULT_NEEDS_RESPONSE)
            .responseTo(DEFAULT_RESPONSE_TO)
            .step(DEFAULT_STEP)
            .stepCode(DEFAULT_STEP_CODE)
            .processId(DEFAULT_PROCESS_ID)
            .processVersion(DEFAULT_PROCESS_VERSION)
            .contact(DEFAULT_CONTACT)
            .senderReference(DEFAULT_SENDER_REFERENCE)
            .dispatchType(DEFAULT_DISPATCH_TYPE)
            .contactCompany(DEFAULT_CONTACT_COMPANY)
            .personCompany(DEFAULT_PERSON_COMPANY)
            .contactAddress(DEFAULT_CONTACT_ADDRESS)
            .contactEmail(DEFAULT_CONTACT_EMAIL)
            .contactEmailCc(DEFAULT_CONTACT_EMAIL_CC)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .typeDocumentId(DEFAULT_TYPE_DOCUMENT_ID)
            .labelDestinataire(DEFAULT_LABEL_DESTINATAIRE)
            .labelContactCC(DEFAULT_LABEL_CONTACT_CC);
        return corresp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Corresp createUpdatedEntity(EntityManager em) {
        Corresp corresp = new Corresp()
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .courrierDate(UPDATED_COURRIER_DATE)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .body(UPDATED_BODY)
            .corresptype(UPDATED_CORRESPTYPE)
            .sender(UPDATED_SENDER)
            .needsResponse(UPDATED_NEEDS_RESPONSE)
            .responseTo(UPDATED_RESPONSE_TO)
            .step(UPDATED_STEP)
            .stepCode(UPDATED_STEP_CODE)
            .processId(UPDATED_PROCESS_ID)
            .processVersion(UPDATED_PROCESS_VERSION)
            .contact(UPDATED_CONTACT)
            .senderReference(UPDATED_SENDER_REFERENCE)
            .dispatchType(UPDATED_DISPATCH_TYPE)
            .contactCompany(UPDATED_CONTACT_COMPANY)
            .personCompany(UPDATED_PERSON_COMPANY)
            .contactAddress(UPDATED_CONTACT_ADDRESS)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactEmailCc(UPDATED_CONTACT_EMAIL_CC)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE)
            .typeDocumentId(UPDATED_TYPE_DOCUMENT_ID)
            .labelDestinataire(UPDATED_LABEL_DESTINATAIRE)
            .labelContactCC(UPDATED_LABEL_CONTACT_CC);
        return corresp;
    }

    @BeforeEach
    public void initTest() {
        corresp = createEntity(em);
    }

    @Test
    @Transactional
    void createCorresp() throws Exception {
        int databaseSizeBeforeCreate = correspRepository.findAll().size();
        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);
        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isCreated());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeCreate + 1);
        Corresp testCorresp = correspList.get(correspList.size() - 1);
        assertThat(testCorresp.getIdinstance()).isEqualTo(DEFAULT_IDINSTANCE);
        assertThat(testCorresp.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testCorresp.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testCorresp.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
        assertThat(testCorresp.getCourrierDate()).isEqualTo(DEFAULT_COURRIER_DATE);
        assertThat(testCorresp.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCorresp.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(testCorresp.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testCorresp.getCorresptype()).isEqualTo(DEFAULT_CORRESPTYPE);
        assertThat(testCorresp.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testCorresp.getNeedsResponse()).isEqualTo(DEFAULT_NEEDS_RESPONSE);
        assertThat(testCorresp.getResponseTo()).isEqualTo(DEFAULT_RESPONSE_TO);
        assertThat(testCorresp.getStep()).isEqualTo(DEFAULT_STEP);
        assertThat(testCorresp.getStepCode()).isEqualTo(DEFAULT_STEP_CODE);
        assertThat(testCorresp.getProcessId()).isEqualTo(DEFAULT_PROCESS_ID);
        assertThat(testCorresp.getProcessVersion()).isEqualTo(DEFAULT_PROCESS_VERSION);
        assertThat(testCorresp.getContact()).isEqualTo(DEFAULT_CONTACT);
        assertThat(testCorresp.getSenderReference()).isEqualTo(DEFAULT_SENDER_REFERENCE);
        assertThat(testCorresp.getDispatchType()).isEqualTo(DEFAULT_DISPATCH_TYPE);
        assertThat(testCorresp.getContactCompany()).isEqualTo(DEFAULT_CONTACT_COMPANY);
        assertThat(testCorresp.getPersonCompany()).isEqualTo(DEFAULT_PERSON_COMPANY);
        assertThat(testCorresp.getContactAddress()).isEqualTo(DEFAULT_CONTACT_ADDRESS);
        assertThat(testCorresp.getContactEmail()).isEqualTo(DEFAULT_CONTACT_EMAIL);
        assertThat(testCorresp.getContactEmailCc()).isEqualTo(DEFAULT_CONTACT_EMAIL_CC);
        assertThat(testCorresp.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCorresp.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testCorresp.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testCorresp.getTypeDocumentId()).isEqualTo(DEFAULT_TYPE_DOCUMENT_ID);
        assertThat(testCorresp.getLabelDestinataire()).isEqualTo(DEFAULT_LABEL_DESTINATAIRE);
        assertThat(testCorresp.getLabelContactCC()).isEqualTo(DEFAULT_LABEL_CONTACT_CC);
    }

    @Test
    @Transactional
    void createCorrespWithExistingId() throws Exception {
        // Create the Corresp with an existing ID
        corresp.setId(1L);
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        int databaseSizeBeforeCreate = correspRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIdinstanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setIdinstance(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setSubject(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setDateCreated(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCourrierDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setCourrierDate(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setStatus(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCorresptypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setCorresptype(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setSender(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDispatchTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = correspRepository.findAll().size();
        // set the field null
        corresp.setDispatchType(null);

        // Create the Corresp, which fails.
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        restCorrespMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isBadRequest());

        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCorresps() throws Exception {
        // Initialize the database
        correspRepository.saveAndFlush(corresp);

        // Get all the correspList
        restCorrespMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(corresp.getId().intValue())))
            .andExpect(jsonPath("$.[*].idinstance").value(hasItem(DEFAULT_IDINSTANCE.toString())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(sameInstant(DEFAULT_DATE_CREATED))))
            .andExpect(jsonPath("$.[*].courrierDate").value(hasItem(sameInstant(DEFAULT_COURRIER_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.booleanValue())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].corresptype").value(hasItem(DEFAULT_CORRESPTYPE.toString())))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
            .andExpect(jsonPath("$.[*].needsResponse").value(hasItem(DEFAULT_NEEDS_RESPONSE.booleanValue())))
            .andExpect(jsonPath("$.[*].responseTo").value(hasItem(DEFAULT_RESPONSE_TO)))
            .andExpect(jsonPath("$.[*].step").value(hasItem(DEFAULT_STEP)))
            .andExpect(jsonPath("$.[*].stepCode").value(hasItem(DEFAULT_STEP_CODE)))
            .andExpect(jsonPath("$.[*].processId").value(hasItem(DEFAULT_PROCESS_ID.intValue())))
            .andExpect(jsonPath("$.[*].processVersion").value(hasItem(DEFAULT_PROCESS_VERSION)))
            .andExpect(jsonPath("$.[*].contact").value(hasItem(DEFAULT_CONTACT)))
            .andExpect(jsonPath("$.[*].senderReference").value(hasItem(DEFAULT_SENDER_REFERENCE)))
            .andExpect(jsonPath("$.[*].dispatchType").value(hasItem(DEFAULT_DISPATCH_TYPE.toString())))
            .andExpect(jsonPath("$.[*].contactCompany").value(hasItem(DEFAULT_CONTACT_COMPANY)))
            .andExpect(jsonPath("$.[*].personCompany").value(hasItem(DEFAULT_PERSON_COMPANY)))
            .andExpect(jsonPath("$.[*].contactAddress").value(hasItem(DEFAULT_CONTACT_ADDRESS)))
            .andExpect(jsonPath("$.[*].contactEmail").value(hasItem(DEFAULT_CONTACT_EMAIL)))
            .andExpect(jsonPath("$.[*].contactEmailCc").value(hasItem(DEFAULT_CONTACT_EMAIL_CC)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))))
            .andExpect(jsonPath("$.[*].typeDocumentId").value(hasItem(DEFAULT_TYPE_DOCUMENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].labelDestinataire").value(hasItem(DEFAULT_LABEL_DESTINATAIRE)))
            .andExpect(jsonPath("$.[*].labelContactCC").value(hasItem(DEFAULT_LABEL_CONTACT_CC)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCorrespsWithEagerRelationshipsIsEnabled() throws Exception {
        when(correspServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCorrespMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(correspServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCorrespsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(correspServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCorrespMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(correspRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCorresp() throws Exception {
        // Initialize the database
        correspRepository.saveAndFlush(corresp);

        // Get the corresp
        restCorrespMockMvc
            .perform(get(ENTITY_API_URL_ID, corresp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(corresp.getId().intValue()))
            .andExpect(jsonPath("$.idinstance").value(DEFAULT_IDINSTANCE.toString()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.dateCreated").value(sameInstant(DEFAULT_DATE_CREATED)))
            .andExpect(jsonPath("$.courrierDate").value(sameInstant(DEFAULT_COURRIER_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.booleanValue()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.corresptype").value(DEFAULT_CORRESPTYPE.toString()))
            .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER))
            .andExpect(jsonPath("$.needsResponse").value(DEFAULT_NEEDS_RESPONSE.booleanValue()))
            .andExpect(jsonPath("$.responseTo").value(DEFAULT_RESPONSE_TO))
            .andExpect(jsonPath("$.step").value(DEFAULT_STEP))
            .andExpect(jsonPath("$.stepCode").value(DEFAULT_STEP_CODE))
            .andExpect(jsonPath("$.processId").value(DEFAULT_PROCESS_ID.intValue()))
            .andExpect(jsonPath("$.processVersion").value(DEFAULT_PROCESS_VERSION))
            .andExpect(jsonPath("$.contact").value(DEFAULT_CONTACT))
            .andExpect(jsonPath("$.senderReference").value(DEFAULT_SENDER_REFERENCE))
            .andExpect(jsonPath("$.dispatchType").value(DEFAULT_DISPATCH_TYPE.toString()))
            .andExpect(jsonPath("$.contactCompany").value(DEFAULT_CONTACT_COMPANY))
            .andExpect(jsonPath("$.personCompany").value(DEFAULT_PERSON_COMPANY))
            .andExpect(jsonPath("$.contactAddress").value(DEFAULT_CONTACT_ADDRESS))
            .andExpect(jsonPath("$.contactEmail").value(DEFAULT_CONTACT_EMAIL))
            .andExpect(jsonPath("$.contactEmailCc").value(DEFAULT_CONTACT_EMAIL_CC))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)))
            .andExpect(jsonPath("$.typeDocumentId").value(DEFAULT_TYPE_DOCUMENT_ID.intValue()))
            .andExpect(jsonPath("$.labelDestinataire").value(DEFAULT_LABEL_DESTINATAIRE))
            .andExpect(jsonPath("$.labelContactCC").value(DEFAULT_LABEL_CONTACT_CC));
    }

    @Test
    @Transactional
    void getNonExistingCorresp() throws Exception {
        // Get the corresp
        restCorrespMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCorresp() throws Exception {
        // Initialize the database
        correspRepository.saveAndFlush(corresp);

        int databaseSizeBeforeUpdate = correspRepository.findAll().size();

        // Update the corresp
        Corresp updatedCorresp = correspRepository.findById(corresp.getId()).get();
        // Disconnect from session so that the updates on updatedCorresp are not directly saved in db
        em.detach(updatedCorresp);
        updatedCorresp
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .courrierDate(UPDATED_COURRIER_DATE)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .body(UPDATED_BODY)
            .corresptype(UPDATED_CORRESPTYPE)
            .sender(UPDATED_SENDER)
            .needsResponse(UPDATED_NEEDS_RESPONSE)
            .responseTo(UPDATED_RESPONSE_TO)
            .step(UPDATED_STEP)
            .stepCode(UPDATED_STEP_CODE)
            .processId(UPDATED_PROCESS_ID)
            .processVersion(UPDATED_PROCESS_VERSION)
            .contact(UPDATED_CONTACT)
            .senderReference(UPDATED_SENDER_REFERENCE)
            .dispatchType(UPDATED_DISPATCH_TYPE)
            .contactCompany(UPDATED_CONTACT_COMPANY)
            .personCompany(UPDATED_PERSON_COMPANY)
            .contactAddress(UPDATED_CONTACT_ADDRESS)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactEmailCc(UPDATED_CONTACT_EMAIL_CC)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE)
            .typeDocumentId(UPDATED_TYPE_DOCUMENT_ID)
            .labelDestinataire(UPDATED_LABEL_DESTINATAIRE)
            .labelContactCC(UPDATED_LABEL_CONTACT_CC);
        CorrespDTO correspDTO = correspMapper.toDto(updatedCorresp);

        restCorrespMockMvc
            .perform(
                put(ENTITY_API_URL_ID, correspDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(correspDTO))
            )
            .andExpect(status().isOk());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
        Corresp testCorresp = correspList.get(correspList.size() - 1);
        assertThat(testCorresp.getIdinstance()).isEqualTo(UPDATED_IDINSTANCE);
        assertThat(testCorresp.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testCorresp.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testCorresp.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
        assertThat(testCorresp.getCourrierDate()).isEqualTo(UPDATED_COURRIER_DATE);
        assertThat(testCorresp.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCorresp.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCorresp.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testCorresp.getCorresptype()).isEqualTo(UPDATED_CORRESPTYPE);
        assertThat(testCorresp.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testCorresp.getNeedsResponse()).isEqualTo(UPDATED_NEEDS_RESPONSE);
        assertThat(testCorresp.getResponseTo()).isEqualTo(UPDATED_RESPONSE_TO);
        assertThat(testCorresp.getStep()).isEqualTo(UPDATED_STEP);
        assertThat(testCorresp.getStepCode()).isEqualTo(UPDATED_STEP_CODE);
        assertThat(testCorresp.getProcessId()).isEqualTo(UPDATED_PROCESS_ID);
        assertThat(testCorresp.getProcessVersion()).isEqualTo(UPDATED_PROCESS_VERSION);
        assertThat(testCorresp.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testCorresp.getSenderReference()).isEqualTo(UPDATED_SENDER_REFERENCE);
        assertThat(testCorresp.getDispatchType()).isEqualTo(UPDATED_DISPATCH_TYPE);
        assertThat(testCorresp.getContactCompany()).isEqualTo(UPDATED_CONTACT_COMPANY);
        assertThat(testCorresp.getPersonCompany()).isEqualTo(UPDATED_PERSON_COMPANY);
        assertThat(testCorresp.getContactAddress()).isEqualTo(UPDATED_CONTACT_ADDRESS);
        assertThat(testCorresp.getContactEmail()).isEqualTo(UPDATED_CONTACT_EMAIL);
        assertThat(testCorresp.getContactEmailCc()).isEqualTo(UPDATED_CONTACT_EMAIL_CC);
        assertThat(testCorresp.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCorresp.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testCorresp.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCorresp.getTypeDocumentId()).isEqualTo(UPDATED_TYPE_DOCUMENT_ID);
        assertThat(testCorresp.getLabelDestinataire()).isEqualTo(UPDATED_LABEL_DESTINATAIRE);
        assertThat(testCorresp.getLabelContactCC()).isEqualTo(UPDATED_LABEL_CONTACT_CC);
    }

    @Test
    @Transactional
    void putNonExistingCorresp() throws Exception {
        int databaseSizeBeforeUpdate = correspRepository.findAll().size();
        corresp.setId(count.incrementAndGet());

        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrespMockMvc
            .perform(
                put(ENTITY_API_URL_ID, correspDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(correspDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCorresp() throws Exception {
        int databaseSizeBeforeUpdate = correspRepository.findAll().size();
        corresp.setId(count.incrementAndGet());

        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrespMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(correspDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCorresp() throws Exception {
        int databaseSizeBeforeUpdate = correspRepository.findAll().size();
        corresp.setId(count.incrementAndGet());

        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrespMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(correspDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCorrespWithPatch() throws Exception {
        // Initialize the database
        correspRepository.saveAndFlush(corresp);

        int databaseSizeBeforeUpdate = correspRepository.findAll().size();

        // Update the corresp using partial update
        Corresp partialUpdatedCorresp = new Corresp();
        partialUpdatedCorresp.setId(corresp.getId());

        partialUpdatedCorresp
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .corresptype(UPDATED_CORRESPTYPE)
            .responseTo(UPDATED_RESPONSE_TO)
            .processVersion(UPDATED_PROCESS_VERSION)
            .senderReference(UPDATED_SENDER_REFERENCE)
            .personCompany(UPDATED_PERSON_COMPANY)
            .contactAddress(UPDATED_CONTACT_ADDRESS)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactEmailCc(UPDATED_CONTACT_EMAIL_CC)
            .updatedDate(UPDATED_UPDATED_DATE)
            .typeDocumentId(UPDATED_TYPE_DOCUMENT_ID)
            .labelDestinataire(UPDATED_LABEL_DESTINATAIRE);

        restCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorresp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCorresp))
            )
            .andExpect(status().isOk());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
        Corresp testCorresp = correspList.get(correspList.size() - 1);
        assertThat(testCorresp.getIdinstance()).isEqualTo(UPDATED_IDINSTANCE);
        assertThat(testCorresp.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testCorresp.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testCorresp.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
        assertThat(testCorresp.getCourrierDate()).isEqualTo(DEFAULT_COURRIER_DATE);
        assertThat(testCorresp.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCorresp.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCorresp.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testCorresp.getCorresptype()).isEqualTo(UPDATED_CORRESPTYPE);
        assertThat(testCorresp.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testCorresp.getNeedsResponse()).isEqualTo(DEFAULT_NEEDS_RESPONSE);
        assertThat(testCorresp.getResponseTo()).isEqualTo(UPDATED_RESPONSE_TO);
        assertThat(testCorresp.getStep()).isEqualTo(DEFAULT_STEP);
        assertThat(testCorresp.getStepCode()).isEqualTo(DEFAULT_STEP_CODE);
        assertThat(testCorresp.getProcessId()).isEqualTo(DEFAULT_PROCESS_ID);
        assertThat(testCorresp.getProcessVersion()).isEqualTo(UPDATED_PROCESS_VERSION);
        assertThat(testCorresp.getContact()).isEqualTo(DEFAULT_CONTACT);
        assertThat(testCorresp.getSenderReference()).isEqualTo(UPDATED_SENDER_REFERENCE);
        assertThat(testCorresp.getDispatchType()).isEqualTo(DEFAULT_DISPATCH_TYPE);
        assertThat(testCorresp.getContactCompany()).isEqualTo(DEFAULT_CONTACT_COMPANY);
        assertThat(testCorresp.getPersonCompany()).isEqualTo(UPDATED_PERSON_COMPANY);
        assertThat(testCorresp.getContactAddress()).isEqualTo(UPDATED_CONTACT_ADDRESS);
        assertThat(testCorresp.getContactEmail()).isEqualTo(UPDATED_CONTACT_EMAIL);
        assertThat(testCorresp.getContactEmailCc()).isEqualTo(UPDATED_CONTACT_EMAIL_CC);
        assertThat(testCorresp.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCorresp.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testCorresp.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCorresp.getTypeDocumentId()).isEqualTo(UPDATED_TYPE_DOCUMENT_ID);
        assertThat(testCorresp.getLabelDestinataire()).isEqualTo(UPDATED_LABEL_DESTINATAIRE);
        assertThat(testCorresp.getLabelContactCC()).isEqualTo(DEFAULT_LABEL_CONTACT_CC);
    }

    @Test
    @Transactional
    void fullUpdateCorrespWithPatch() throws Exception {
        // Initialize the database
        correspRepository.saveAndFlush(corresp);

        int databaseSizeBeforeUpdate = correspRepository.findAll().size();

        // Update the corresp using partial update
        Corresp partialUpdatedCorresp = new Corresp();
        partialUpdatedCorresp.setId(corresp.getId());

        partialUpdatedCorresp
            .idinstance(UPDATED_IDINSTANCE)
            .reference(UPDATED_REFERENCE)
            .subject(UPDATED_SUBJECT)
            .dateCreated(UPDATED_DATE_CREATED)
            .courrierDate(UPDATED_COURRIER_DATE)
            .status(UPDATED_STATUS)
            .priority(UPDATED_PRIORITY)
            .body(UPDATED_BODY)
            .corresptype(UPDATED_CORRESPTYPE)
            .sender(UPDATED_SENDER)
            .needsResponse(UPDATED_NEEDS_RESPONSE)
            .responseTo(UPDATED_RESPONSE_TO)
            .step(UPDATED_STEP)
            .stepCode(UPDATED_STEP_CODE)
            .processId(UPDATED_PROCESS_ID)
            .processVersion(UPDATED_PROCESS_VERSION)
            .contact(UPDATED_CONTACT)
            .senderReference(UPDATED_SENDER_REFERENCE)
            .dispatchType(UPDATED_DISPATCH_TYPE)
            .contactCompany(UPDATED_CONTACT_COMPANY)
            .personCompany(UPDATED_PERSON_COMPANY)
            .contactAddress(UPDATED_CONTACT_ADDRESS)
            .contactEmail(UPDATED_CONTACT_EMAIL)
            .contactEmailCc(UPDATED_CONTACT_EMAIL_CC)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE)
            .typeDocumentId(UPDATED_TYPE_DOCUMENT_ID)
            .labelDestinataire(UPDATED_LABEL_DESTINATAIRE)
            .labelContactCC(UPDATED_LABEL_CONTACT_CC);

        restCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorresp.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCorresp))
            )
            .andExpect(status().isOk());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
        Corresp testCorresp = correspList.get(correspList.size() - 1);
        assertThat(testCorresp.getIdinstance()).isEqualTo(UPDATED_IDINSTANCE);
        assertThat(testCorresp.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testCorresp.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testCorresp.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
        assertThat(testCorresp.getCourrierDate()).isEqualTo(UPDATED_COURRIER_DATE);
        assertThat(testCorresp.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCorresp.getPriority()).isEqualTo(UPDATED_PRIORITY);
        assertThat(testCorresp.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testCorresp.getCorresptype()).isEqualTo(UPDATED_CORRESPTYPE);
        assertThat(testCorresp.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testCorresp.getNeedsResponse()).isEqualTo(UPDATED_NEEDS_RESPONSE);
        assertThat(testCorresp.getResponseTo()).isEqualTo(UPDATED_RESPONSE_TO);
        assertThat(testCorresp.getStep()).isEqualTo(UPDATED_STEP);
        assertThat(testCorresp.getStepCode()).isEqualTo(UPDATED_STEP_CODE);
        assertThat(testCorresp.getProcessId()).isEqualTo(UPDATED_PROCESS_ID);
        assertThat(testCorresp.getProcessVersion()).isEqualTo(UPDATED_PROCESS_VERSION);
        assertThat(testCorresp.getContact()).isEqualTo(UPDATED_CONTACT);
        assertThat(testCorresp.getSenderReference()).isEqualTo(UPDATED_SENDER_REFERENCE);
        assertThat(testCorresp.getDispatchType()).isEqualTo(UPDATED_DISPATCH_TYPE);
        assertThat(testCorresp.getContactCompany()).isEqualTo(UPDATED_CONTACT_COMPANY);
        assertThat(testCorresp.getPersonCompany()).isEqualTo(UPDATED_PERSON_COMPANY);
        assertThat(testCorresp.getContactAddress()).isEqualTo(UPDATED_CONTACT_ADDRESS);
        assertThat(testCorresp.getContactEmail()).isEqualTo(UPDATED_CONTACT_EMAIL);
        assertThat(testCorresp.getContactEmailCc()).isEqualTo(UPDATED_CONTACT_EMAIL_CC);
        assertThat(testCorresp.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCorresp.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testCorresp.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testCorresp.getTypeDocumentId()).isEqualTo(UPDATED_TYPE_DOCUMENT_ID);
        assertThat(testCorresp.getLabelDestinataire()).isEqualTo(UPDATED_LABEL_DESTINATAIRE);
        assertThat(testCorresp.getLabelContactCC()).isEqualTo(UPDATED_LABEL_CONTACT_CC);
    }

    @Test
    @Transactional
    void patchNonExistingCorresp() throws Exception {
        int databaseSizeBeforeUpdate = correspRepository.findAll().size();
        corresp.setId(count.incrementAndGet());

        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, correspDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(correspDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCorresp() throws Exception {
        int databaseSizeBeforeUpdate = correspRepository.findAll().size();
        corresp.setId(count.incrementAndGet());

        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(correspDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCorresp() throws Exception {
        int databaseSizeBeforeUpdate = correspRepository.findAll().size();
        corresp.setId(count.incrementAndGet());

        // Create the Corresp
        CorrespDTO correspDTO = correspMapper.toDto(corresp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrespMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(correspDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Corresp in the database
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCorresp() throws Exception {
        // Initialize the database
        correspRepository.saveAndFlush(corresp);

        int databaseSizeBeforeDelete = correspRepository.findAll().size();

        // Delete the corresp
        restCorrespMockMvc
            .perform(delete(ENTITY_API_URL_ID, corresp.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Corresp> correspList = correspRepository.findAll();
        assertThat(correspList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
