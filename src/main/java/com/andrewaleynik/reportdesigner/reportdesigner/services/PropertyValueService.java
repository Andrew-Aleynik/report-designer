package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;

import java.util.List;
import java.util.Optional;

public interface PropertyValueService {
    Optional<PropertyValue> getPropertyValueById(Long id);
    List<PropertyValue> getPropertyValueOfProperty(Property property);

    List<PropertyValue> getPropertyValues();

    void savePropertyValue(PropertyValue propertyValue);

    void updatePropertyValue(PropertyValue propertyValue);

    void deletePropertyValue(PropertyValue propertyValue);
}
