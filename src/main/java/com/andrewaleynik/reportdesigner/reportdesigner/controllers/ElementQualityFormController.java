package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ElementQualityFormController {
    private QualityDataModel qualityDataModel;
    @FXML
    private TextField codeField;
    @FXML
    private Button okButton;
    private boolean saved = false;
    private Stage dialogStage;


    public ElementQualityFormController(QualityDataModel qualityDataModel) {
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public void initialize() {
        codeField.textProperty().addListener((obs, oldVal, newVal) -> {
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
            ElementQuality elementQuality = new ElementQuality();
            elementQuality.setCode(codeField.getText());
            qualityDataModel.saveQuality(elementQuality);
            qualityDataModel.refreshNewQuality(elementQuality);
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
        String code = codeField.getText();
        return code == null || code.trim().isEmpty() || code.trim().length() < 3;
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
