package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.ResponsableTache;
import com.africom.correspmanage.repository.ResponsableTacheRepository;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import com.africom.correspmanage.service.mapper.ResponsableTacheMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ResponsableTache}.
 */
@Service
@Transactional
public class ResponsableTacheService {

    private final Logger log = LoggerFactory.getLogger(ResponsableTacheService.class);

    private final ResponsableTacheRepository responsableTacheRepository;

    private final ResponsableTacheMapper responsableTacheMapper;

    public ResponsableTacheService(ResponsableTacheRepository responsableTacheRepository, ResponsableTacheMapper responsableTacheMapper) {
        this.responsableTacheRepository = responsableTacheRepository;
        this.responsableTacheMapper = responsableTacheMapper;
    }

    /**
     * Save a responsableTache.
     *
     * @param responsableTacheDTO the entity to save.
     * @return the persisted entity.
     */
    public ResponsableTacheDTO save(ResponsableTacheDTO responsableTacheDTO) {
        log.debug("Request to save ResponsableTache : {}", responsableTacheDTO);
        ResponsableTache responsableTache = responsableTacheMapper.toEntity(responsableTacheDTO);
        responsableTache = responsableTacheRepository.save(responsableTache);
        return responsableTacheMapper.toDto(responsableTache);
    }

    /**
     * Partially update a responsableTache.
     *
     * @param responsableTacheDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ResponsableTacheDTO> partialUpdate(ResponsableTacheDTO responsableTacheDTO) {
        log.debug("Request to partially update ResponsableTache : {}", responsableTacheDTO);

        return responsableTacheRepository
            .findById(responsableTacheDTO.getId())
            .map(existingResponsableTache -> {
                responsableTacheMapper.partialUpdate(existingResponsableTache, responsableTacheDTO);

                return existingResponsableTache;
            })
            .map(responsableTacheRepository::save)
            .map(responsableTacheMapper::toDto);
    }

    /**
     * Get all the responsableTaches.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ResponsableTacheDTO> findAll() {
        log.debug("Request to get all ResponsableTaches");
        return responsableTacheRepository
            .findAll()
            .stream()
            .map(responsableTacheMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one responsableTache by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ResponsableTacheDTO> findOne(Long id) {
        log.debug("Request to get ResponsableTache : {}", id);
        return responsableTacheRepository.findById(id).map(responsableTacheMapper::toDto);
    }

    /**
     * Delete the responsableTache by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ResponsableTache : {}", id);
        responsableTacheRepository.deleteById(id);
    }

    public List<ResponsableTacheDTO> saveAllForTask(Long tacheId, List<ResponsableTacheDTO> dtos) {
        // Supprime d'abord les anciens (option), puis enregistre les nouveaux
        responsableTacheRepository.deleteByTacheId(tacheId);

        List<ResponsableTache> entities = dtos.stream()
            .map(dto -> {
                ResponsableTache e = responsableTacheMapper.toEntity(dto);
                e.setTacheId(tacheId);
                return e;
            })
            .collect(Collectors.toList());

        List<ResponsableTache> saved = responsableTacheRepository.saveAll(entities);
        return saved.stream().map(responsableTacheMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsableTacheDTO> findByTacheId(Long tacheId) {
        return responsableTacheRepository.findByTacheId(tacheId)
            .stream().map(responsableTacheMapper::toDto).collect(Collectors.toList());
    }

    public void deleteByTacheId(Long tacheId) {
        responsableTacheRepository.deleteByTacheId(tacheId);
    }

    @Transactional(readOnly = true)
    public List<String> getResponsableCodeDeptByTacheId(Long tacheId) {
        return responsableTacheRepository.findByTacheId(tacheId).stream()
            .map(ResponsableTache::getCodeDept)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .distinct()
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getResponsablesMatriculePersByTacheId(Long tacheId) {
        return responsableTacheRepository.findByTacheId(tacheId).stream()
            .map(ResponsableTache::getMatriculePers)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .distinct()
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ResponsableTache> findByTacheIdIn(List<Long> tacheIds) {
        return responsableTacheRepository.findByTacheIdIn(tacheIds);
    }

    @Transactional(readOnly = true)
    public List<ResponsableTacheDTO> getResponsablesByTacheId(Long tacheId) {
        log.debug("Request to get ResponsableTaches by tacheId : {}", tacheId);
        return responsableTacheRepository.findByTacheId(tacheId)
            .stream()
            .map(responsableTacheMapper::toDto)
            .collect(Collectors.toList());
    }


}
