package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementQualityDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyUnitDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;

import java.util.List;
import java.util.Optional;

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
    public void updateQuality(ElementQuality elementQuality) {
        elementQualityDao.update(elementQuality);
    }
}
