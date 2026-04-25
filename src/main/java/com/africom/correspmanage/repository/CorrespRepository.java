package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Corresp entity.
 *
 * When extending this class, extend CorrespRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface CorrespRepository extends CorrespRepositoryWithBagRelationships, JpaRepository<Corresp, Long> {
    default Optional<Corresp> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Corresp> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Corresp> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Page<Corresp> findAllByCorresptype(CorrespType corresptype, Pageable pageable);

    Page<Corresp> findAllByReferenceIsNotNull(Pageable pageable);

    List<Corresp> findAllByReferenceIsNotNull();

    Page<Corresp> findAll(Specification<Corresp> spec, Pageable pageable);

    @Query(
        value = "SELECT c.* FROM corresp c " +
            "JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id " +
            "JOIN acl_entry ae ON ae.acl_object_identity = aoi.id " +
            "JOIN acl_sid sid ON sid.id = ae.sid " +
            "WHERE sid.sid = :deptCode " +
            "AND (ae.mask & 1) <> 0", // READ bit set
        nativeQuery = true
    )
    List<Corresp> findAllByAclForDept(@Param("deptCode") String deptCode);

    @Query(
        value = "SELECT c.* FROM corresp c " +
            "JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id " +
            "JOIN acl_entry ae ON ae.acl_object_identity = aoi.id " +
            "JOIN acl_sid sid ON sid.id = ae.sid " +
            "WHERE sid.sid = :deptCode AND (ae.mask & 1) <> 0",
        countQuery = "SELECT COUNT(*) FROM corresp c " +
            "JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id " +
            "JOIN acl_entry ae ON ae.acl_object_identity = aoi.id " +
            "JOIN acl_sid sid ON sid.id = ae.sid " +
            "WHERE sid.sid = :deptCode AND (ae.mask & 1) <> 0",
        nativeQuery = true
    )
    Page<Corresp> findAllByAclForDeptWithPagination(@Param("deptCode") String deptCode, Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT c.* FROM corresp c
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
              AND sid.sid IN (:sids)
              AND (ae.mask & :mask) <> 0
            ORDER BY c.date_created DESC
            """,
        nativeQuery = true
    )
    List<Corresp> findAllByAclForSids(@Param("sids") List<String> sids, @Param("mask") int mask);

    @Query("SELECT c FROM Corresp c WHERE LOWER(c.createdBy) = LOWER(:login) AND c.status = 'DRAFT' ORDER BY c.dateCreated DESC")
    List<Corresp> findAllDraftByCreatedBy(@Param("login") String login);

    @Query("SELECT c FROM Corresp c WHERE c.status = 'DISPATCH' ORDER BY c.dateCreated DESC")
    List<Corresp> findAllByStatusDispatch();

    @Query(
        value = """
        SELECT combined.id FROM (
            SELECT DISTINCT c.id, c.date_created AS dc FROM corresp c
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
              AND sid.sid IN (:sids)
              AND (ae.mask & :mask) <> 0
            UNION
            SELECT c.id, c.date_created AS dc FROM corresp c
            WHERE LOWER(c.created_by) = LOWER(:login)
              AND c.status = 'DRAFT'
        ) combined
        ORDER BY combined.dc DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM (
            SELECT DISTINCT c.id FROM corresp c
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
              AND sid.sid IN (:sids)
              AND (ae.mask & :mask) <> 0
            UNION
            SELECT c.id FROM corresp c
            WHERE LOWER(c.created_by) = LOWER(:login)
              AND c.status = 'DRAFT'
        ) combined
        """,
        nativeQuery = true
    )
    Page<Number> findAllIdsByAclForSidsOrDraft(
        @Param("sids") List<String> sids,
        @Param("mask") int mask,
        @Param("login") String login,
        Pageable pageable
    );

    // Récupérer les entités complètes par IDs (avec eager relationships si besoin)
    @Query("SELECT c FROM Corresp c WHERE c.id IN (:ids) ORDER BY c.dateCreated DESC")
    List<Corresp> findAllByIds(@Param("ids") List<Long> ids);

    @Query(
        value = """
SELECT combined.id FROM (
    SELECT DISTINCT c.id, c.date_created AS dc, c.id AS cid FROM corresp c
    JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id
    JOIN acl_class ac ON ac.id = aoi.object_id_class
    JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
    JOIN acl_sid sid ON sid.id = ae.sid
    WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
      AND sid.sid IN (:sids)
      AND (ae.mask & :mask) <> 0
    UNION
    SELECT c.id, c.date_created AS dc, c.id AS cid FROM corresp c
    WHERE LOWER(c.created_by) = LOWER(:login)
      AND c.status = 'DRAFT'
    UNION
    SELECT c.id, c.date_created AS dc, c.id AS cid FROM corresp c
    WHERE c.status = 'DISPATCH'
) combined
ORDER BY combined.dc DESC, combined.cid DESC
""",
        countQuery = """
SELECT COUNT(*) FROM (
    SELECT DISTINCT c.id FROM corresp c
    JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c.id
    JOIN acl_class ac ON ac.id = aoi.object_id_class
    JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
    JOIN acl_sid sid ON sid.id = ae.sid
    WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
      AND sid.sid IN (:sids)
      AND (ae.mask & :mask) <> 0
    UNION
    SELECT c.id FROM corresp c
    WHERE LOWER(c.created_by) = LOWER(:login)
      AND c.status = 'DRAFT'
    UNION
    SELECT c.id FROM corresp c
    WHERE c.status = 'DISPATCH'
) combined
""",
        nativeQuery = true
    )
    Page<Number> findAllIdsByAclOrDraftOrDispatch(
        @Param("sids") List<String> sids,
        @Param("mask") int mask,
        @Param("login") String login,
        Pageable pageable
    );

    @Query(value = """
    SELECT c.id, c.reference, c.subject, c.date_created, c.sender
    FROM corresp c
    WHERE c.reference IS NOT NULL
    ORDER BY c.date_created DESC
    """,
        countQuery = """
    SELECT COUNT(*) FROM corresp c WHERE c.reference IS NOT NULL
    """,
        nativeQuery = true)
    Page<Object[]> findReferencesPagedRaw(Pageable pageable);

    @Query(value = """
    SELECT c.id, c.reference, c.subject, c.date_created, c.sender
    FROM corresp c
    WHERE c.reference IS NOT NULL
      AND (LOWER(c.reference) LIKE :search
        OR LOWER(c.subject)   LIKE :search)
    ORDER BY c.date_created DESC
    """,
        countQuery = """
    SELECT COUNT(*) FROM corresp c
    WHERE c.reference IS NOT NULL
      AND (LOWER(c.reference) LIKE :search
        OR LOWER(c.subject)   LIKE :search)
    """,
        nativeQuery = true)
    Page<Object[]> findReferencesPagedFilteredRaw(
        @Param("search") String search, Pageable pageable);

    @Query(value = """
    SELECT c.id, c.reference, c.subject, c.date_created, c.sender
    FROM corresp c
    WHERE c.reference IS NOT NULL
      AND c.id IN (
          SELECT DISTINCT sub.id FROM (
              SELECT c2.id FROM corresp c2
              JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c2.id
              JOIN acl_class ac ON ac.id = aoi.object_id_class
              JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
              JOIN acl_sid sid ON sid.id = ae.sid
              WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
                AND sid.sid IN (:sids)
                AND (ae.mask & 1) <> 0
              UNION
              SELECT c3.id FROM corresp c3
              WHERE LOWER(c3.created_by) = LOWER(:login)
                AND c3.status = 'DRAFT'
          ) sub
      )
    ORDER BY c.date_created DESC
    """,
        countQuery = """
    SELECT COUNT(*) FROM corresp c
    WHERE c.reference IS NOT NULL
      AND c.id IN (
          SELECT DISTINCT sub.id FROM (
              SELECT c2.id FROM corresp c2
              JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c2.id
              JOIN acl_class ac ON ac.id = aoi.object_id_class
              JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
              JOIN acl_sid sid ON sid.id = ae.sid
              WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
                AND sid.sid IN (:sids)
                AND (ae.mask & 1) <> 0
              UNION
              SELECT c3.id FROM corresp c3
              WHERE LOWER(c3.created_by) = LOWER(:login)
                AND c3.status = 'DRAFT'
          ) sub
      )
    """,
        nativeQuery = true)
    Page<Object[]> findReferencesPagedByAclRaw(
        @Param("sids") List<String> sids,
        @Param("login") String login,
        Pageable pageable);

    @Query(value = """
    SELECT c.id, c.reference, c.subject, c.date_created, c.sender
    FROM corresp c
    WHERE c.reference IS NOT NULL
      AND (LOWER(c.reference) LIKE :search OR LOWER(c.subject) LIKE :search)
      AND c.id IN (
          SELECT DISTINCT sub.id FROM (
              SELECT c2.id FROM corresp c2
              JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c2.id
              JOIN acl_class ac ON ac.id = aoi.object_id_class
              JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
              JOIN acl_sid sid ON sid.id = ae.sid
              WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
                AND sid.sid IN (:sids)
                AND (ae.mask & 1) <> 0
              UNION
              SELECT c3.id FROM corresp c3
              WHERE LOWER(c3.created_by) = LOWER(:login)
                AND c3.status = 'DRAFT'
          ) sub
      )
    ORDER BY c.date_created DESC
    """,
        countQuery = """
    SELECT COUNT(*) FROM corresp c
    WHERE c.reference IS NOT NULL
      AND (LOWER(c.reference) LIKE :search OR LOWER(c.subject) LIKE :search)
      AND c.id IN (
          SELECT DISTINCT sub.id FROM (
              SELECT c2.id FROM corresp c2
              JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = c2.id
              JOIN acl_class ac ON ac.id = aoi.object_id_class
              JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
              JOIN acl_sid sid ON sid.id = ae.sid
              WHERE ac.class = 'com.africom.correspmanage.domain.Corresp'
                AND sid.sid IN (:sids)
                AND (ae.mask & 1) <> 0
              UNION
              SELECT c3.id FROM corresp c3
              WHERE LOWER(c3.created_by) = LOWER(:login)
                AND c3.status = 'DRAFT'
          ) sub
      )
    """,
        nativeQuery = true)
    Page<Object[]> findReferencesPagedByAclFilteredRaw(
        @Param("sids") List<String> sids,
        @Param("login") String login,
        @Param("search") String search,
        Pageable pageable);

    @Query("select c from Corresp c left join fetch c.attachments where c.id = :id")
    Optional<Corresp> findOneWithEagerRelationshipss(@Param("id") Long id);

    @Query("""
    SELECT c FROM Corresp c
    WHERE (:search IS NULL OR :search = ''
        OR LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(c.subject)   LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(c.sender)    LIKE LOWER(CONCAT('%', :search, '%'))
    )
    ORDER BY c.dateCreated DESC
    """)
    Page<Corresp> searchByKeyword(@Param("search") String search, Pageable pageable);

    /**
     * Récupère les champs d'affichage de la liste (avec sender/receiver du message COURRIER)
     * pour une liste d'IDs donnés.
     * LEFT JOIN sur message pour ne pas exclure les corresps sans message.
     */
    @Query(value = """
        SELECT
        c.id,
        c.reference,
        c.corresptype,
        c.courrier_date,
        c.subject,
        c.status,
        c.priority,
        c.sender          AS msg_sender,
        c.label_destinataire AS msg_receiver,
        c.dispatch_type,
        c.nom_societe
    FROM corresp c
    WHERE c.id IN (:ids)
    ORDER BY c.date_created DESC
    """,
        nativeQuery = true)
    List<Object[]> findListItemsByIds(@Param("ids") List<Long> ids);

}
