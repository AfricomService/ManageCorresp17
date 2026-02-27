package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.ContactCopieCorresp;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the ContactCopieCorresp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContactCopieCorrespRepository extends JpaRepository<ContactCopieCorresp, Long> {
    List<ContactCopieCorresp> findByIdCorrsp(Long idCorrsp);
    void deleteByIdCorrsp(Long idCorrsp);
}
