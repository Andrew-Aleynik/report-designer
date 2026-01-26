package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExternalInfluenceFormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalInfluenceFormController.class);
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    private boolean isEditMode = false;
    private boolean saved;
    private Stage dialogStage;
    private final ExternalInfluence editingExternalInfluence;
    private final ExternalInfluencesDataModel externalInfluencesDataModel;

    public ExternalInfluenceFormController(ExternalInfluencesDataModel externalInfluencesDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
        this.editingExternalInfluence = externalInfluencesDataModel.getSelectedExternalInfluence();
        if (editingExternalInfluence != null) {
            isEditMode = true;
        }
    }

    @FXML
    public void initialize() {
        if (isEditMode) {
            populateFormWithExternalInfluenceData();
        }

        nameField.textProperty().addListener(propertyChangeListener());
        descriptionField.textProperty().addListener(propertyChangeListener());

        updateOkButtonState();
    }

    @FXML
    public void handleOk() {
        boolean isValid = !validateForm();
        if (isValid) {
            if (!isEditMode) {
                createNewExternalInfluence();
            } else {
                updateExistingExternalInfluence();
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

    private void populateFormWithExternalInfluenceData() {
        nameField.setText(editingExternalInfluence.getName());
        descriptionField.setText(editingExternalInfluence.getDescription());
    }

    private void createNewExternalInfluence() {
        ExternalInfluence externalInfluence = new ExternalInfluence();
        externalInfluence.setName(nameField.getText());
        externalInfluence.setDescription(descriptionField.getText());
        externalInfluencesDataModel.saveExternalInfluence(externalInfluence);
    }

    private void updateExistingExternalInfluence() {
        editingExternalInfluence.setName(nameField.getText());
        editingExternalInfluence.setDescription(descriptionField.getText());
        externalInfluencesDataModel.updateExternalInfluence(editingExternalInfluence);
    }

    private void updateOkButtonState() {
        boolean isNotValid = validateForm();
        okButton.setDisable(isNotValid);
    }

    private boolean validateForm() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        return name == null || name.trim().isEmpty()
                || description == null || description.trim().isEmpty();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private ChangeListener<? super String> propertyChangeListener() {
        return (obs, oldVal, newVal) -> updateOkButtonState();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaved() {
        return saved;
    }
}
