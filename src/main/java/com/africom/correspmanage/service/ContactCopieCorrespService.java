package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.ContactCopieCorresp;
import com.africom.correspmanage.repository.ContactCopieCorrespRepository;
import com.africom.correspmanage.service.dto.ContactCopieCorrespDTO;
import com.africom.correspmanage.service.mapper.ContactCopieCorrespMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ContactCopieCorresp}.
 */
@Service
@Transactional
public class ContactCopieCorrespService {

    private final Logger log = LoggerFactory.getLogger(ContactCopieCorrespService.class);

    private final ContactCopieCorrespRepository contactCopieCorrespRepository;

    private final ContactCopieCorrespMapper contactCopieCorrespMapper;

    public ContactCopieCorrespService(
        ContactCopieCorrespRepository contactCopieCorrespRepository,
        ContactCopieCorrespMapper contactCopieCorrespMapper
    ) {
        this.contactCopieCorrespRepository = contactCopieCorrespRepository;
        this.contactCopieCorrespMapper = contactCopieCorrespMapper;
    }

    /**
     * Save a contactCopieCorresp.
     */
    public ContactCopieCorrespDTO save(ContactCopieCorrespDTO contactCopieCorrespDTO) {
        log.debug("Request to save ContactCopieCorresp : {}", contactCopieCorrespDTO);
        ContactCopieCorresp contactCopieCorresp = contactCopieCorrespMapper.toEntity(contactCopieCorrespDTO);
        contactCopieCorresp = contactCopieCorrespRepository.save(contactCopieCorresp);
        return contactCopieCorrespMapper.toDto(contactCopieCorresp);
    }

    /**
     * Partial update (PATCH) a contactCopieCorresp.
     */
    public Optional<ContactCopieCorrespDTO> partialUpdate(ContactCopieCorrespDTO contactCopieCorrespDTO) {
        log.debug("Request to partially update ContactCopieCorresp : {}", contactCopieCorrespDTO);

        if (contactCopieCorrespDTO.getId() == null) {
            return Optional.empty();
        }

        return contactCopieCorrespRepository
            .findById(contactCopieCorrespDTO.getId())
            .map(existingEntity -> {
                // mapper must implement partialUpdate(ContactCopieCorresp, ContactCopieCorrespDTO)
                contactCopieCorrespMapper.partialUpdate(existingEntity, contactCopieCorrespDTO);
                return existingEntity;
            })
            .map(contactCopieCorrespRepository::save)
            .map(contactCopieCorrespMapper::toDto);
    }

    /**
     * Get all the contactCopieCorresps.
     */
    @Transactional(readOnly = true)
    public List<ContactCopieCorrespDTO> findAll() {
        log.debug("Request to get all ContactCopieCorresps");
        return contactCopieCorrespRepository
            .findAll()
            .stream()
            .map(contactCopieCorrespMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one contactCopieCorresp by id.
     */
    @Transactional(readOnly = true)
    public Optional<ContactCopieCorrespDTO> findOne(Long id) {
        log.debug("Request to get ContactCopieCorresp : {}", id);
        return contactCopieCorrespRepository.findById(id).map(contactCopieCorrespMapper::toDto);
    }

    /**
     * Delete the contactCopieCorresp by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete ContactCopieCorresp : {}", id);
        contactCopieCorrespRepository.deleteById(id);
    }

    /**
     * Save many ContactCopieCorresp for a given correspId (delete previous then save).
     */
    public List<ContactCopieCorrespDTO> saveAllForCorresp(Long correspId, List<ContactCopieCorrespDTO> dtos) {
        log.debug("Save {} ContactCopieCorresp for correspId={}", dtos == null ? 0 : dtos.size(), correspId);
        contactCopieCorrespRepository.deleteByIdCorrsp(correspId);

        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }

        List<ContactCopieCorresp> entities = dtos.stream()
            .map(contactCopieCorrespMapper::toEntity)
            .peek(e -> e.setIdCorrsp(correspId))
            .collect(Collectors.toList());

        List<ContactCopieCorresp> saved = contactCopieCorrespRepository.saveAll(entities);
        return saved.stream().map(contactCopieCorrespMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Get list by corresp id.
     */
    @Transactional(readOnly = true)
    public List<ContactCopieCorrespDTO> findByCorrespId(Long correspId) {
        log.debug("Request to get ContactCopieCorresp by correspId : {}", correspId);
        return contactCopieCorrespRepository.findByIdCorrsp(correspId)
            .stream()
            .map(contactCopieCorrespMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Delete all contact copies for a corresp.
     */
    public void deleteByCorrespId(Long correspId) {
        log.debug("Request to delete ContactCopieCorresp by correspId : {}", correspId);
        contactCopieCorrespRepository.deleteByIdCorrsp(correspId);
    }
}
