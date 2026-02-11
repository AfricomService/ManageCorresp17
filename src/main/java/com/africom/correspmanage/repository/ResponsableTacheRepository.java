package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.ResponsableTache;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the ResponsableTache entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ResponsableTacheRepository extends JpaRepository<ResponsableTache, Long> {

    List<ResponsableTache> findByTacheId(Long tacheId);
    void deleteByTacheId(Long tacheId);
}
