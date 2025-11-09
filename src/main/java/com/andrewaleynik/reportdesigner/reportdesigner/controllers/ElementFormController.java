package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
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

public class ElementFormController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElementFormController.class);
    private final ElementDataModel elementDataModel;
    private final QualityDataModel qualityDataModel;
    private boolean isEditMode = false;

    private final Element editingElement;

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
        this.editingElement = elementDataModel.getSelectedEditElement();
        if (editingElement != null) {
            this.isEditMode = true;
        }
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();

        if (isEditMode && editingElement != null) {
            populateFormWithElementData();
        } else {
            Element parentElement = elementDataModel.getSelectedParentElement();
            if (parentElement != null) {
                parentElementLabel.setText(parentElement.getCode() + " - " + parentElement.getName());
            }
        }

        codeField.textProperty().addListener(propertyChangeListener());
        typeComboBox.valueProperty().addListener(propertyChangeListener());
        nameField.textProperty().addListener(propertyChangeListener());

        updateOkButtonState();
    }

    private void populateFormWithElementData() {
        if (editingElement != null) {
            codeField.setText(editingElement.getCode());
            typeComboBox.setValue(editingElement.getType());
            nameField.setText(editingElement.getName());
            descriptionField.setText(editingElement.getDescription());
            qualityComboBox.setValue(editingElement.getQuality());

            Element parentElement = editingElement.getParent();
            if (parentElement != null) {
                parentElementLabel.setText(parentElement.getCode() + " - " + parentElement.getName());
            } else {
                parentElementLabel.setText("Текущий элемент - корневой");
            }
        }
    }

    private void updateOkButtonState() {
        boolean isNotValid = validateForm();
        okButton.setDisable(isNotValid);
    }

    @FXML
    public void handleOk() {
        boolean isValid = !validateForm();
        if (isValid) {
            if (isEditMode && editingElement != null) {
                updateExistingElement();
            } else {
                createNewElement();
            }

            saved = true;
            closeDialog();
        }
    }

    private void createNewElement() {
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
    }

    private void updateExistingElement() {
        editingElement.setCode(codeField.getText().trim());
        editingElement.setType(typeComboBox.getValue());
        editingElement.setName(nameField.getText().trim());
        editingElement.setDescription(descriptionField.getText().trim());
        editingElement.setQuality(qualityComboBox.getValue());

        elementDataModel.updateElement(editingElement);
    }

    @FXML
    public void handleCancel() {
        saved = false;
        closeDialog();
    }

    @FXML
    private void handleCreateTypeButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_ELEMENT_TYPE_FORM));
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
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    @FXML
    private void handleCreateQualityButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_ELEMENT_QUALITY_SHORT_FORM));
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
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
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

    public boolean isEditMode() {
        return isEditMode;
    }
}