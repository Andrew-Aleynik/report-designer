package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyIndicator;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PropertyIndicatorFormController extends AbstractDialogController {

    private final PropertyDataModel propertyDataModel;

    @FXML
    private TextField nameField;
    @FXML
    private Button okButton;

    public PropertyIndicatorFormController(PropertyDataModel propertyDataModel) {
        this.propertyDataModel = propertyDataModel;
    }

    @FXML
    public void initialize() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        updateOkButtonState();
    }

    @FXML
    public void handleOk() {
        if (isFormInvalid()) {
            return;
        }

        PropertyIndicator propertyIndicator = new PropertyIndicator();
        propertyIndicator.setName(nameField.getText());
        propertyDataModel.savePropertyIndicator(propertyIndicator);
        markSavedAndClose();
    }

    @FXML
    public void handleCancel() {
        markCancelledAndClose();
    }

    private void updateOkButtonState() {
        okButton.setDisable(isFormInvalid());
    }

    private boolean isFormInvalid() {
        return FormValidators.isBlank(nameField.getText());
    }
}
