package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public class PropertyDataModel extends ObservableDataModel {

    private Property editingProperty;
    private final ObservableList<PropertyUnit> propertyUnits = FXCollections.observableArrayList();
    private final ObservableList<PropertyGroup> propertyGroups = FXCollections.observableArrayList();
    private PropertyUnit newPropertyUnit;
    private PropertyGroup newPropertyGroup;
    private final ObservableList<Property> currentProperties = FXCollections.observableArrayList();
    private final Set<Property> inheritedProperties = new HashSet<>();
    private final ObservableList<Property> parentProperties = FXCollections.observableArrayList();
    private final ElementService elementService;
    private final PropertyService propertyService;
    private final PropertyValueService propertyValueService;

    public PropertyDataModel(ElementService elementService,
                             PropertyService propertyService,
                             PropertyValueService propertyValueService) {
        this.elementService = elementService;
        this.propertyService = propertyService;
        this.propertyValueService = propertyValueService;
        refreshPropertyUnits();
        refreshPropertyGroups();
    }

    public Property getEditingProperty() {
        return editingProperty;
    }

    public ObservableList<PropertyUnit> getPropertyUnits() {
        return propertyUnits;
    }

    public ObservableList<PropertyGroup> getPropertyGroups() {
        return propertyGroups;
    }

    public PropertyUnit getNewPropertyUnit() {
        return newPropertyUnit;
    }

    public PropertyGroup getNewPropertyGroup() {
        return newPropertyGroup;
    }

    public List<PropertyValue> getPropertyValuesOfQuality(ElementQuality quality) {
        return quality.getProperties().stream()
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

    public void setEditingProperty(Property property) {
        editingProperty = property;
    }

    public void refreshPropertyUnits() {
        propertyUnits.setAll(propertyService.getPropertyUnits());
        fireChanged();
    }

    public void refreshPropertyGroups() {
        propertyGroups.setAll(propertyService.getPropertyGroups());
        fireChanged();
    }

    public void setCurrentProperties(Set<Property> properties) {
        currentProperties.setAll(properties);
        fireChanged();
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
        fireChanged();
    }

    public void savePropertyValues(PropertyValueDomain propertyValueDomain) {
        Property property = propertyValueDomain.getProperty();
        ExternalInfluence influence = propertyValueDomain.getExternalInfluence();

        if (property == null || influence == null) {
            throw new IllegalArgumentException("Property and ExternalInfluence must not be null");
        }

        for (Map.Entry<ExternalInfluenceLevel, PropertyValueDomain.Pair> entry :
                propertyValueDomain.getAllLevelPairs().entrySet()) {

            ExternalInfluenceLevel level = entry.getKey();
            PropertyValueDomain.Pair pair = entry.getValue();

            if (pair.id() == null) {
                PropertyValue propertyValue = new PropertyValue();
                propertyValue.setProperty(property);
                propertyValue.setExternalInfluence(influence);
                propertyValue.setExternalInfluenceLevel(level);
                propertyValue.setValue(pair.value());
                propertyValueService.savePropertyValue(propertyValue);
                propertyValueDomain.setLevelPair(level, propertyValue.getId(), pair.value());
            } else {
                PropertyValue propertyValue = propertyValueService.getPropertyValueOfProperty(property).stream()
                        .filter(pv -> pv.getExternalInfluenceLevel().equals(level))
                        .findFirst()
                        .orElse(null);
                if (propertyValue != null) {
                    propertyValue.setValue(pair.value());
                    propertyValue.setExternalInfluence(influence);
                    propertyValue.setExternalInfluenceLevel(level);
                    propertyValueService.updatePropertyValue(propertyValue);
                }
            }
        }
        fireChanged();
    }

    public void updateProperty(ElementQuality quality, Property property) {
        propertyService.updateProperty(property);
        setCurrentProperties(quality.getProperties());
    }

    public void savePropertyUnit(PropertyUnit propertyUnit) {
        propertyService.savePropertyUnit(propertyUnit);
        newPropertyUnit = propertyUnit;
        refreshPropertyUnits();
    }

    public void savePropertyGroup(PropertyGroup propertyGroup) {
        propertyService.savePropertyGroup(propertyGroup);
        newPropertyGroup = propertyGroup;
        refreshPropertyGroups();
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
        if (parentElement == null || parentElement.getQuality() == null) {
            return Collections.emptySet();
        }
        return parentElement.getQuality().getProperties();
    }
}
