package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyUnitDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class PropertyUnitDaoImpl extends BaseDaoImpl<PropertyUnit> implements PropertyUnitDao {

    public PropertyUnitDaoImpl() {
        super(PropertyUnit.class);
    }

    public Optional<PropertyUnit> findByName(String name) {
        return execute(s -> {
            Query<PropertyUnit> query = s.createQuery(
                    "SELECT u FROM PropertyUnit u WHERE u.name = :name", PropertyUnit.class);
            query.setParameter("name", name);
            return query.getResultStream().findFirst();
        });
    }

    public List<PropertyUnit> findByNameContaining(String name) {
        return execute(s -> {
            Query<PropertyUnit> query = s.createQuery(
                    "SELECT u FROM PropertyUnit u WHERE u.name LIKE :name", PropertyUnit.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        });
    }

    public boolean existsByName(String name) {
        return execute(s -> {
            Query<Long> query = s.createQuery(
                    "SELECT COUNT(u) FROM PropertyUnit u WHERE u.name = :name", Long.class);
            query.setParameter("name", name);
            return query.getSingleResult() > 0;
        });
    }
}
