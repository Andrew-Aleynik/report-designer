package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ExternalInfluenceGroupFormController extends AbstractDialogController {

    private final ExternalInfluencesDataModel externalInfluencesDataModel;

    @FXML
    private TextField nameField;
    @FXML
    private Button okButton;

    public ExternalInfluenceGroupFormController(ExternalInfluencesDataModel externalInfluencesDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
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

        ExternalInfluenceGroup externalInfluenceGroup = new ExternalInfluenceGroup();
        externalInfluenceGroup.setName(nameField.getText());
        externalInfluencesDataModel.saveExternalInfluenceGroup(externalInfluenceGroup);
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
