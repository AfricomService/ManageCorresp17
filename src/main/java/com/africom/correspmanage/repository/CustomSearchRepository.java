package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.CustomSearch;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CustomSearch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomSearchRepository extends JpaRepository<CustomSearch, Long> {
    List<CustomSearch> findAllByUserLogin(String userLogin);
}
