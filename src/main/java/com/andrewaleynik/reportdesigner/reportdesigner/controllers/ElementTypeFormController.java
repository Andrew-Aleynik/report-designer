package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ElementTypeFormController implements FormController {
    private Controller parentController;

    private ElementService elementService;

    @FXML
    private TextField nameField;

    private ElementType newElementType;

    public ElementTypeFormController(ElementService elementService) {
        this.elementService = elementService;
    }

    @Override
    public void setParentController(Controller controller) {
        this.parentController = controller;
    }

    public boolean handleOk() {
        if (validateForm()) {
            ElementType elementType = new ElementType();
            elementType.setName(nameField.getText());
            elementService.saveElementType(elementType);
            newElementType = elementType;
            return true;
        }

        return false;
    }

    public ElementType getNewElementType() {
        return newElementType;
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.append("• Название не может быть пустым\n");
        }

        if (errors.length() > 0) {
            showError("Ошибка валидации:\n", errors.toString());
            return false;
        }

        return true;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void updateViews() {
        // Nothing
    }
}
