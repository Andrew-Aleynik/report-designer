package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ElementTypeFormController {
    private ElementDataModel elementDataModel;

    @FXML
    private TextField nameField;
    @FXML
    private Button okButton;
    private boolean saved = false;
    private Stage dialogStage;

    public ElementTypeFormController(ElementDataModel elementDataModel) {
        this.elementDataModel = elementDataModel;
    }

    @FXML
    public void initialize() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateOkButtonState();
        });
        updateOkButtonState();
    }

    private void updateOkButtonState() {
        boolean isNotValid = validateForm();
        okButton.setDisable(isNotValid);
    }

    @FXML
    public void handleOk() {
        boolean isValid = !validateForm();
        if (isValid) {
            ElementType elementType = new ElementType();
            elementType.setName(nameField.getText());
            elementDataModel.saveElementType(elementType);
            elementDataModel.refreshNewElementType(elementType);
            saved = true;
            closeDialog();
        }
    }

    @FXML
    public void handleCancel() {
        saved = false;
        closeDialog();
    }

    private boolean validateForm() {
        String name = nameField.getText();
        return name == null || name.trim().isEmpty();
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
