package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementQualityDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

public class ElementQualityDaoImpl extends BaseDaoImpl<ElementQuality> implements ElementQualityDao {

    public ElementQualityDaoImpl() {
        super(ElementQuality.class);
    }

    public List<ElementQuality> findByServiceLife(Long serviceLife) {
        return execute(s -> {
            Query<ElementQuality> query = s.createQuery(
                    "SELECT eq FROM ElementQuality eq WHERE eq.serviceLife = :serviceLife", ElementQuality.class);
            query.setParameter("serviceLife", serviceLife);
            return query.getResultList();
        });
    }

    public List<ElementQuality> findBySatisfyingCostBetween(BigDecimal minCost, BigDecimal maxCost) {
        return execute(s -> {
            Query<ElementQuality> query = s.createQuery(
                    "SELECT eq FROM ElementQuality eq WHERE eq.satisfyingCost BETWEEN :minCost AND :maxCost",
                    ElementQuality.class);
            query.setParameter("minCost", minCost);
            query.setParameter("maxCost", maxCost);
            return query.getResultList();
        });
    }

    public List<ElementQuality> findByActualCostBetween(BigDecimal minCost, BigDecimal maxCost) {
        return execute(s -> {
            Query<ElementQuality> query = s.createQuery(
                    "SELECT eq FROM ElementQuality eq WHERE eq.actualCost BETWEEN :minCost AND :maxCost",
                    ElementQuality.class);
            query.setParameter("minCost", minCost);
            query.setParameter("maxCost", maxCost);
            return query.getResultList();
        });
    }

    public List<ElementQuality> findByPropertiesCountGreaterThan(int minPropertiesCount) {
        return execute(s -> {
            Query<ElementQuality> query = s.createQuery(
                    "SELECT eq FROM ElementQuality eq WHERE SIZE(eq.properties) > :minPropertiesCount",
                    ElementQuality.class);
            query.setParameter("minPropertiesCount", minPropertiesCount);
            return query.getResultList();
        });
    }
}
