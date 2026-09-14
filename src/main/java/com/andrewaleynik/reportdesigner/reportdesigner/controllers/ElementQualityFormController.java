package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ElementQualityFormController extends AbstractDialogController {

    private final QualityDataModel qualityDataModel;

    @FXML
    private TextField codeField;
    @FXML
    private Button okButton;

    public ElementQualityFormController(QualityDataModel qualityDataModel) {
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public void initialize() {
        codeField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        updateOkButtonState();
    }

    @FXML
    public void handleOk() {
        if (isFormInvalid()) {
            return;
        }

        ElementQuality elementQuality = new ElementQuality();
        elementQuality.setCode(codeField.getText());
        qualityDataModel.saveQuality(elementQuality);
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
        return FormValidators.isBlankOrTooShort(codeField.getText(), 3);
    }
}
