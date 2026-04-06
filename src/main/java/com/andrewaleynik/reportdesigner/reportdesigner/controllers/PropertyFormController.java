package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class PropertyFormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyFormController.class);
    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField qualityCriterionValueField;
    @FXML
    private ComboBox<PropertyUnit> unitComboBox;
    @FXML
    private Button okButton;
    private boolean editing = false;
    private boolean saved = false;

    private Property editingProperty;
    private Stage dialogStage;

    public PropertyFormController(QualityDataModel qualityDataModel, PropertyDataModel propertyDataModel) {
        this.qualityDataModel = qualityDataModel;
        this.propertyDataModel = propertyDataModel;
        if (propertyDataModel.getEditingProperty() != null) {
            editing = true;
            editingProperty = propertyDataModel.getEditingProperty();
        }
    }

    @FXML
    public void initialize() {
        initializeUnitComboBox();
        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        qualityCriterionValueField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        unitComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());

        if (editing) {
            populateFormWithProperty();
        }

        updateOkButtonState();
    }

    private void initializeUnitComboBox() {
        unitComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PropertyUnit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        unitComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PropertyUnit item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        unitComboBox.setItems(propertyDataModel.getPropertyUnits());
    }

    private void updateOkButtonState() {
        boolean isNotValid = validateForm();
        okButton.setDisable(isNotValid);
    }

    @FXML
    public void handleCreateUnit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_PROPERTY_UNIT_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            PropertyUnitFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Добавление размерности");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(unitComboBox.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            controller.setDialogStage(stage);

            stage.showAndWait();

            if (controller.isSaved()) {
                unitComboBox.getSelectionModel().select(propertyDataModel.getNewPropertyUnit());
            }
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    @FXML
    public void handleOk() {
        boolean isValid = !validateForm();
        if (isValid) {
            if (editing && editingProperty != null) {
                updateExistingProperty();
            } else {
                createNewProperty();
            }
            saved = true;
            closeDialog();
        }
    }

    @FXML
    public void handleCancel() {
        saved = false;
        closeDialog();
    }

    private void populateFormWithProperty() {
        nameField.textProperty().set(editingProperty.getName());
        qualityCriterionValueField.textProperty().set(editingProperty.getQualityCriterionValue());
        if (editingProperty.getUnit() != null) {
            unitComboBox.valueProperty().set(
                    editingProperty.getUnit()
            );
        }
    }

    private boolean validateForm() {
        ElementQuality quality = qualityDataModel.getSelectedQuality();
        String name = nameField.getText().trim();
        PropertyUnit unit = unitComboBox.getSelectionModel().getSelectedItem();
        return quality == null || name.isEmpty() || unit == null;
    }

    private void createNewProperty() {
        Property property = new Property();
        property.setName(nameField.getText());
        property.addQuality(qualityDataModel.getSelectedQuality());
        property.setQualityCriterionValue(
                Optional.ofNullable(qualityCriterionValueField.getText())
                        .orElse("")
        );
        property.setUnit(unitComboBox.getValue());
        propertyDataModel.saveProperty(property);
    }

    private void updateExistingProperty() {
        editingProperty.setName(nameField.getText());
        editingProperty.setQualityCriterionValue(
                Optional.ofNullable(qualityCriterionValueField.getText())
                        .orElse("")
        );
        editingProperty.setUnit(unitComboBox.getValue());
        propertyDataModel.updateProperty(qualityDataModel.getSelectedQuality(), editingProperty);
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaved() {
        return saved;
    }
}
