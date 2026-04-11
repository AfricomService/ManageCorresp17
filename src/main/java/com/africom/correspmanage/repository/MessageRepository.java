package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Message;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByCorrespId(Long correspId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.correspId = :correspId")
    void deleteByCorrespId(@Param("correspId") Long correspId);

    Optional<Message> findByCorrespIdAndMessageType(Long correspId, String messageType);

    List<Message> findAllByCorrespId(Long correspId);

    // AJOUTER :
    Optional<Message> findFirstByCorrespIdOrderByIdAsc(Long correspId);
}
