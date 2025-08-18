package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Bordereaux;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bordereaux entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BordereauxRepository extends JpaRepository<Bordereaux, Long> {}
