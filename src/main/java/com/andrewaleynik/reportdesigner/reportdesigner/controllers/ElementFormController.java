package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ElementFormController extends AbstractDialogController {

    private final ElementDataModel elementDataModel;
    private final QualityDataModel qualityDataModel;
    private final boolean isEditMode;
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

    public ElementFormController(ElementDataModel elementDataModel, QualityDataModel qualityDataModel) {
        this.elementDataModel = elementDataModel;
        this.qualityDataModel = qualityDataModel;
        this.editingElement = elementDataModel.getSelectedEditElement();
        this.isEditMode = editingElement != null;
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();

        if (isEditMode) {
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

    @FXML
    public void handleOk() {
        if (isFormInvalid()) {
            return;
        }

        if (isEditMode) {
            updateExistingElement();
        } else {
            createNewElement();
        }

        markSavedAndClose();
    }

    @FXML
    public void handleCancel() {
        markCancelledAndClose();
    }

    @FXML
    private void handleCreateTypeButton() {
        DialogOpener.<ElementTypeFormController>open(
                App.FxmlPaths.ADD_ELEMENT_TYPE_FORM,
                "Добавление нового типа",
                typeComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                typeComboBox.getSelectionModel().select(elementDataModel.getNewElementType());
            }
        });
    }

    @FXML
    private void handleCreateQualityButton() {
        DialogOpener.<ElementQualityFormController>open(
                App.FxmlPaths.ADD_ELEMENT_QUALITY_SHORT_FORM,
                "Добавление элемента структурной модели",
                qualityComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                qualityComboBox.getSelectionModel().select(qualityDataModel.getNewQuality());
            }
        });
    }

    private void populateFormWithElementData() {
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

    private void initializeComboBoxes() {
        JavaFxControls.bindComboBoxDisplay(typeComboBox, ElementType::getName);
        typeComboBox.setItems(elementDataModel.getElementTypes());

        JavaFxControls.bindComboBoxDisplay(qualityComboBox, ElementQuality::getCode);
        qualityComboBox.setItems(qualityDataModel.getQualities());
    }

    private void updateOkButtonState() {
        okButton.setDisable(isFormInvalid());
    }

    private boolean isFormInvalid() {
        return FormValidators.isBlankOrTooShort(codeField.getText(), 3)
                || typeComboBox.getValue() == null
                || FormValidators.isBlank(nameField.getText());
    }

    private <T> ChangeListener<T> propertyChangeListener() {
        return (obs, oldVal, newVal) -> updateOkButtonState();
    }
}
