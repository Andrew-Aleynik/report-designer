package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    List<Property> getPropertiesOf(ElementQuality quality);

    List<PropertyUnit> getPropertyUnits();

    void saveProperty(Property property);

    Optional<Property> findPropertyById(Long id);

    void updateProperty(Property property);

    void deleteProperty(Property property);

    void validateProperty(Property property);

    void savePropertyUnit(PropertyUnit propertyUnit);
}
