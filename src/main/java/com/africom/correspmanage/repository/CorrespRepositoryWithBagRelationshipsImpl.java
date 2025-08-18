package com.africom.correspmanage.repository;

import com.africom.correspmanage.domain.Corresp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CorrespRepositoryWithBagRelationshipsImpl implements CorrespRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Corresp> fetchBagRelationships(Optional<Corresp> corresp) {
        return corresp.map(this::fetchDossiers).map(this::fetchBordereaux).map(this::fetchAttachments).map(this::fetchTasks);
    }

    @Override
    public Page<Corresp> fetchBagRelationships(Page<Corresp> corresps) {
        return new PageImpl<>(fetchBagRelationships(corresps.getContent()), corresps.getPageable(), corresps.getTotalElements());
    }

    @Override
    public List<Corresp> fetchBagRelationships(List<Corresp> corresps) {
        return Optional
            .of(corresps)
            .map(this::fetchDossiers)
            .map(this::fetchBordereaux)
            .map(this::fetchAttachments)
            .map(this::fetchTasks)
            .orElse(Collections.emptyList());
    }

    Corresp fetchDossiers(Corresp result) {
        return entityManager
            .createQuery("select corresp from Corresp corresp left join fetch corresp.dossiers where corresp is :corresp", Corresp.class)
            .setParameter("corresp", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Corresp> fetchDossiers(List<Corresp> corresps) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, corresps.size()).forEach(index -> order.put(corresps.get(index).getId(), index));
        List<Corresp> result = entityManager
            .createQuery(
                "select distinct corresp from Corresp corresp left join fetch corresp.dossiers where corresp in :corresps",
                Corresp.class
            )
            .setParameter("corresps", corresps)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Corresp fetchBordereaux(Corresp result) {
        return entityManager
            .createQuery("select corresp from Corresp corresp left join fetch corresp.bordereaux where corresp is :corresp", Corresp.class)
            .setParameter("corresp", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Corresp> fetchBordereaux(List<Corresp> corresps) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, corresps.size()).forEach(index -> order.put(corresps.get(index).getId(), index));
        List<Corresp> result = entityManager
            .createQuery(
                "select distinct corresp from Corresp corresp left join fetch corresp.bordereaux where corresp in :corresps",
                Corresp.class
            )
            .setParameter("corresps", corresps)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Corresp fetchAttachments(Corresp result) {
        return entityManager
            .createQuery("select corresp from Corresp corresp left join fetch corresp.attachments where corresp is :corresp", Corresp.class)
            .setParameter("corresp", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Corresp> fetchAttachments(List<Corresp> corresps) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, corresps.size()).forEach(index -> order.put(corresps.get(index).getId(), index));
        List<Corresp> result = entityManager
            .createQuery(
                "select distinct corresp from Corresp corresp left join fetch corresp.attachments where corresp in :corresps",
                Corresp.class
            )
            .setParameter("corresps", corresps)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Corresp fetchTasks(Corresp result) {
        return entityManager
            .createQuery("select corresp from Corresp corresp left join fetch corresp.tasks where corresp is :corresp", Corresp.class)
            .setParameter("corresp", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Corresp> fetchTasks(List<Corresp> corresps) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, corresps.size()).forEach(index -> order.put(corresps.get(index).getId(), index));
        List<Corresp> result = entityManager
            .createQuery(
                "select distinct corresp from Corresp corresp left join fetch corresp.tasks where corresp in :corresps",
                Corresp.class
            )
            .setParameter("corresps", corresps)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
