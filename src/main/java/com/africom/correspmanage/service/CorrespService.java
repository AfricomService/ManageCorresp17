package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.service.dto.CorrespDTO;
import com.africom.correspmanage.service.mapper.CorrespMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Corresp}.
 */
@Service
@Transactional
public class CorrespService {

    private final Logger log = LoggerFactory.getLogger(CorrespService.class);

    private final CorrespRepository correspRepository;

    private final CorrespMapper correspMapper;

    public CorrespService(CorrespRepository correspRepository, CorrespMapper correspMapper) {
        this.correspRepository = correspRepository;
        this.correspMapper = correspMapper;
    }

    /**
     * Save a corresp.
     *
     * @param correspDTO the entity to save.
     * @return the persisted entity.
     */
    public CorrespDTO save(CorrespDTO correspDTO) {
        log.debug("Request to save Corresp : {}", correspDTO);
        Corresp corresp = correspMapper.toEntity(correspDTO);
        corresp = correspRepository.save(corresp);
        return correspMapper.toDto(corresp);
    }

    /**
     * Update a corresp.
     *
     * @param correspDTO the entity to save.
     * @return the persisted entity.
     */
    public CorrespDTO update(CorrespDTO correspDTO) {
        log.debug("Request to update Corresp : {}", correspDTO);
        Corresp corresp = correspMapper.toEntity(correspDTO);
        corresp = correspRepository.save(corresp);
        return correspMapper.toDto(corresp);
    }

    /**
     * Partially update a corresp.
     *
     * @param correspDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CorrespDTO> partialUpdate(CorrespDTO correspDTO) {
        log.debug("Request to partially update Corresp : {}", correspDTO);

        return correspRepository
            .findById(correspDTO.getId())
            .map(existingCorresp -> {
                correspMapper.partialUpdate(existingCorresp, correspDTO);

                return existingCorresp;
            })
            .map(correspRepository::save)
            .map(correspMapper::toDto);
    }

    /**
     * Get all the corresps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CorrespDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Corresps");
        return correspRepository.findAll(pageable).map(correspMapper::toDto);
    }

    /**
     * Get all the corresps with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CorrespDTO> findAllWithEagerRelationships(Pageable pageable) {
        return correspRepository.findAllWithEagerRelationships(pageable).map(correspMapper::toDto);
    }

    /**
     * Get one corresp by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CorrespDTO> findOne(Long id) {
        log.debug("Request to get Corresp : {}", id);
        return correspRepository.findOneWithEagerRelationships(id).map(correspMapper::toDto);
    }

    /**
     * Delete the corresp by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Corresp : {}", id);
        correspRepository.deleteById(id);
    }
}
