package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.Optional;

public class PropertyFormController extends AbstractDialogController {

    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;
    private final boolean editing;
    private final Property editingProperty;

    @FXML
    private TextField nameField;
    @FXML
    private TextField qualityCriterionValueField;
    @FXML
    private ComboBox<PropertyGroup> propertyGroupComboBox;
    @FXML
    private ComboBox<PropertyUnit> unitComboBox;
    @FXML
    private Button okButton;

    public PropertyFormController(QualityDataModel qualityDataModel, PropertyDataModel propertyDataModel) {
        this.qualityDataModel = qualityDataModel;
        this.propertyDataModel = propertyDataModel;
        this.editingProperty = propertyDataModel.getEditingProperty();
        this.editing = editingProperty != null;
    }

    @FXML
    public void initialize() {
        JavaFxControls.bindComboBoxDisplay(propertyGroupComboBox, PropertyGroup::getName);
        propertyGroupComboBox.setItems(propertyDataModel.getPropertyGroups());

        JavaFxControls.bindComboBoxDisplay(unitComboBox, PropertyUnit::getName);
        unitComboBox.setItems(propertyDataModel.getPropertyUnits());

        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        qualityCriterionValueField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        propertyGroupComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        unitComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());

        if (editing) {
            populateFormWithProperty();
        }

        updateOkButtonState();
    }

    @FXML
    public void handleCreatePropertyGroup() {
        DialogOpener.<PropertyGroupFormController>open(
                App.FxmlPaths.ADD_PROPERTY_GROUP_FORM,
                "Добавление группы свойств",
                propertyGroupComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                propertyGroupComboBox.getSelectionModel().select(propertyDataModel.getNewPropertyGroup());
            }
        });
    }

    @FXML
    public void handleCreateUnit() {
        DialogOpener.<PropertyUnitFormController>open(
                App.FxmlPaths.ADD_PROPERTY_UNIT_FORM,
                "Добавление размерности",
                unitComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                unitComboBox.getSelectionModel().select(propertyDataModel.getNewPropertyUnit());
            }
        });
    }

    @FXML
    public void handleOk() {
        if (isFormInvalid()) {
            return;
        }

        if (editing) {
            updateExistingProperty();
        } else {
            createNewProperty();
        }
        markSavedAndClose();
    }

    @FXML
    public void handleCancel() {
        markCancelledAndClose();
    }

    private void populateFormWithProperty() {
        nameField.setText(Optional.ofNullable(editingProperty.getName()).orElse(""));
        qualityCriterionValueField.setText(
                Optional.ofNullable(editingProperty.getQualityCriterionValue()).orElse(""));
        propertyGroupComboBox.setValue(editingProperty.getPropertyGroup());
        unitComboBox.setValue(editingProperty.getUnit());
    }

    private void createNewProperty() {
        Property property = new Property();
        property.setName(nameField.getText());
        property.addQuality(qualityDataModel.getSelectedQuality());
        property.setQualityCriterionValue(
                Optional.ofNullable(qualityCriterionValueField.getText()).orElse(""));
        property.setPropertyGroup(propertyGroupComboBox.getValue());
        property.setUnit(unitComboBox.getValue());
        propertyDataModel.saveProperty(property);
    }

    private void updateExistingProperty() {
        editingProperty.setName(nameField.getText());
        editingProperty.setQualityCriterionValue(
                Optional.ofNullable(qualityCriterionValueField.getText()).orElse(""));
        editingProperty.setPropertyGroup(propertyGroupComboBox.getValue());
        editingProperty.setUnit(unitComboBox.getValue());
        propertyDataModel.updateProperty(qualityDataModel.getSelectedQuality(), editingProperty);
    }

    private void updateOkButtonState() {
        okButton.setDisable(isFormInvalid());
    }

    private boolean isFormInvalid() {
        return qualityDataModel.getSelectedQuality() == null
                || FormValidators.isBlank(nameField.getText())
                || propertyGroupComboBox.getValue() == null
                || unitComboBox.getValue() == null;
    }
}
