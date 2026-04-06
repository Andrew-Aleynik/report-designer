package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ExternalInfluenceFormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalInfluenceFormController.class);
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<ExternalInfluenceGroup> groupComboBox;
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
        groupComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateOkButtonState());
        groupComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ExternalInfluenceGroup item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        groupComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ExternalInfluenceGroup item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        groupComboBox.setItems(externalInfluencesDataModel.getExternalInfluenceGroups());

        updateOkButtonState();
    }

    @FXML
    public void showAddGroupForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_GROUP_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ExternalInfluenceGroupFormController controller = loader.getController();

            Stage dialogStageChild = new Stage();
            dialogStageChild.setTitle("Добавление группы внешних воздействий");
            dialogStageChild.initModality(Modality.APPLICATION_MODAL);
            dialogStageChild.initOwner(groupComboBox.getScene().getWindow());
            dialogStageChild.setScene(new Scene(root));
            dialogStageChild.setResizable(false);

            controller.setDialogStage(dialogStageChild);

            dialogStageChild.showAndWait();

            if (controller.isSaved()) {
                groupComboBox.getSelectionModel().select(externalInfluencesDataModel.getNewExternalInfluenceGroup());
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
        boolean isNotValid = validateForm();
        okButton.setDisable(isNotValid);
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
