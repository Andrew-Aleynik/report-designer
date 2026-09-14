package com.andrewaleynik.reportdesigner.reportdesigner.domains;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;

import java.util.List;

public final class PropertyValueDomainMapper {

    private PropertyValueDomainMapper() {
    }

    public static List<PropertyValueDomain> fromQuality(ElementQuality quality,
                                                        List<PropertyValue> propertyValues) {
        return quality.getProperties().stream()
                .map(property -> mapProperty(property, propertyValues))
                .toList();
    }

    private static PropertyValueDomain mapProperty(Property property, List<PropertyValue> propertyValues) {
        PropertyValueDomain row = new PropertyValueDomain(property);
        propertyValues.stream()
                .filter(pv -> pv.getProperty().equals(property))
                .forEach(pv -> applyPropertyValue(row, pv));
        return row;
    }

    private static void applyPropertyValue(PropertyValueDomain row, PropertyValue propertyValue) {
        row.setExternalInfluence(propertyValue.getExternalInfluence());
        if (propertyValue.getExternalInfluenceLevel() != null && propertyValue.getValue() != null) {
            row.setLevelPair(
                    propertyValue.getExternalInfluenceLevel(),
                    propertyValue.getId(),
                    propertyValue.getValue()
            );
        }
    }
}
