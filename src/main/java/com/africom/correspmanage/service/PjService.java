package com.africom.correspmanage.service;

import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.repository.PjRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.dto.PjDTO;
import com.africom.correspmanage.service.mapper.PjMapper;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link Pj}.
 */
@Service
@Transactional
public class PjService {

    private final Logger log = LoggerFactory.getLogger(PjService.class);

    private final PjRepository pjRepository;

    private final PjMapper pjMapper;

    private final ApplicationProperties applicationProperties;

    public PjService(PjRepository pjRepository, PjMapper pjMapper, ApplicationProperties applicationProperties) {
        this.pjRepository = pjRepository;
        this.pjMapper = pjMapper;
        this.applicationProperties = applicationProperties;
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

    @Transactional(readOnly = true)
    public Optional<PjDTO> findOneByUniqueName(String uniqueName) {
        log.debug("Request to get Pj : {}", uniqueName);
        return pjRepository.findByUniqueName(uniqueName).map(pjMapper::toDto);
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

    public void softDelete(String uniqueName) {
        log.debug("Request to soft delete Pj by uniqueName : {}", uniqueName);
        pjRepository.findByUniqueName(uniqueName).ifPresent(pj -> {
            pj.setPjDeleted(true);
            pjRepository.save(pj);
        });
    }

    public String uploadPj(MultipartFile file, String uniqueName) throws IOException {
        Path filePath;
        try {
            //            Path pjDirectory = Paths.get(applicationProperties.getDir_pj());

            //            log.debug("FILE NAME {}",file.getOriginalFilename());

            PjDTO pjDTO = new PjDTO();

            //            log.debug("FILE NAME {}",pjDTO.getId());

            pjDTO.setName(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")));
            pjDTO.setFileType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            pjDTO.setUniqueName(uniqueName);
            //            pjDTO.setPath(pjDirectory+applicationProperties.getSlash()+pjDTO.getUniqueName()+"."+pjDTO.getFileType());
            pjDTO.setCreationDate(ZonedDateTime.now());
            pjDTO.setCreatedBy(SecurityUtils.getCurrentUserLogin().get());
            pjDTO.setTransferable(false);

            String path =
                Paths.get(applicationProperties.getDir_pj()) +
                File.separator +
                ZonedDateTime.now().getYear() +
                File.separator +
                ZonedDateTime.now().getMonth() +
                File.separator +
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Path finalPath = Paths.get(path);

            if (!Files.exists(finalPath)) {
                Files.createDirectories(finalPath);
            }

            filePath = finalPath.resolve(pjDTO.getUniqueName() + "." + pjDTO.getFileType());

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            save(pjDTO);

            return "File uploaded successfully.";
        } catch (IOException e) {
            return "Failed to upload file.";
        }
    }

    public String uploadFilePj(MultipartFile file, String uniqueName) throws IOException {
        Path filePath;
        try {
            String path =
                Paths.get(applicationProperties.getDir_pj()) +
                File.separator +
                ZonedDateTime.now().getYear() +
                File.separator +
                ZonedDateTime.now().getMonth() +
                File.separator +
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            Path finalPath = Paths.get(path);

            if (!Files.exists(finalPath)) {
                Files.createDirectories(finalPath);
            }

            filePath =
                finalPath.resolve(uniqueName + "." + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            return "Failed to upload file.";
        }
    }

    public List<PjDTO> getAllPjsByCorrespId(Long correspId) {
        log.debug("Request to get all PJs by correspId : {}", correspId);
        return pjRepository.findAllByCorrespId(correspId).stream().map(pjMapper::toDto).toList();
    }

    public ResponseEntity<Resource> getFile(@RequestParam(name = "pjId") Long pjId) {
        try {
            PjDTO pjDTO = findOne(pjId).get();

            Path finalPath = Paths.get(pjDTO.getPath());
            log.debug("path {}", finalPath);
            Resource resource = new UrlResource(finalPath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
