package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyDataModel {
    private Property editingProperty;
    private final ObservableList<PropertyUnit> propertyUnits = FXCollections.observableArrayList();
    private PropertyUnit newPropertyUnit;

    private final ObservableList<Property> currentProperties = FXCollections.observableArrayList();
    private final Set<Property> inheritedProperties = new HashSet<>();
    private final ObservableList<Property> parentProperties = FXCollections.observableArrayList();
    private final ElementService elementService;
    private final ElementQualityService elementQualityService;
    private final PropertyService propertyService;
    private final PropertyValueService propertyValueService;

    public PropertyDataModel(ElementService elementService,
                             ElementQualityService elementQualityService,
                             PropertyService propertyService,
                             PropertyValueService propertyValueService) {
        this.elementService = elementService;
        this.elementQualityService = elementQualityService;
        this.propertyService = propertyService;
        this.propertyValueService = propertyValueService;
        refreshPropertyUnits();
    }

    public Property getEditingProperty() {
        return editingProperty;
    }

    public ObservableList<PropertyUnit> getPropertyUnits() {
        return propertyUnits;
    }

    public PropertyUnit getNewPropertyUnit() {
        return newPropertyUnit;
    }

    public List<PropertyValue> getPropertyValuesOfProperty(Property property) {
        return propertyValueService.getPropertyValueOfProperty(property);
    }

    public List<PropertyValue> getPropertyValuesOfQuality(ElementQuality quality) {
        Set<Property> qualityProperties = quality.getProperties();
        return qualityProperties.stream()
                .map(propertyValueService::getPropertyValueOfProperty)
                .flatMap(Collection::stream)
                .toList();
    }

    public ObservableList<Property> getCurrentProperties() {
        return currentProperties;
    }

    public Set<Property> getInheritedProperties() {
        return inheritedProperties;
    }

    public ObservableList<Property> getParentProperties() {
        return parentProperties;
    }

    public void refreshEditingProperty(Property property) {
        editingProperty = property;
    }

    public void refreshPropertyUnits() {
        propertyUnits.setAll(propertyService.getPropertyUnits());
    }

    public void refreshNewPropertyUnit(PropertyUnit propertyUnit) {
        newPropertyUnit = propertyUnit;
    }

    public void refreshCurrentProperties(Set<Property> properties) {
        currentProperties.setAll(properties);
    }

    public void refreshParentProperties(ElementQuality childQuality) {
        Set<Property> properties = getParentProperties(childQuality).stream()
                .filter(property -> !childQuality.getProperties().contains(property))
                .collect(Collectors.toSet());
        parentProperties.setAll(properties);
    }

    public void saveProperty(Property property) {
        currentProperties.add(property);
        propertyService.saveProperty(property);
    }

    public void savePropertyValues(PropertyValueDomain propertyValueDomain) {
        Property property = propertyValueDomain.getProperty();
        ExternalInfluence influence = propertyValueDomain.getExternalInfluence();

        if (property == null || influence == null) {
            throw new IllegalArgumentException("Property and ExternalInfluence must not be null");
        }

        // Проходим по всем уровням в домене
        for (Map.Entry<ExternalInfluenceLevel, PropertyValueDomain.Pair> entry :
                propertyValueDomain.getAllLevelPairs().entrySet()) {

            ExternalInfluenceLevel level = entry.getKey();
            PropertyValueDomain.Pair pair = entry.getValue();

            PropertyValue propertyValue;

            if (pair.id() == null) {
                // Создаем новый PropertyValue
                propertyValue = new PropertyValue();
                propertyValue.setProperty(property);
                propertyValue.setExternalInfluence(influence);
                propertyValue.setExternalInfluenceLevel(level);
                propertyValue.setValue(pair.value());

                // Сохраняем и получаем ID
                propertyValueService.savePropertyValue(propertyValue);

                // Обновляем домен с новым ID
                propertyValueDomain.setLevelPair(level, propertyValue.getId(), pair.value());

            } else {
                // Обновляем существующий PropertyValue
                propertyValue = propertyValueService.getPropertyValueOfProperty(property).stream()
                        .filter(pv -> pv.getExternalInfluenceLevel().equals(level))
                        .findFirst()
                        .orElseGet(() -> null);
                if (propertyValue != null) {
                    propertyValue.setValue(pair.value());
                    propertyValue.setExternalInfluence(influence);
                    propertyValue.setExternalInfluenceLevel(level);

                    propertyValueService.updatePropertyValue(propertyValue);
                }
            }
        }
    }

    public void updateProperty(ElementQuality quality, Property property) {
        propertyService.updateProperty(property);
        refreshCurrentProperties(quality.getProperties());
    }

    public void deleteProperty(Property property) {
        currentProperties.remove(property);
        propertyService.deleteProperty(property);
    }

    public void savePropertyUnit(PropertyUnit propertyUnit) {
        propertyService.savePropertyUnit(propertyUnit);
        refreshNewPropertyUnit(propertyUnit);
        refreshPropertyUnits();
    }

    public void addInheritedProperty(Property property) {
        inheritedProperties.add(property);
    }

    public void removeInheritedProperty(Property property) {
        inheritedProperties.remove(property);
    }

    public void clearInheritedProperties() {
        inheritedProperties.clear();
    }

    private Set<Property> getParentProperties(ElementQuality quality) {
        Optional<Element> elementOptional = elementService.findElementByQualityId(quality.getId());
        if (elementOptional.isEmpty()) {
            return Collections.emptySet();
        }
        Element parentElement = elementOptional.get().getParent();
        if (parentElement == null) {
            return Collections.emptySet();
        }
        return parentElement.getQuality().getProperties();
    }
}
