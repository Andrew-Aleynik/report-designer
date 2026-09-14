package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ExternalInfluenceFormController extends AbstractDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<ExternalInfluenceGroup> groupComboBox;
    @FXML
    private Button okButton;

    private final boolean isEditMode;
    private final ExternalInfluence editingExternalInfluence;
    private final ExternalInfluencesDataModel externalInfluencesDataModel;

    public ExternalInfluenceFormController(ExternalInfluencesDataModel externalInfluencesDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
        this.editingExternalInfluence = externalInfluencesDataModel.getSelectedExternalInfluence();
        this.isEditMode = editingExternalInfluence != null;
    }

    @FXML
    public void initialize() {
        if (isEditMode) {
            populateFormWithExternalInfluenceData();
        }

        nameField.textProperty().addListener(propertyChangeListener());
        descriptionField.textProperty().addListener(propertyChangeListener());
        groupComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());

        JavaFxControls.bindComboBoxDisplay(groupComboBox, ExternalInfluenceGroup::getName);
        groupComboBox.setItems(externalInfluencesDataModel.getExternalInfluenceGroups());
        updateOkButtonState();
    }

    @FXML
    public void showAddGroupForm() {
        DialogOpener.<ExternalInfluenceGroupFormController>open(
                App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_GROUP_FORM,
                "Добавление группы внешних воздействий",
                groupComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                groupComboBox.getSelectionModel().select(
                        externalInfluencesDataModel.getNewExternalInfluenceGroup());
            }
        });
    }

    @FXML
    public void handleOk() {
        if (isFormInvalid()) {
            return;
        }

        if (isEditMode) {
            updateExistingExternalInfluence();
        } else {
            createNewExternalInfluence();
        }
        markSavedAndClose();
    }

    @FXML
    public void handleCancel() {
        markCancelledAndClose();
    }

    private void populateFormWithExternalInfluenceData() {
        nameField.setText(editingExternalInfluence.getName());
        descriptionField.setText(editingExternalInfluence.getDescription());
        groupComboBox.getSelectionModel().select(editingExternalInfluence.getExternalInfluenceGroup());
    }

    private void createNewExternalInfluence() {
        ExternalInfluence externalInfluence = new ExternalInfluence();
        externalInfluence.setName(nameField.getText());
        externalInfluence.setDescription(descriptionField.getText());
        externalInfluence.setExternalInfluenceGroup(groupComboBox.getValue());
        externalInfluencesDataModel.saveExternalInfluence(externalInfluence);
    }

    private void updateExistingExternalInfluence() {
        editingExternalInfluence.setName(nameField.getText());
        editingExternalInfluence.setDescription(descriptionField.getText());
        editingExternalInfluence.setExternalInfluenceGroup(groupComboBox.getValue());
        externalInfluencesDataModel.updateExternalInfluence(editingExternalInfluence);
    }

    private void updateOkButtonState() {
        okButton.setDisable(isFormInvalid());
    }

    private boolean isFormInvalid() {
        return FormValidators.isBlank(nameField.getText());
    }

    private ChangeListener<String> propertyChangeListener() {
        return (obs, oldVal, newVal) -> updateOkButtonState();
    }
}
