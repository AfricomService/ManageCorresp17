package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.repository.TasksRepository;
import com.africom.correspmanage.service.dto.TasksDTO;
import com.africom.correspmanage.service.mapper.TasksMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tasks}.
 */
@Service
@Transactional
public class TasksService {

    private final Logger log = LoggerFactory.getLogger(TasksService.class);

    private final TasksRepository tasksRepository;

    private final TasksMapper tasksMapper;

    public TasksService(TasksRepository tasksRepository, TasksMapper tasksMapper) {
        this.tasksRepository = tasksRepository;
        this.tasksMapper = tasksMapper;
    }

    /**
     * Save a tasks.
     *
     * @param tasksDTO the entity to save.
     * @return the persisted entity.
     */
    public TasksDTO save(TasksDTO tasksDTO) {
        log.debug("Request to save Tasks : {}", tasksDTO);
        Tasks tasks = tasksMapper.toEntity(tasksDTO);
        tasks = tasksRepository.save(tasks);
        return tasksMapper.toDto(tasks);
    }

    /**
     * Update a tasks.
     *
     * @param tasksDTO the entity to save.
     * @return the persisted entity.
     */
    public TasksDTO update(TasksDTO tasksDTO) {
        log.debug("Request to update Tasks : {}", tasksDTO);
        Tasks tasks = tasksMapper.toEntity(tasksDTO);
        tasks = tasksRepository.save(tasks);
        return tasksMapper.toDto(tasks);
    }

    /**
     * Partially update a tasks.
     *
     * @param tasksDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TasksDTO> partialUpdate(TasksDTO tasksDTO) {
        log.debug("Request to partially update Tasks : {}", tasksDTO);

        return tasksRepository
            .findById(tasksDTO.getId())
            .map(existingTasks -> {
                tasksMapper.partialUpdate(existingTasks, tasksDTO);

                return existingTasks;
            })
            .map(tasksRepository::save)
            .map(tasksMapper::toDto);
    }

    /**
     * Get all the tasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TasksDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return tasksRepository.findAll(pageable).map(tasksMapper::toDto);
    }

    /**
     * Get one tasks by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TasksDTO> findOne(Long id) {
        log.debug("Request to get Tasks : {}", id);
        return tasksRepository.findById(id).map(tasksMapper::toDto);
    }

    /**
     * Delete the tasks by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tasks : {}", id);
        tasksRepository.deleteById(id);
    }
}
