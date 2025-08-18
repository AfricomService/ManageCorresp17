package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Corresp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface CorrespRepositoryWithBagRelationships {
    Optional<Corresp> fetchBagRelationships(Optional<Corresp> corresp);

    List<Corresp> fetchBagRelationships(List<Corresp> corresps);

    Page<Corresp> fetchBagRelationships(Page<Corresp> corresps);
}
