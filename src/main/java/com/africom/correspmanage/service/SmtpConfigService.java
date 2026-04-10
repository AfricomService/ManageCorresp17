package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.SmtpConfig;
import com.africom.correspmanage.repository.SmtpConfigRepository;
import com.africom.correspmanage.service.dto.SmtpConfigDTO;
import com.africom.correspmanage.service.mapper.SmtpConfigMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SmtpConfig}.
 */
@Service
@Transactional
public class SmtpConfigService {

    private final Logger log = LoggerFactory.getLogger(SmtpConfigService.class);

    private final SmtpConfigRepository smtpConfigRepository;

    private final SmtpConfigMapper smtpConfigMapper;

    public SmtpConfigService(SmtpConfigRepository smtpConfigRepository, SmtpConfigMapper smtpConfigMapper) {
        this.smtpConfigRepository = smtpConfigRepository;
        this.smtpConfigMapper = smtpConfigMapper;
    }

    /**
     * Save a smtpConfig.
     *
     * @param smtpConfigDTO the entity to save.
     * @return the persisted entity.
     */
    public SmtpConfigDTO save(SmtpConfigDTO smtpConfigDTO) {
        log.debug("Request to save SmtpConfig : {}", smtpConfigDTO);
        SmtpConfig smtpConfig = smtpConfigMapper.toEntity(smtpConfigDTO);
        smtpConfig = smtpConfigRepository.save(smtpConfig);
        return smtpConfigMapper.toDto(smtpConfig);
    }

    /**
     * Partially update a smtpConfig.
     *
     * @param smtpConfigDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SmtpConfigDTO> partialUpdate(SmtpConfigDTO smtpConfigDTO) {
        log.debug("Request to partially update SmtpConfig : {}", smtpConfigDTO);

        return smtpConfigRepository
            .findById(smtpConfigDTO.getId())
            .map(existingSmtpConfig -> {
                smtpConfigMapper.partialUpdate(existingSmtpConfig, smtpConfigDTO);

                return existingSmtpConfig;
            })
            .map(smtpConfigRepository::save)
            .map(smtpConfigMapper::toDto);
    }

    /**
     * Get all the smtpConfigs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SmtpConfigDTO> findAll() {
        log.debug("Request to get all SmtpConfigs");
        return smtpConfigRepository.findAll().stream().map(smtpConfigMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one smtpConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SmtpConfigDTO> findOne(Long id) {
        log.debug("Request to get SmtpConfig : {}", id);
        return smtpConfigRepository.findById(id).map(smtpConfigMapper::toDto);
    }

    /**
     * Delete the smtpConfig by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SmtpConfig : {}", id);
        smtpConfigRepository.deleteById(id);
    }
}
