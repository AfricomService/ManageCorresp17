package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.SmtpConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the SmtpConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SmtpConfigRepository extends JpaRepository<SmtpConfig, Long> {
    // Récupérer la config active par défaut (name = "default")
    Optional<SmtpConfig> findByNameAndActiveTrue(String name);

    // Récupérer toutes les configs actives
    List<SmtpConfig> findAllByActiveTrue();

    // Récupérer la première config active (fallback)
    Optional<SmtpConfig> findFirstByActiveTrue();
}
