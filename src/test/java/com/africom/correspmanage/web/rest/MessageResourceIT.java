//package com.africom.correspmanage.web.rest;
//
//import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.africom.correspmanage.IntegrationTest;
//import com.africom.correspmanage.domain.Message;
//import com.africom.correspmanage.repository.MessageRepository;
//import com.africom.correspmanage.service.dto.MessageDTO;
//import com.africom.correspmanage.service.mapper.MessageMapper;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicLong;
//import javax.persistence.EntityManager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * Integration tests for the {@link MessageResource} REST controller.
// */
//@IntegrationTest
//@AutoConfigureMockMvc
//@WithMockUser
//class MessageResourceIT {
//
//    private static final String DEFAULT_SENDER = "AAAAAAAAAA";
//    private static final String UPDATED_SENDER = "BBBBBBBBBB";
//
//    private static final String DEFAULT_RECEIVER = "AAAAAAAAAA";
//    private static final String UPDATED_RECEIVER = "BBBBBBBBBB";
//
//    private static final String DEFAULT_COPY_TO = "AAAAAAAAAA";
//    private static final String UPDATED_COPY_TO = "BBBBBBBBBB";
//
//    private static final String DEFAULT_BLIND_COPY_TO = "AAAAAAAAAA";
//    private static final String UPDATED_BLIND_COPY_TO = "BBBBBBBBBB";
//
//    private static final String DEFAULT_PATH = "AAAAAAAAAA";
//    private static final String UPDATED_PATH = "BBBBBBBBBB";
//
//    private static final String DEFAULT_MSG_ID = "AAAAAAAAAA";
//    private static final String UPDATED_MSG_ID = "BBBBBBBBBB";
//
//    private static final String DEFAULT_PARENT_ID = "AAAAAAAAAA";
//    private static final String UPDATED_PARENT_ID = "BBBBBBBBBB";
//
//    private static final String DEFAULT_IN_REPLY_TO = "AAAAAAAAAA";
//    private static final String UPDATED_IN_REPLY_TO = "BBBBBBBBBB";
//
//    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
//    private static final String UPDATED_TYPE = "BBBBBBBBBB";
//
//    private static final String DEFAULT_ETAT = "AAAAAAAAAA";
//    private static final String UPDATED_ETAT = "BBBBBBBBBB";
//
//    private static final Long DEFAULT_CORRESP_ID = 1L;
//    private static final Long UPDATED_CORRESP_ID = 2L;
//
//    private static final Long DEFAULT_FILE_ID = 1L;
//    private static final Long UPDATED_FILE_ID = 2L;
//
//    private static final String DEFAULT_UNIQUE_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_UNIQUE_NAME = "BBBBBBBBBB";
//
//    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
//    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";
//
//    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
//
//    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
//    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";
//
//    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
//    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
//
//    private static final String ENTITY_API_URL = "/api/messages";
//    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
//
//    private static Random random = new Random();
//    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
//
//    @Autowired
//    private MessageRepository messageRepository;
//
//    @Autowired
//    private MessageMapper messageMapper;
//
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private MockMvc restMessageMockMvc;
//
//    private Message message;
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Message createEntity(EntityManager em) {
//        Message message = new Message()
//            .sender(DEFAULT_SENDER)
//            .receiver(DEFAULT_RECEIVER)
//            .copyTo(DEFAULT_COPY_TO)
//            .blindCopyTo(DEFAULT_BLIND_COPY_TO)
//            .path(DEFAULT_PATH)
//            .msgID(DEFAULT_MSG_ID)
//            .parentID(DEFAULT_PARENT_ID)
//            .inReplyTo(DEFAULT_IN_REPLY_TO)
//            .type(DEFAULT_TYPE)
//            .etat(DEFAULT_ETAT)
//            .correspId(DEFAULT_CORRESP_ID)
//            .fileId(DEFAULT_FILE_ID)
//            .uniqueName(DEFAULT_UNIQUE_NAME)
//            .createdBy(DEFAULT_CREATED_BY)
//            .creationDate(DEFAULT_CREATION_DATE)
//            .updatedBy(DEFAULT_UPDATED_BY)
//            .updatedDate(DEFAULT_UPDATED_DATE);
//        return message;
//    }
//
//    /**
//     * Create an updated entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Message createUpdatedEntity(EntityManager em) {
//        Message message = new Message()
//            .sender(UPDATED_SENDER)
//            .receiver(UPDATED_RECEIVER)
//            .copyTo(UPDATED_COPY_TO)
//            .blindCopyTo(UPDATED_BLIND_COPY_TO)
//            .path(UPDATED_PATH)
//            .msgID(UPDATED_MSG_ID)
//            .parentID(UPDATED_PARENT_ID)
//            .inReplyTo(UPDATED_IN_REPLY_TO)
//            .type(UPDATED_TYPE)
//            .etat(UPDATED_ETAT)
//            .correspId(UPDATED_CORRESP_ID)
//            .fileId(UPDATED_FILE_ID)
//            .uniqueName(UPDATED_UNIQUE_NAME)
//            .createdBy(UPDATED_CREATED_BY)
//            .creationDate(UPDATED_CREATION_DATE)
//            .updatedBy(UPDATED_UPDATED_BY)
//            .updatedDate(UPDATED_UPDATED_DATE);
//        return message;
//    }
//
//    @BeforeEach
//    public void initTest() {
//        message = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    void createMessage() throws Exception {
//        int databaseSizeBeforeCreate = messageRepository.findAll().size();
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//        restMessageMockMvc
//            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
//            .andExpect(status().isCreated());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeCreate + 1);
//        Message testMessage = messageList.get(messageList.size() - 1);
//        assertThat(testMessage.getSender()).isEqualTo(DEFAULT_SENDER);
//        assertThat(testMessage.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
//        assertThat(testMessage.getCopyTo()).isEqualTo(DEFAULT_COPY_TO);
//        assertThat(testMessage.getBlindCopyTo()).isEqualTo(DEFAULT_BLIND_COPY_TO);
//        assertThat(testMessage.getPath()).isEqualTo(DEFAULT_PATH);
//        assertThat(testMessage.getMsgID()).isEqualTo(DEFAULT_MSG_ID);
//        assertThat(testMessage.getParentID()).isEqualTo(DEFAULT_PARENT_ID);
//        assertThat(testMessage.getInReplyTo()).isEqualTo(DEFAULT_IN_REPLY_TO);
//        assertThat(testMessage.getType()).isEqualTo(DEFAULT_TYPE);
//        assertThat(testMessage.getEtat()).isEqualTo(DEFAULT_ETAT);
//        assertThat(testMessage.getCorrespId()).isEqualTo(DEFAULT_CORRESP_ID);
//        assertThat(testMessage.getFileId()).isEqualTo(DEFAULT_FILE_ID);
//        assertThat(testMessage.getUniqueName()).isEqualTo(DEFAULT_UNIQUE_NAME);
//        assertThat(testMessage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
//        assertThat(testMessage.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
//        assertThat(testMessage.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
//        assertThat(testMessage.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
//    }
//
//    @Test
//    @Transactional
//    void createMessageWithExistingId() throws Exception {
//        // Create the Message with an existing ID
//        message.setId(1L);
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        int databaseSizeBeforeCreate = messageRepository.findAll().size();
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restMessageMockMvc
//            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    @Transactional
//    void getAllMessages() throws Exception {
//        // Initialize the database
//        messageRepository.saveAndFlush(message);
//
//        // Get all the messageList
//        restMessageMockMvc
//            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
//            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
//            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
//            .andExpect(jsonPath("$.[*].copyTo").value(hasItem(DEFAULT_COPY_TO)))
//            .andExpect(jsonPath("$.[*].blindCopyTo").value(hasItem(DEFAULT_BLIND_COPY_TO)))
//            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
//            .andExpect(jsonPath("$.[*].msgID").value(hasItem(DEFAULT_MSG_ID)))
//            .andExpect(jsonPath("$.[*].parentID").value(hasItem(DEFAULT_PARENT_ID)))
//            .andExpect(jsonPath("$.[*].inReplyTo").value(hasItem(DEFAULT_IN_REPLY_TO)))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
//            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT)))
//            .andExpect(jsonPath("$.[*].correspId").value(hasItem(DEFAULT_CORRESP_ID.intValue())))
//            .andExpect(jsonPath("$.[*].fileId").value(hasItem(DEFAULT_FILE_ID.intValue())))
//            .andExpect(jsonPath("$.[*].uniqueName").value(hasItem(DEFAULT_UNIQUE_NAME)))
//            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
//            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
//            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
//            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))));
//    }
//
//    @Test
//    @Transactional
//    void getMessage() throws Exception {
//        // Initialize the database
//        messageRepository.saveAndFlush(message);
//
//        // Get the message
//        restMessageMockMvc
//            .perform(get(ENTITY_API_URL_ID, message.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
//            .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER))
//            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER))
//            .andExpect(jsonPath("$.copyTo").value(DEFAULT_COPY_TO))
//            .andExpect(jsonPath("$.blindCopyTo").value(DEFAULT_BLIND_COPY_TO))
//            .andExpect(jsonPath("$.path").value(DEFAULT_PATH))
//            .andExpect(jsonPath("$.msgID").value(DEFAULT_MSG_ID))
//            .andExpect(jsonPath("$.parentID").value(DEFAULT_PARENT_ID))
//            .andExpect(jsonPath("$.inReplyTo").value(DEFAULT_IN_REPLY_TO))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
//            .andExpect(jsonPath("$.etat").value(DEFAULT_ETAT))
//            .andExpect(jsonPath("$.correspId").value(DEFAULT_CORRESP_ID.intValue()))
//            .andExpect(jsonPath("$.fileId").value(DEFAULT_FILE_ID.intValue()))
//            .andExpect(jsonPath("$.uniqueName").value(DEFAULT_UNIQUE_NAME))
//            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
//            .andExpect(jsonPath("$.creationDate").value(sameInstant(DEFAULT_CREATION_DATE)))
//            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
//            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)));
//    }
//
//    @Test
//    @Transactional
//    void getNonExistingMessage() throws Exception {
//        // Get the message
//        restMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    void putExistingMessage() throws Exception {
//        // Initialize the database
//        messageRepository.saveAndFlush(message);
//
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//
//        // Update the message
//        Message updatedMessage = messageRepository.findById(message.getId()).get();
//        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
//        em.detach(updatedMessage);
//        updatedMessage
//            .sender(UPDATED_SENDER)
//            .receiver(UPDATED_RECEIVER)
//            .copyTo(UPDATED_COPY_TO)
//            .blindCopyTo(UPDATED_BLIND_COPY_TO)
//            .path(UPDATED_PATH)
//            .msgID(UPDATED_MSG_ID)
//            .parentID(UPDATED_PARENT_ID)
//            .inReplyTo(UPDATED_IN_REPLY_TO)
//            .type(UPDATED_TYPE)
//            .etat(UPDATED_ETAT)
//            .correspId(UPDATED_CORRESP_ID)
//            .fileId(UPDATED_FILE_ID)
//            .uniqueName(UPDATED_UNIQUE_NAME)
//            .createdBy(UPDATED_CREATED_BY)
//            .creationDate(UPDATED_CREATION_DATE)
//            .updatedBy(UPDATED_UPDATED_BY)
//            .updatedDate(UPDATED_UPDATED_DATE);
//        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);
//
//        restMessageMockMvc
//            .perform(
//                put(ENTITY_API_URL_ID, messageDTO.getId())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
//            )
//            .andExpect(status().isOk());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//        Message testMessage = messageList.get(messageList.size() - 1);
//        assertThat(testMessage.getSender()).isEqualTo(UPDATED_SENDER);
//        assertThat(testMessage.getReceiver()).isEqualTo(UPDATED_RECEIVER);
//        assertThat(testMessage.getCopyTo()).isEqualTo(UPDATED_COPY_TO);
//        assertThat(testMessage.getBlindCopyTo()).isEqualTo(UPDATED_BLIND_COPY_TO);
//        assertThat(testMessage.getPath()).isEqualTo(UPDATED_PATH);
//        assertThat(testMessage.getMsgID()).isEqualTo(UPDATED_MSG_ID);
//        assertThat(testMessage.getParentID()).isEqualTo(UPDATED_PARENT_ID);
//        assertThat(testMessage.getInReplyTo()).isEqualTo(UPDATED_IN_REPLY_TO);
//        assertThat(testMessage.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testMessage.getEtat()).isEqualTo(UPDATED_ETAT);
//        assertThat(testMessage.getCorrespId()).isEqualTo(UPDATED_CORRESP_ID);
//        assertThat(testMessage.getFileId()).isEqualTo(UPDATED_FILE_ID);
//        assertThat(testMessage.getUniqueName()).isEqualTo(UPDATED_UNIQUE_NAME);
//        assertThat(testMessage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
//        assertThat(testMessage.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
//        assertThat(testMessage.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
//        assertThat(testMessage.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
//    }
//
//    @Test
//    @Transactional
//    void putNonExistingMessage() throws Exception {
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//        message.setId(count.incrementAndGet());
//
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        restMessageMockMvc
//            .perform(
//                put(ENTITY_API_URL_ID, messageDTO.getId())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
//            )
//            .andExpect(status().isBadRequest());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    void putWithIdMismatchMessage() throws Exception {
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//        message.setId(count.incrementAndGet());
//
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        restMessageMockMvc
//            .perform(
//                put(ENTITY_API_URL_ID, count.incrementAndGet())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
//            )
//            .andExpect(status().isBadRequest());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    void putWithMissingIdPathParamMessage() throws Exception {
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//        message.setId(count.incrementAndGet());
//
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        restMessageMockMvc
//            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(messageDTO)))
//            .andExpect(status().isMethodNotAllowed());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    void partialUpdateMessageWithPatch() throws Exception {
//        // Initialize the database
//        messageRepository.saveAndFlush(message);
//
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//
//        // Update the message using partial update
//        Message partialUpdatedMessage = new Message();
//        partialUpdatedMessage.setId(message.getId());
//
//        partialUpdatedMessage
//            .path(UPDATED_PATH)
//            .msgID(UPDATED_MSG_ID)
//            .type(UPDATED_TYPE)
//            .etat(UPDATED_ETAT)
//            .correspId(UPDATED_CORRESP_ID)
//            .uniqueName(UPDATED_UNIQUE_NAME)
//            .createdBy(UPDATED_CREATED_BY)
//            .creationDate(UPDATED_CREATION_DATE)
//            .updatedDate(UPDATED_UPDATED_DATE);
//
//        restMessageMockMvc
//            .perform(
//                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
//                    .contentType("application/merge-patch+json")
//                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
//            )
//            .andExpect(status().isOk());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//        Message testMessage = messageList.get(messageList.size() - 1);
//        assertThat(testMessage.getSender()).isEqualTo(DEFAULT_SENDER);
//        assertThat(testMessage.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
//        assertThat(testMessage.getCopyTo()).isEqualTo(DEFAULT_COPY_TO);
//        assertThat(testMessage.getBlindCopyTo()).isEqualTo(DEFAULT_BLIND_COPY_TO);
//        assertThat(testMessage.getPath()).isEqualTo(UPDATED_PATH);
//        assertThat(testMessage.getMsgID()).isEqualTo(UPDATED_MSG_ID);
//        assertThat(testMessage.getParentID()).isEqualTo(DEFAULT_PARENT_ID);
//        assertThat(testMessage.getInReplyTo()).isEqualTo(DEFAULT_IN_REPLY_TO);
//        assertThat(testMessage.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testMessage.getEtat()).isEqualTo(UPDATED_ETAT);
//        assertThat(testMessage.getCorrespId()).isEqualTo(UPDATED_CORRESP_ID);
//        assertThat(testMessage.getFileId()).isEqualTo(DEFAULT_FILE_ID);
//        assertThat(testMessage.getUniqueName()).isEqualTo(UPDATED_UNIQUE_NAME);
//        assertThat(testMessage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
//        assertThat(testMessage.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
//        assertThat(testMessage.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
//        assertThat(testMessage.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
//    }
//
//    @Test
//    @Transactional
//    void fullUpdateMessageWithPatch() throws Exception {
//        // Initialize the database
//        messageRepository.saveAndFlush(message);
//
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//
//        // Update the message using partial update
//        Message partialUpdatedMessage = new Message();
//        partialUpdatedMessage.setId(message.getId());
//
//        partialUpdatedMessage
//            .sender(UPDATED_SENDER)
//            .receiver(UPDATED_RECEIVER)
//            .copyTo(UPDATED_COPY_TO)
//            .blindCopyTo(UPDATED_BLIND_COPY_TO)
//            .path(UPDATED_PATH)
//            .msgID(UPDATED_MSG_ID)
//            .parentID(UPDATED_PARENT_ID)
//            .inReplyTo(UPDATED_IN_REPLY_TO)
//            .type(UPDATED_TYPE)
//            .etat(UPDATED_ETAT)
//            .correspId(UPDATED_CORRESP_ID)
//            .fileId(UPDATED_FILE_ID)
//            .uniqueName(UPDATED_UNIQUE_NAME)
//            .createdBy(UPDATED_CREATED_BY)
//            .creationDate(UPDATED_CREATION_DATE)
//            .updatedBy(UPDATED_UPDATED_BY)
//            .updatedDate(UPDATED_UPDATED_DATE);
//
//        restMessageMockMvc
//            .perform(
//                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
//                    .contentType("application/merge-patch+json")
//                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMessage))
//            )
//            .andExpect(status().isOk());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//        Message testMessage = messageList.get(messageList.size() - 1);
//        assertThat(testMessage.getSender()).isEqualTo(UPDATED_SENDER);
//        assertThat(testMessage.getReceiver()).isEqualTo(UPDATED_RECEIVER);
//        assertThat(testMessage.getCopyTo()).isEqualTo(UPDATED_COPY_TO);
//        assertThat(testMessage.getBlindCopyTo()).isEqualTo(UPDATED_BLIND_COPY_TO);
//        assertThat(testMessage.getPath()).isEqualTo(UPDATED_PATH);
//        assertThat(testMessage.getMsgID()).isEqualTo(UPDATED_MSG_ID);
//        assertThat(testMessage.getParentID()).isEqualTo(UPDATED_PARENT_ID);
//        assertThat(testMessage.getInReplyTo()).isEqualTo(UPDATED_IN_REPLY_TO);
//        assertThat(testMessage.getType()).isEqualTo(UPDATED_TYPE);
//        assertThat(testMessage.getEtat()).isEqualTo(UPDATED_ETAT);
//        assertThat(testMessage.getCorrespId()).isEqualTo(UPDATED_CORRESP_ID);
//        assertThat(testMessage.getFileId()).isEqualTo(UPDATED_FILE_ID);
//        assertThat(testMessage.getUniqueName()).isEqualTo(UPDATED_UNIQUE_NAME);
//        assertThat(testMessage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
//        assertThat(testMessage.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
//        assertThat(testMessage.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
//        assertThat(testMessage.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
//    }
//
//    @Test
//    @Transactional
//    void patchNonExistingMessage() throws Exception {
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//        message.setId(count.incrementAndGet());
//
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        restMessageMockMvc
//            .perform(
//                patch(ENTITY_API_URL_ID, messageDTO.getId())
//                    .contentType("application/merge-patch+json")
//                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
//            )
//            .andExpect(status().isBadRequest());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    void patchWithIdMismatchMessage() throws Exception {
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//        message.setId(count.incrementAndGet());
//
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        restMessageMockMvc
//            .perform(
//                patch(ENTITY_API_URL_ID, count.incrementAndGet())
//                    .contentType("application/merge-patch+json")
//                    .content(TestUtil.convertObjectToJsonBytes(messageDTO))
//            )
//            .andExpect(status().isBadRequest());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    void patchWithMissingIdPathParamMessage() throws Exception {
//        int databaseSizeBeforeUpdate = messageRepository.findAll().size();
//        message.setId(count.incrementAndGet());
//
//        // Create the Message
//        MessageDTO messageDTO = messageMapper.toDto(message);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        restMessageMockMvc
//            .perform(
//                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(messageDTO))
//            )
//            .andExpect(status().isMethodNotAllowed());
//
//        // Validate the Message in the database
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    void deleteMessage() throws Exception {
//        // Initialize the database
//        messageRepository.saveAndFlush(message);
//
//        int databaseSizeBeforeDelete = messageRepository.findAll().size();
//
//        // Delete the message
//        restMessageMockMvc
//            .perform(delete(ENTITY_API_URL_ID, message.getId()).accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNoContent());
//
//        // Validate the database contains one less item
//        List<Message> messageList = messageRepository.findAll();
//        assertThat(messageList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//}
