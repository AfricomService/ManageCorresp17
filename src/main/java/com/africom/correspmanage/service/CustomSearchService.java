package com.africom.correspmanage.service;

import com.africom.correspmanage.domain.CustomSearch;
import com.africom.correspmanage.repository.CustomSearchRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.dto.CustomSearchDTO;
import com.africom.correspmanage.service.dto.CustomSearchViewDTO;
import com.africom.correspmanage.service.mapper.CustomSearchMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CustomSearch}.
 */
@Service
@Transactional
public class CustomSearchService {

    private final Logger log = LoggerFactory.getLogger(CustomSearchService.class);

    private final CustomSearchRepository customSearchRepository;

    private final CustomSearchMapper customSearchMapper;

    public CustomSearchService(CustomSearchRepository customSearchRepository, CustomSearchMapper customSearchMapper) {
        this.customSearchRepository = customSearchRepository;
        this.customSearchMapper = customSearchMapper;
    }

    /**
     * Save a customSearch.
     *
     * @param customSearchDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomSearchDTO save(CustomSearchDTO customSearchDTO) {
        log.debug("Request to save CustomSearch : {}", customSearchDTO);
        CustomSearch customSearch = customSearchMapper.toEntity(customSearchDTO);
        customSearch = customSearchRepository.save(customSearch);
        return customSearchMapper.toDto(customSearch);
    }

    /**
     * Update a customSearch.
     *
     * @param customSearchDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomSearchDTO update(CustomSearchDTO customSearchDTO) {
        log.debug("Request to update CustomSearch : {}", customSearchDTO);
        CustomSearch customSearch = customSearchMapper.toEntity(customSearchDTO);
        customSearch = customSearchRepository.save(customSearch);
        return customSearchMapper.toDto(customSearch);
    }

    /**
     * Partially update a customSearch.
     *
     * @param customSearchDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CustomSearchDTO> partialUpdate(CustomSearchDTO customSearchDTO) {
        log.debug("Request to partially update CustomSearch : {}", customSearchDTO);

        return customSearchRepository
            .findById(customSearchDTO.getId())
            .map(existingCustomSearch -> {
                customSearchMapper.partialUpdate(existingCustomSearch, customSearchDTO);

                return existingCustomSearch;
            })
            .map(customSearchRepository::save)
            .map(customSearchMapper::toDto);
    }

    /**
     * Get all the customSearches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomSearchDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CustomSearches");
        return customSearchRepository.findAll(pageable).map(customSearchMapper::toDto);
    }

    /**
     * Get one customSearch by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CustomSearchDTO> findOne(Long id) {
        log.debug("Request to get CustomSearch : {}", id);
        return customSearchRepository.findById(id).map(customSearchMapper::toDto);
    }

    /**
     * Delete the customSearch by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CustomSearch : {}", id);
        customSearchRepository.deleteById(id);
    }

    public CustomSearchDTO saveCustomSearch(String request, String entitySearching, String name) {
        CustomSearchDTO customSearchDTO = new CustomSearchDTO();

        String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);

        customSearchDTO.setEntitySearching(entitySearching);
        customSearchDTO.setRequest(request);
        customSearchDTO.setDateCreation(ZonedDateTime.now());
        customSearchDTO.setVisibility("Private");
        customSearchDTO.setUserLogin(SecurityUtils.getCurrentUserLogin().get());
        customSearchDTO.setName(decodedName);

        return save(customSearchDTO);
    }

    //    public List<CustomSearchDTO> findAllbyUserLogin(String userLogin){
    //        return customSearchMapper.toDto(customSearchRepository.findAllByUserLogin(userLogin));
    //    }

    public List<CustomSearchViewDTO> findAllByUserLogin() {
        return customSearchRepository
            .findAllByUserLogin(SecurityUtils.getCurrentUserLogin().get())
            .stream()
            .map(customSearch -> {
                String req = customSearch.getRequest();
                Map<String, String> filters = new HashMap<>();

                if (req != null && req.contains("?")) {
                    String query = req.substring(req.indexOf("?") + 1);
                    query = query.replace("%26", "&");

                    Arrays
                        .stream(query.split("&"))
                        .map(param -> param.split("=", 2))
                        .forEach(pair -> {
                            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                            String value = pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
                            // skip paging params if you want
                            if (!key.equals("pageIndex") && !key.equals("pageSize")) {
                                filters.put(key, value);
                            }
                        });
                }

                return new CustomSearchViewDTO(customSearch.getName(), filters);
            })
            .toList();
    }
}
