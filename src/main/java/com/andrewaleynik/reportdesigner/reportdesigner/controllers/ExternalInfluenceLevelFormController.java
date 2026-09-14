package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExternalInfluenceLevelFormController extends AbstractDialogController {

    private final ExternalInfluencesDataModel externalInfluencesDataModel;
    private final PropertyDataModel propertyDataModel;

    @FXML
    private ComboBox<PropertyGroup> propertyGroupComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private Button okButton;

    public ExternalInfluenceLevelFormController(ExternalInfluencesDataModel externalInfluencesDataModel,
                                                PropertyDataModel propertyDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
        this.propertyDataModel = propertyDataModel;
    }

    @FXML
    public void initialize() {
        JavaFxControls.bindComboBoxDisplay(propertyGroupComboBox, PropertyGroup::getName);
        propertyGroupComboBox.setItems(propertyDataModel.getPropertyGroups());

        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        propertyGroupComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        updateOkButtonState();
    }

    @FXML
    public void handleOk() {
        if (isFormInvalid()) {
            return;
        }

        ExternalInfluenceLevel externalInfluenceLevel = new ExternalInfluenceLevel();
        externalInfluenceLevel.setName(nameField.getText());
        externalInfluenceLevel.setPropertyGroup(propertyGroupComboBox.getValue());
        externalInfluencesDataModel.saveExternalInfluenceLevel(externalInfluenceLevel);
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
        return FormValidators.isBlank(nameField.getText())
                || propertyGroupComboBox.getValue() == null;
    }
}
