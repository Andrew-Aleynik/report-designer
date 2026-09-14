package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PropertyGroupFormController extends AbstractDialogController {

    private final PropertyDataModel propertyDataModel;

    @FXML
    private TextField nameField;
    @FXML
    private Button okButton;

    public PropertyGroupFormController(PropertyDataModel propertyDataModel) {
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

        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setName(nameField.getText());
        propertyDataModel.savePropertyGroup(propertyGroup);
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
