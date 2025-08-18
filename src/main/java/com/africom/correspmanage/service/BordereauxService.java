package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.Bordereaux;
import com.africom.correspmanage.repository.BordereauxRepository;
import com.africom.correspmanage.service.dto.BordereauxDTO;
import com.africom.correspmanage.service.mapper.BordereauxMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Bordereaux}.
 */
@Service
@Transactional
public class BordereauxService {

    private final Logger log = LoggerFactory.getLogger(BordereauxService.class);

    private final BordereauxRepository bordereauxRepository;

    private final BordereauxMapper bordereauxMapper;

    public BordereauxService(BordereauxRepository bordereauxRepository, BordereauxMapper bordereauxMapper) {
        this.bordereauxRepository = bordereauxRepository;
        this.bordereauxMapper = bordereauxMapper;
    }

    /**
     * Save a bordereaux.
     *
     * @param bordereauxDTO the entity to save.
     * @return the persisted entity.
     */
    public BordereauxDTO save(BordereauxDTO bordereauxDTO) {
        log.debug("Request to save Bordereaux : {}", bordereauxDTO);
        Bordereaux bordereaux = bordereauxMapper.toEntity(bordereauxDTO);
        bordereaux = bordereauxRepository.save(bordereaux);
        return bordereauxMapper.toDto(bordereaux);
    }

    /**
     * Update a bordereaux.
     *
     * @param bordereauxDTO the entity to save.
     * @return the persisted entity.
     */
    public BordereauxDTO update(BordereauxDTO bordereauxDTO) {
        log.debug("Request to update Bordereaux : {}", bordereauxDTO);
        Bordereaux bordereaux = bordereauxMapper.toEntity(bordereauxDTO);
        bordereaux = bordereauxRepository.save(bordereaux);
        return bordereauxMapper.toDto(bordereaux);
    }

    /**
     * Partially update a bordereaux.
     *
     * @param bordereauxDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BordereauxDTO> partialUpdate(BordereauxDTO bordereauxDTO) {
        log.debug("Request to partially update Bordereaux : {}", bordereauxDTO);

        return bordereauxRepository
            .findById(bordereauxDTO.getId())
            .map(existingBordereaux -> {
                bordereauxMapper.partialUpdate(existingBordereaux, bordereauxDTO);

                return existingBordereaux;
            })
            .map(bordereauxRepository::save)
            .map(bordereauxMapper::toDto);
    }

    /**
     * Get all the bordereaux.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BordereauxDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Bordereaux");
        return bordereauxRepository.findAll(pageable).map(bordereauxMapper::toDto);
    }

    /**
     * Get one bordereaux by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BordereauxDTO> findOne(Long id) {
        log.debug("Request to get Bordereaux : {}", id);
        return bordereauxRepository.findById(id).map(bordereauxMapper::toDto);
    }

    /**
     * Delete the bordereaux by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Bordereaux : {}", id);
        bordereauxRepository.deleteById(id);
    }
}
