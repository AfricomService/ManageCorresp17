package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.ResponsableTache;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
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

    @Query("select distinct r.codeDept from ResponsableTache r where r.tacheId = :tacheId and r.codeDept is not null")
    List<String> findDistinctCodeDeptByTacheId(@Param("tacheId") Long tacheId);

    @Query("select distinct r.matriculePers from ResponsableTache r where r.tacheId = :tacheId and r.matriculePers is not null")
    List<String> findDistinctMatriculePersByTacheId(@Param("tacheId") Long tacheId);

    List<ResponsableTache> findByTacheIdIn(List<Long> tacheIds);

}
