package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.HistAction;
import com.africom.correspmanage.repository.HistActionRepository;
import com.africom.correspmanage.service.dto.HistActionDTO;
import com.africom.correspmanage.service.mapper.HistActionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link HistAction}.
 */
@Service
@Transactional
public class HistActionService {

    private final Logger log = LoggerFactory.getLogger(HistActionService.class);

    private final HistActionRepository histActionRepository;

    private final HistActionMapper histActionMapper;

    public HistActionService(HistActionRepository histActionRepository, HistActionMapper histActionMapper) {
        this.histActionRepository = histActionRepository;
        this.histActionMapper = histActionMapper;
    }

    /**
     * Save a histAction.
     *
     * @param histActionDTO the entity to save.
     * @return the persisted entity.
     */
    public HistActionDTO save(HistActionDTO histActionDTO) {
        log.debug("Request to save HistAction : {}", histActionDTO);
        HistAction histAction = histActionMapper.toEntity(histActionDTO);
        histAction = histActionRepository.save(histAction);
        return histActionMapper.toDto(histAction);
    }

    /**
     * Update a histAction.
     *
     * @param histActionDTO the entity to save.
     * @return the persisted entity.
     */
    public HistActionDTO update(HistActionDTO histActionDTO) {
        log.debug("Request to update HistAction : {}", histActionDTO);
        HistAction histAction = histActionMapper.toEntity(histActionDTO);
        histAction = histActionRepository.save(histAction);
        return histActionMapper.toDto(histAction);
    }

    /**
     * Partially update a histAction.
     *
     * @param histActionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HistActionDTO> partialUpdate(HistActionDTO histActionDTO) {
        log.debug("Request to partially update HistAction : {}", histActionDTO);

        return histActionRepository
            .findById(histActionDTO.getId())
            .map(existingHistAction -> {
                histActionMapper.partialUpdate(existingHistAction, histActionDTO);

                return existingHistAction;
            })
            .map(histActionRepository::save)
            .map(histActionMapper::toDto);
    }

    /**
     * Get all the histActions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<HistActionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all HistActions");
        return histActionRepository.findAll(pageable).map(histActionMapper::toDto);
    }

    /**
     * Get one histAction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HistActionDTO> findOne(Long id) {
        log.debug("Request to get HistAction : {}", id);
        return histActionRepository.findById(id).map(histActionMapper::toDto);
    }

    /**
     * Delete the histAction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete HistAction : {}", id);
        histActionRepository.deleteById(id);
    }
}
