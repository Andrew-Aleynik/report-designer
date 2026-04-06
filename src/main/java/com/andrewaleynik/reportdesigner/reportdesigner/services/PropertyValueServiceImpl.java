package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyValueDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;

import java.util.List;
import java.util.Optional;

public class PropertyValueServiceImpl implements PropertyValueService {
    private final PropertyValueDao dao;

    public PropertyValueServiceImpl(PropertyValueDao propertyValueDao) {
        dao = propertyValueDao;
    }

    @Override
    public Optional<PropertyValue> getPropertyValueById(Long id) {
        return dao.findById(id);
    }

    @Override
    public List<PropertyValue> getPropertyValueOfProperty(Property property) {
        return dao.findAll().stream()
                .filter(propertyValue -> propertyValue.getProperty().equals(property))
                .toList();
    }

    @Override
    public List<PropertyValue> getPropertyValues() {
        return dao.findAll();
    }

    @Override
    public void savePropertyValue(PropertyValue propertyValue) {
        dao.save(propertyValue);
    }

    @Override
    public void updatePropertyValue(PropertyValue propertyValue) {
        dao.update(propertyValue);
    }

    @Override
    public void deletePropertyValue(PropertyValue propertyValue) {
        dao.delete(propertyValue);
    }
}
