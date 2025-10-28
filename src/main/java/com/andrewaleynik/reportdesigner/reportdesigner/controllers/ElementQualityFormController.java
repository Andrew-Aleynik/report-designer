package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ElementQualityFormController {
    ElementFormController elementFormController;

    private ElementQualityService elementQualityService;

    @FXML
    private TextField codeField;


    public void setElementFormController(ElementFormController elementFormController) {
        this.elementFormController = elementFormController;
    }

    public void setElementQualityService(ElementQualityService elementQualityService) {
        this.elementQualityService = elementQualityService;
    }

    public boolean handleOk() {
        if (validateForm()) {
            ElementQuality elementQuality = new ElementQuality();
            elementQuality.setCode(codeField.getText());
            elementQualityService.saveQuality(elementQuality);
            return true;
        }

        return false;
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (codeField.getText() == null || codeField.getText().trim().isEmpty()) {
            errors.append("• Код не может быть пустым\n");
        } else if (codeField.getText().trim().length() < 3) {
            errors.append("• Код должен содержать минимум 3 символа\n");
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
}
