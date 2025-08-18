package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.HistAction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HistAction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistActionRepository extends JpaRepository<HistAction, Long> {}
