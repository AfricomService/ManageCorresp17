package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.Dossier;
import com.africom.correspmanage.repository.DossierRepository;
import com.africom.correspmanage.service.dto.DossierDTO;
import com.africom.correspmanage.service.mapper.DossierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Dossier}.
 */
@Service
@Transactional
public class DossierService {

    private final Logger log = LoggerFactory.getLogger(DossierService.class);

    private final DossierRepository dossierRepository;

    private final DossierMapper dossierMapper;

    public DossierService(DossierRepository dossierRepository, DossierMapper dossierMapper) {
        this.dossierRepository = dossierRepository;
        this.dossierMapper = dossierMapper;
    }

    /**
     * Save a dossier.
     *
     * @param dossierDTO the entity to save.
     * @return the persisted entity.
     */
    public DossierDTO save(DossierDTO dossierDTO) {
        log.debug("Request to save Dossier : {}", dossierDTO);
        Dossier dossier = dossierMapper.toEntity(dossierDTO);
        dossier = dossierRepository.save(dossier);
        return dossierMapper.toDto(dossier);
    }

    /**
     * Update a dossier.
     *
     * @param dossierDTO the entity to save.
     * @return the persisted entity.
     */
    public DossierDTO update(DossierDTO dossierDTO) {
        log.debug("Request to update Dossier : {}", dossierDTO);
        Dossier dossier = dossierMapper.toEntity(dossierDTO);
        dossier = dossierRepository.save(dossier);
        return dossierMapper.toDto(dossier);
    }

    /**
     * Partially update a dossier.
     *
     * @param dossierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DossierDTO> partialUpdate(DossierDTO dossierDTO) {
        log.debug("Request to partially update Dossier : {}", dossierDTO);

        return dossierRepository
            .findById(dossierDTO.getId())
            .map(existingDossier -> {
                dossierMapper.partialUpdate(existingDossier, dossierDTO);

                return existingDossier;
            })
            .map(dossierRepository::save)
            .map(dossierMapper::toDto);
    }

    /**
     * Get all the dossiers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DossierDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Dossiers");
        return dossierRepository.findAll(pageable).map(dossierMapper::toDto);
    }

    /**
     * Get one dossier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DossierDTO> findOne(Long id) {
        log.debug("Request to get Dossier : {}", id);
        return dossierRepository.findById(id).map(dossierMapper::toDto);
    }

    /**
     * Delete the dossier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Dossier : {}", id);
        dossierRepository.deleteById(id);
    }
}
