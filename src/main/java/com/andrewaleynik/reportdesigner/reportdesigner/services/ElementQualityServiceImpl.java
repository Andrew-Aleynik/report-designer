package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementQualityDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyUnitDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ElementQualityServiceImpl implements ElementQualityService {
    private final ElementQualityDao elementQualityDao;
    private final PropertyDao propertyDao;
    private final PropertyUnitDao propertyUnitDao;

    public ElementQualityServiceImpl(ElementQualityDao elementQualityDao, PropertyDao propertyDao,
                                     PropertyUnitDao propertyUnitDao) {
        this.elementQualityDao = elementQualityDao;
        this.propertyDao = propertyDao;
        this.propertyUnitDao = propertyUnitDao;

    }

    @Override
    public List<ElementQuality> getAllQualities() {
        return elementQualityDao.findAll();
    }

    @Override
    public Optional<ElementQuality> getElementQualityById(Long id) {
        return elementQualityDao.findById(id);
    }

    @Override
    public void saveQuality(ElementQuality elementQuality) {
        elementQualityDao.save(elementQuality);
    }

    @Override
    public void deleteQuality(ElementQuality elementQuality) {
        elementQualityDao.delete(elementQuality);
    }

    @Override
    public void updateQuality(ElementQuality detachedQuality) {
        Session session = elementQualityDao.openSession();
        Transaction tx = session.beginTransaction();
        try {
            ElementQuality managed = session.find(ElementQuality.class, detachedQuality.getId());

            managed.setCode(detachedQuality.getCode());
            managed.setServiceLife(detachedQuality.getServiceLife());
            managed.setSatisfyingCost(detachedQuality.getSatisfyingCost());
            managed.setActualCost(detachedQuality.getActualCost());

            Set<Long> desiredPropertyIds = detachedQuality.getProperties().stream()
                    .map(Property::getId)
                    .collect(Collectors.toSet());

            managed.getProperties().stream()
                    .filter(property -> !desiredPropertyIds.contains(property.getId()))
                    .collect(Collectors.toSet())
                    .forEach(managed::removeProperty);

            for (Property property : detachedQuality.getProperties()) {
                Property managedProperty = session.find(Property.class, property.getId());
                if (managedProperty != null) {
                    managed.addProperty(managedProperty);
                }
            }

            session.flush();
            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
