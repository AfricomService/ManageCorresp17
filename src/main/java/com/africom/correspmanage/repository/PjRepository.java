package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Pj;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Pj entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PjRepository extends JpaRepository<Pj, Long> {
    Optional<Pj> findByUniqueName(String uniqueName);
}
