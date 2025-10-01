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

    Page<Corresp> findAll(Specification<Corresp> spec, Pageable pageable);
}
