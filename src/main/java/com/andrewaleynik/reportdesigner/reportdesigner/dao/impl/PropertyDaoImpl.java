package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import org.hibernate.query.Query;

import java.util.List;

public class PropertyDaoImpl extends BaseDaoImpl<Property> implements PropertyDao {

    public PropertyDaoImpl() {
        super(Property.class);
    }

    public List<Property> findByUnit(Long unit) {
        return execute(s -> {
            Query<Property> query = s.createQuery(
                    "SELECT p FROM Property p WHERE p.unit = :unit", Property.class);
            query.setParameter("unit", unit);
            return query.getResultList();
        });
    }
}
