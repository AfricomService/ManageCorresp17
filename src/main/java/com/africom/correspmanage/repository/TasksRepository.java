package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tasks entity.
 */

@SuppressWarnings("unused")
@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {
    @Query("""
            SELECT DISTINCT t FROM Tasks t
            JOIN t.corresps c
            WHERE t.status = :status
              AND (c.classStatus IS NULL OR c.classStatus <> 'SUPPRIMER')
        """)
    List<Tasks> findAllByStatusEquals(@Param("status") CorrespStatus status);

    List<Tasks> findAllByCorresps_Id(Long correspId);

    @Query("""
            SELECT t FROM Tasks t
            JOIN t.corresps c
            WHERE c.id = :correspId
        """)
    List<Tasks> findTasksByCorrespId(@Param("correspId") Long correspId);

    @Query(
        value = "SELECT t.* FROM tasks t " +
            "JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = t.id " +
            "JOIN acl_entry ae ON ae.acl_object_identity = aoi.id " +
            "JOIN acl_sid sid ON sid.id = ae.sid " +
            "WHERE sid.sid = :deptCode " +
            "AND (ae.mask & 1) <> 0", // READ permission
        nativeQuery = true
    )
    List<Tasks> findAllByAclForDept(@Param("deptCode") String deptCode);

    @Query("""
            SELECT COUNT(c)
            FROM Corresp c
            JOIN c.tasks t
            WHERE t.id = :taskId
        """)
    long countCorrespsUsingTask(@Param("taskId") Long taskId);

    @Query(
        value = """
            SELECT DISTINCT t.* FROM tasks t
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = t.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Tasks'
              AND sid.sid IN (:deptCodes)
              AND (ae.mask & :mask) <> 0
              AND EXISTS (
                  SELECT 1 FROM rel_corresp__tasks rct
                  JOIN corresp c ON c.id = rct.corresp_id
                  WHERE rct.tasks_id = t.id
                    AND (c.class_status IS NULL OR c.class_status <> 'SUPPRIMER')
              )
            """,
        nativeQuery = true
    )
    List<Tasks> findAllByAclForDepts(@Param("deptCodes") List<String> deptCodes, @Param("mask") int mask);

    // recherche tasks par référence de correspondance avec ACL
// REMPLACER l'ancienne méthode par celle-ci :
    @Query(
        value = """
        SELECT DISTINCT t.* FROM tasks t
        JOIN rel_corresp__tasks r ON r.tasks_id = t.id
        JOIN corresp c ON c.id = r.corresp_id
        JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = t.id
        JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
        JOIN acl_sid sid ON sid.id = ae.sid
        WHERE sid.sid IN (:deptCodes)
          AND (ae.mask & :mask) <> 0
          AND (c.class_status IS NULL OR c.class_status <> 'SUPPRIMER')
          AND (
            (:ref IS NULL OR LOWER(c.reference) LIKE LOWER(CONCAT('%', :ref, '%')))
            OR
            (:subject IS NULL OR LOWER(c.subject) LIKE LOWER(CONCAT('%', :subject, '%')))
          )
        """,
        nativeQuery = true
    )
    List<Tasks> findAllByAclForDeptsAndCorrespKeyword(
        @Param("deptCodes") List<String> deptCodes,
        @Param("mask") int mask,
        @Param("ref") String ref,
        @Param("subject") String subject
    );

    /**
     * Recherche avancée : tâches accessibles (ACL) filtrées par subject et/ou reference de la corresp associée.
     * Les filtres sont optionnels (null ou vide = pas de filtre sur ce champ).
     */
    @Query(
        value = """
            SELECT DISTINCT t.* FROM tasks t
            JOIN rel_corresp__tasks r ON r.tasks_id = t.id
            JOIN corresp c ON c.id = r.corresp_id
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = t.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Tasks'
              AND sid.sid IN (:sids)
              AND (ae.mask & :mask) <> 0
              AND (:subject IS NULL OR LOWER(c.subject) LIKE LOWER(CONCAT('%', :subject, '%')))
              AND (:reference IS NULL OR LOWER(c.reference) LIKE LOWER(CONCAT('%', :reference, '%')))
            """,
        nativeQuery = true
    )
    List<Tasks> findAllByAclForSidsAndCorrespFilters(
        @Param("sids") List<String> sids,
        @Param("mask") int mask,
        @Param("subject") String subject,
        @Param("reference") String reference
    );


    @Query(
        value = """
        SELECT combined.id FROM (
            SELECT DISTINCT t.id, t.creation_date AS dc FROM tasks t
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = t.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Tasks'
              AND sid.sid IN (:sids)
              AND (ae.mask & :mask) <> 0
              AND (:status IS NULL OR t.status = :status)
              AND (:dateFilter = 0 OR (t.start_date >= :startFrom AND t.start_date < :startToExclusive))
              AND EXISTS (
                  SELECT 1 FROM rel_corresp__tasks rct
                  JOIN corresp c ON c.id = rct.corresp_id
                  WHERE rct.tasks_id = t.id
                    AND (c.class_status IS NULL OR c.class_status <> 'SUPPRIMER')
              )
        ) combined
        ORDER BY combined.dc DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM (
            SELECT DISTINCT t.id FROM tasks t
            JOIN acl_object_identity aoi ON CAST(aoi.object_id_identity AS BIGINT) = t.id
            JOIN acl_class ac ON ac.id = aoi.object_id_class
            JOIN acl_entry ae ON ae.acl_object_identity = aoi.id
            JOIN acl_sid sid ON sid.id = ae.sid
            WHERE ac.class = 'com.africom.correspmanage.domain.Tasks'
              AND sid.sid IN (:sids)
              AND (ae.mask & :mask) <> 0
              AND (:status IS NULL OR t.status = :status)
              AND (:dateFilter = 0 OR (t.start_date >= :startFrom AND t.start_date < :startToExclusive))
              AND EXISTS (
                  SELECT 1 FROM rel_corresp__tasks rct
                  JOIN corresp c ON c.id = rct.corresp_id
                  WHERE rct.tasks_id = t.id
                    AND (c.class_status IS NULL OR c.class_status <> 'SUPPRIMER')
              )
        ) combined
        """,
        nativeQuery = true
    )
    Page<Object> findAllIdsByAclForSidsAndStatusPaged(
        @Param("sids") List<String> sids,
        @Param("mask") int mask,
        @Param("status") String status,
        @Param("dateFilter") int dateFilter,                        // <-- NEW (0 = pas de filtre, 1 = filtre actif)
        @Param("startFrom") LocalDateTime startFrom,                // <-- NEW
        @Param("startToExclusive") LocalDateTime startToExclusive,  // <-- NEW
        Pageable pageable
    );

    @Query("SELECT t FROM Tasks t WHERE t.id IN (:ids)")
    List<Tasks> findAllByIds(@Param("ids") List<Long> ids);

}
