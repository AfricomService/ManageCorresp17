package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.repository.PjRepository;
import com.africom.correspmanage.service.dto.PjDTO;
import com.africom.correspmanage.service.mapper.PjMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Pj}.
 */
@Service
@Transactional
public class PjService {

    private final Logger log = LoggerFactory.getLogger(PjService.class);

    private final PjRepository pjRepository;

    private final PjMapper pjMapper;

    public PjService(PjRepository pjRepository, PjMapper pjMapper) {
        this.pjRepository = pjRepository;
        this.pjMapper = pjMapper;
    }

    /**
     * Save a pj.
     *
     * @param pjDTO the entity to save.
     * @return the persisted entity.
     */
    public PjDTO save(PjDTO pjDTO) {
        log.debug("Request to save Pj : {}", pjDTO);
        Pj pj = pjMapper.toEntity(pjDTO);
        pj = pjRepository.save(pj);
        return pjMapper.toDto(pj);
    }

    /**
     * Update a pj.
     *
     * @param pjDTO the entity to save.
     * @return the persisted entity.
     */
    public PjDTO update(PjDTO pjDTO) {
        log.debug("Request to update Pj : {}", pjDTO);
        Pj pj = pjMapper.toEntity(pjDTO);
        pj = pjRepository.save(pj);
        return pjMapper.toDto(pj);
    }

    /**
     * Partially update a pj.
     *
     * @param pjDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PjDTO> partialUpdate(PjDTO pjDTO) {
        log.debug("Request to partially update Pj : {}", pjDTO);

        return pjRepository
            .findById(pjDTO.getId())
            .map(existingPj -> {
                pjMapper.partialUpdate(existingPj, pjDTO);

                return existingPj;
            })
            .map(pjRepository::save)
            .map(pjMapper::toDto);
    }

    /**
     * Get all the pjs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PjDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pjs");
        return pjRepository.findAll(pageable).map(pjMapper::toDto);
    }

    /**
     * Get one pj by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PjDTO> findOne(Long id) {
        log.debug("Request to get Pj : {}", id);
        return pjRepository.findById(id).map(pjMapper::toDto);
    }

    /**
     * Delete the pj by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Pj : {}", id);
        pjRepository.deleteById(id);
    }
}
