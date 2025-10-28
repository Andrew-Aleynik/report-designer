package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementTypeDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ElementTypeDaoImpl extends BaseDaoImpl<ElementType> implements ElementTypeDao {

    public ElementTypeDaoImpl() {
        super(ElementType.class);
    }

    public Optional<ElementType> findByName(String name) {
        return execute(s -> {
            Query<ElementType> query = s.createQuery(
                    "SELECT et FROM ElementType et WHERE et.name = :name", ElementType.class);
            query.setParameter("name", name);
            return query.getResultStream().findFirst();
        });
    }

    public List<ElementType> findByNameContaining(String name) {
        return execute(s -> {
            Query<ElementType> query = s.createQuery(
                    "SELECT et FROM ElementType et WHERE et.name LIKE :name", ElementType.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        });
    }

    public boolean existsByName(String name) {
        return execute(s -> {
            Query<Long> query = s.createQuery(
                    "SELECT COUNT(et) FROM ElementType et WHERE et.name = :name", Long.class);
            query.setParameter("name", name);
            return query.getSingleResult() > 0;
        });
    }
}