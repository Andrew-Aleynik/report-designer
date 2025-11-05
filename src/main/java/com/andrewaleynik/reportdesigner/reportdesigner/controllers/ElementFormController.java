package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ElementFormController {
    private ElementDataModel elementDataModel;
    private QualityDataModel qualityDataModel;

    @FXML
    private Label parentElementLabel;

    @FXML
    private TextField codeField;

    @FXML
    private ComboBox<ElementType> typeComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<ElementQuality> qualityComboBox;
    @FXML
    private Button okButton;
    private boolean saved = false;
    private Stage dialogStage;

    public ElementFormController(ElementDataModel elementDataModel, QualityDataModel qualityDataModel) {
        this.elementDataModel = elementDataModel;
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();
        Element parentElement = elementDataModel.getSelectedParentElement();
        if (parentElement != null) {
            parentElementLabel.setText(parentElement.getCode() + " - " + parentElement.getName());
        }

        codeField.textProperty().addListener(propertyChangeListener());
        typeComboBox.placeholderProperty().addListener(propertyChangeListener());
        nameField.textProperty().addListener(propertyChangeListener());

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
            Element element = new Element();
            element.setCode(codeField.getText().trim());
            element.setType(typeComboBox.getValue());
            element.setName(nameField.getText().trim());
            element.setDescription(descriptionField.getText().trim());
            element.setQuality(qualityComboBox.getValue());

            if (elementDataModel.getSelectedParentElement() != null) {
                elementDataModel.getSelectedParentElement().addChild(element);
                element.setLevel(elementDataModel.getSelectedParentElement().getLevel() + 1);
            } else {
                element.setLevel(0);
            }

            elementDataModel.saveElement(element);
            saved = true;
            closeDialog();
        }
    }

    @FXML
    public void handleCancel() {
        saved = false;
        closeDialog();
    }

    @FXML
    private void handleCreateTypeButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_ELEMENT_TYPE_FORM_PATH));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ElementTypeFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление нового типа");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(typeComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                typeComboBox.getSelectionModel().select(elementDataModel.getNewElementType());
            }
        } catch (IOException e) {
            System.out.format("Ошибка при открытии формы: %s", e.getMessage());
        }
    }

    @FXML
    private void handleCreateQualityButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_ELEMENT_QUALITY_SHORT_FORM_PATH));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ElementQualityFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление нового качества");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(qualityComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                qualityComboBox.getSelectionModel().select(qualityDataModel.getNewQuality());
            }
        } catch (IOException e) {
            System.out.format("Ошибка при открытии формы: %s", e.getMessage());
        }
    }

    private boolean validateForm() {
        String code = codeField.getText();
        ElementType type = typeComboBox.getValue();
        String name = nameField.getText();

        return code == null || code.trim().isEmpty() || code.trim().length() < 3 ||
                type == null ||
                name == null || name.trim().isEmpty();
    }

    private void initializeComboBoxes() {
        typeComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ElementType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        typeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ElementType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        typeComboBox.setItems(elementDataModel.getElementTypes());

        qualityComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ElementQuality item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode());
                }
            }
        });

        qualityComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ElementQuality item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode());
                }
            }
        });

        qualityComboBox.setItems(qualityDataModel.getQualities());
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

    private <T> ChangeListener<? super T> propertyChangeListener() {
        return (obs, oldVal, newVal) -> updateOkButtonState();
    }
}