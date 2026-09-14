package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.util.FormValidators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ElementTypeFormController extends AbstractDialogController {

    private final ElementDataModel elementDataModel;

    @FXML
    private TextField nameField;
    @FXML
    private Button okButton;

    public ElementTypeFormController(ElementDataModel elementDataModel) {
        this.elementDataModel = elementDataModel;
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

        ElementType elementType = new ElementType();
        elementType.setName(nameField.getText());
        elementDataModel.saveElementType(elementType);
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
