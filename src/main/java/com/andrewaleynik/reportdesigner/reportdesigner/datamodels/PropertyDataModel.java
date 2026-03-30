package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

    public PropertyDataModel(ElementService elementService,
                             ElementQualityService elementQualityService,
                             PropertyService propertyService) {
        this.elementService = elementService;
        this.elementQualityService = elementQualityService;
        this.propertyService = propertyService;
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
