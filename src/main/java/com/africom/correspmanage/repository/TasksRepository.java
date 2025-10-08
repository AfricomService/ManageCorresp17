package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tasks entity.
 */

@SuppressWarnings("unused")
@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {
    List<Tasks> findAllByStatusEquals(CorrespStatus status);

    List<Tasks> findAllByCorresps_Id(Long correspId);
}
