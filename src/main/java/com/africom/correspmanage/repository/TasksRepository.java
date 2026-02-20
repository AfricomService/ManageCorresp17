package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tasks entity.
 */

@SuppressWarnings("unused")
@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {
    List<Tasks> findAllByStatusEquals(CorrespStatus status);

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
            """,
        nativeQuery = true
    )
    List<Tasks> findAllByAclForDepts(@Param("deptCodes") List<String> deptCodes, @Param("mask") int mask);

    // recherche tasks par référence de correspondance avec ACL
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
              AND LOWER(c.reference) LIKE LOWER(CONCAT('%', :ref, '%'))
            """,
        nativeQuery = true
    )
    List<Tasks> findAllByAclForDeptsAndCorrespReferenceLike(
        @Param("deptCodes") List<String> deptCodes,
        @Param("mask") int mask,
        @Param("ref") String ref
    );

}
