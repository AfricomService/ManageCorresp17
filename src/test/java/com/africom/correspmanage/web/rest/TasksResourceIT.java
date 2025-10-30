package com.africom.correspmanage.web.rest;

import static com.africom.correspmanage.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.africom.correspmanage.IntegrationTest;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.service.mapper.TasksMapper;
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
 * Integration tests for the {@link TasksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TasksResourceIT {

    private static final String DEFAULT_OBJECT = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DUE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DUE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_CLOSE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CLOSE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_RESPONSIBLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSIBLE = "BBBBBBBBBB";

    private static final CorrespStatus DEFAULT_STATUS = CorrespStatus.DRAFT;
    private static final CorrespStatus UPDATED_STATUS = CorrespStatus.IN_PROGRESS;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_UPDATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_TASK_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TASK_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private TasksMapper tasksMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTasksMockMvc;

    private Tasks tasks;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tasks createEntity(EntityManager em) {
        Tasks tasks = new Tasks()
            .object(DEFAULT_OBJECT)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE)
            .creationDate(DEFAULT_CREATION_DATE)
            .startDate(DEFAULT_START_DATE)
            .dueDate(DEFAULT_DUE_DATE)
            .closeDate(DEFAULT_CLOSE_DATE)
            .responsible(DEFAULT_RESPONSIBLE)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .taskType(DEFAULT_TASK_TYPE);
        return tasks;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tasks createUpdatedEntity(EntityManager em) {
        Tasks tasks = new Tasks()
            .object(UPDATED_OBJECT)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE)
            .startDate(UPDATED_START_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .closeDate(UPDATED_CLOSE_DATE)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE)
            .taskType(UPDATED_TASK_TYPE);
        return tasks;
    }

    @BeforeEach
    public void initTest() {
        tasks = createEntity(em);
    }

    @Test
    @Transactional
    void createTasks() throws Exception {
        int databaseSizeBeforeCreate = tasksRepository.findAll().size();
        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);
        restTasksMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tasksDTO)))
            .andExpect(status().isCreated());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeCreate + 1);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getObject()).isEqualTo(DEFAULT_OBJECT);
        assertThat(testTasks.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testTasks.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);
        assertThat(testTasks.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testTasks.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTasks.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testTasks.getCloseDate()).isEqualTo(DEFAULT_CLOSE_DATE);
        assertThat(testTasks.getResponsible()).isEqualTo(DEFAULT_RESPONSIBLE);
        assertThat(testTasks.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTasks.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTasks.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testTasks.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testTasks.getTaskType()).isEqualTo(DEFAULT_TASK_TYPE);
    }

    @Test
    @Transactional
    void createTasksWithExistingId() throws Exception {
        // Create the Tasks with an existing ID
        tasks.setId(1L);
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        int databaseSizeBeforeCreate = tasksRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTasksMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tasksDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkObjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = tasksRepository.findAll().size();
        // set the field null
        tasks.setObject(null);

        // Create the Tasks, which fails.
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        restTasksMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tasksDTO)))
            .andExpect(status().isBadRequest());

        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        // Get all the tasksList
        restTasksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tasks.getId().intValue())))
            .andExpect(jsonPath("$.[*].object").value(hasItem(DEFAULT_OBJECT)))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(sameInstant(DEFAULT_START_DATE))))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(sameInstant(DEFAULT_DUE_DATE))))
            .andExpect(jsonPath("$.[*].closeDate").value(hasItem(sameInstant(DEFAULT_CLOSE_DATE))))
            .andExpect(jsonPath("$.[*].responsible").value(hasItem(DEFAULT_RESPONSIBLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(sameInstant(DEFAULT_UPDATED_DATE))))
            .andExpect(jsonPath("$.[*].taskType").value(hasItem(DEFAULT_TASK_TYPE)));
    }

    @Test
    @Transactional
    void getTasks() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        // Get the tasks
        restTasksMockMvc
            .perform(get(ENTITY_API_URL_ID, tasks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tasks.getId().intValue()))
            .andExpect(jsonPath("$.object").value(DEFAULT_OBJECT))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.creationDate").value(sameInstant(DEFAULT_CREATION_DATE)))
            .andExpect(jsonPath("$.startDate").value(sameInstant(DEFAULT_START_DATE)))
            .andExpect(jsonPath("$.dueDate").value(sameInstant(DEFAULT_DUE_DATE)))
            .andExpect(jsonPath("$.closeDate").value(sameInstant(DEFAULT_CLOSE_DATE)))
            .andExpect(jsonPath("$.responsible").value(DEFAULT_RESPONSIBLE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY))
            .andExpect(jsonPath("$.updatedDate").value(sameInstant(DEFAULT_UPDATED_DATE)))
            .andExpect(jsonPath("$.taskType").value(DEFAULT_TASK_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingTasks() throws Exception {
        // Get the tasks
        restTasksMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTasks() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();

        // Update the tasks
        Tasks updatedTasks = tasksRepository.findById(tasks.getId()).get();
        // Disconnect from session so that the updates on updatedTasks are not directly saved in db
        em.detach(updatedTasks);
        updatedTasks
            .object(UPDATED_OBJECT)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE)
            .startDate(UPDATED_START_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .closeDate(UPDATED_CLOSE_DATE)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE)
            .taskType(UPDATED_TASK_TYPE);
        TasksDTO tasksDTO = tasksMapper.toDto(updatedTasks);

        restTasksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tasksDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tasksDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getObject()).isEqualTo(UPDATED_OBJECT);
        assertThat(testTasks.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTasks.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
        assertThat(testTasks.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testTasks.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTasks.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTasks.getCloseDate()).isEqualTo(UPDATED_CLOSE_DATE);
        assertThat(testTasks.getResponsible()).isEqualTo(UPDATED_RESPONSIBLE);
        assertThat(testTasks.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTasks.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTasks.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testTasks.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testTasks.getTaskType()).isEqualTo(UPDATED_TASK_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();
        tasks.setId(count.incrementAndGet());

        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTasksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tasksDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();
        tasks.setId(count.incrementAndGet());

        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTasksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();
        tasks.setId(count.incrementAndGet());

        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTasksMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tasksDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTasksWithPatch() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();

        // Update the tasks using partial update
        Tasks partialUpdatedTasks = new Tasks();
        partialUpdatedTasks.setId(tasks.getId());

        partialUpdatedTasks
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .closeDate(UPDATED_CLOSE_DATE)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS);

        restTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTasks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTasks))
            )
            .andExpect(status().isOk());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getObject()).isEqualTo(DEFAULT_OBJECT);
        assertThat(testTasks.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTasks.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
        assertThat(testTasks.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testTasks.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testTasks.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testTasks.getCloseDate()).isEqualTo(UPDATED_CLOSE_DATE);
        assertThat(testTasks.getResponsible()).isEqualTo(UPDATED_RESPONSIBLE);
        assertThat(testTasks.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTasks.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTasks.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testTasks.getUpdatedDate()).isEqualTo(DEFAULT_UPDATED_DATE);
        assertThat(testTasks.getTaskType()).isEqualTo(DEFAULT_TASK_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTasksWithPatch() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();

        // Update the tasks using partial update
        Tasks partialUpdatedTasks = new Tasks();
        partialUpdatedTasks.setId(tasks.getId());

        partialUpdatedTasks
            .object(UPDATED_OBJECT)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE)
            .startDate(UPDATED_START_DATE)
            .dueDate(UPDATED_DUE_DATE)
            .closeDate(UPDATED_CLOSE_DATE)
            .responsible(UPDATED_RESPONSIBLE)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedDate(UPDATED_UPDATED_DATE)
            .taskType(UPDATED_TASK_TYPE);

        restTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTasks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTasks))
            )
            .andExpect(status().isOk());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
        Tasks testTasks = tasksList.get(tasksList.size() - 1);
        assertThat(testTasks.getObject()).isEqualTo(UPDATED_OBJECT);
        assertThat(testTasks.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTasks.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);
        assertThat(testTasks.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testTasks.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testTasks.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTasks.getCloseDate()).isEqualTo(UPDATED_CLOSE_DATE);
        assertThat(testTasks.getResponsible()).isEqualTo(UPDATED_RESPONSIBLE);
        assertThat(testTasks.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTasks.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTasks.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testTasks.getUpdatedDate()).isEqualTo(UPDATED_UPDATED_DATE);
        assertThat(testTasks.getTaskType()).isEqualTo(UPDATED_TASK_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();
        tasks.setId(count.incrementAndGet());

        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tasksDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();
        tasks.setId(count.incrementAndGet());

        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTasks() throws Exception {
        int databaseSizeBeforeUpdate = tasksRepository.findAll().size();
        tasks.setId(count.incrementAndGet());

        // Create the Tasks
        TasksDTO tasksDTO = tasksMapper.toDto(tasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTasksMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tasksDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tasks in the database
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTasks() throws Exception {
        // Initialize the database
        tasksRepository.saveAndFlush(tasks);

        int databaseSizeBeforeDelete = tasksRepository.findAll().size();

        // Delete the tasks
        restTasksMockMvc
            .perform(delete(ENTITY_API_URL_ID, tasks.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tasks> tasksList = tasksRepository.findAll();
        assertThat(tasksList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
