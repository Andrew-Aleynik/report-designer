package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PropertyDataModel {
    private Property editingProperty;
    private final ObservableList<PropertyUnit> propertyUnits = FXCollections.observableArrayList();
    private PropertyUnit newPropertyUnit;

    private final ObservableList<Property> currentProperties = FXCollections.observableArrayList();
    private final PropertyService propertyService;

    public PropertyDataModel(PropertyService propertyService) {
        this.propertyService = propertyService;

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

    public ObservableList<Property> getCurrentProperties() {
        return currentProperties;
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

    public void refreshCurrentProperties(List<Property> properties) {
        currentProperties.setAll(properties);
    }

    public void saveProperty(Property property) {
        currentProperties.add(property);
        propertyService.saveProperty(property);
    }

    public void updateProperty(Property property) {
        ElementQuality quality = property.getQuality();
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
}
