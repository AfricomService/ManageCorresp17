package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Dossier;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Dossier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DossierRepository extends JpaRepository<Dossier, Long> {}
