package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ExternalInfluenceDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ExternalInfluenceDaoImpl extends BaseDaoImpl<ExternalInfluence> implements ExternalInfluenceDao {

    public ExternalInfluenceDaoImpl() {
        super(ExternalInfluence.class);
    }

    public Optional<ExternalInfluence> findByName(String name) {
        return execute(s -> {
            Query<ExternalInfluence> query = s.createQuery(
                    "SELECT ei FROM ExternalInfluence ei WHERE ei.name = :name", ExternalInfluence.class);
            query.setParameter("name", name);
            return query.getResultStream().findFirst();
        });
    }

    public List<ExternalInfluence> findByNameContaining(String name) {
        return execute(s -> {
            Query<ExternalInfluence> query = s.createQuery(
                    "SELECT ei FROM ExternalInfluence ei WHERE ei.name LIKE :name", ExternalInfluence.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        });
    }

    public List<ExternalInfluence> findByDescriptionContaining(String description) {
        return execute(s -> {
            Query<ExternalInfluence> query = s.createQuery(
                    "SELECT ei FROM ExternalInfluence ei WHERE ei.description LIKE :description", ExternalInfluence.class);
            query.setParameter("description", "%" + description + "%");
            return query.getResultList();
        });
    }

    public List<ExternalInfluence> searchByNameOrDescription(String searchTerm) {
        return execute(s -> {
            Query<ExternalInfluence> query = s.createQuery(
                    "SELECT ei FROM ExternalInfluence ei WHERE ei.name LIKE :search OR ei.description LIKE :search",
                    ExternalInfluence.class);
            query.setParameter("search", "%" + searchTerm + "%");
            return query.getResultList();
        });
    }

    public boolean existsByName(String name) {
        return execute(s -> {
            Query<Long> query = s.createQuery(
                    "SELECT COUNT(ei) FROM ExternalInfluence ei WHERE ei.name = :name", Long.class);
            query.setParameter("name", name);
            return query.getSingleResult() > 0;
        });
    }
}
