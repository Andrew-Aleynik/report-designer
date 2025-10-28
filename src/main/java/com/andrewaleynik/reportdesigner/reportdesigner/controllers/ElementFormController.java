package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ElementFormController {
    private MainController mainController;
    private ElementService elementService;
    private ElementQualityService elementQualityService;
    private Element parentElement;

    @FXML
    private Label parentElementLabel;

    @FXML
    private TextField codeField;

    @FXML
    private ComboBox<ElementType> typeComboBox;

    @FXML
    private Button createTypeButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<ElementQuality> qualityComboBox;

    @FXML
    private Button createQualityButton;

    public void setElementQualityService(ElementQualityService elementQualityService) {
        this.elementQualityService = elementQualityService;
    }

    public void setElementService(ElementService elementService) {
        this.elementService = elementService;
        initializeComboBoxes();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        initializeComboBoxes();
    }

    public void setParentElement(Element parentElement) {
        this.parentElement = parentElement;
        if (parentElementLabel != null && parentElement != null) {
            parentElementLabel.setText(parentElement.getCode() + " - " + parentElement.getName());
        }
    }

    @FXML
    public void initialize() {
        setupValidation();
    }

    public void initializeComboBoxes() {
        List<ElementType> elementTypes = elementService.getAllElementTypes();
        typeComboBox.getItems().setAll(elementTypes);

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

        List<ElementQuality> elementQualities = elementService.getAllElementQualities();
        qualityComboBox.getItems().setAll(elementQualities);

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
    }

    private void setupValidation() {
        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Zа-яА-Я0-9_]*")) {
                codeField.setText(oldValue);
            }
        });
    }

    @FXML
    public boolean handleOk() {
        if (validateForm()) {
            try {
                Element element = new Element();
                element.setCode(codeField.getText().trim());
                element.setType(typeComboBox.getValue());
                element.setName(nameField.getText().trim());
                element.setDescription(descriptionField.getText().trim());
                element.setQuality(qualityComboBox.getValue());

                if (parentElement != null) {
                    parentElement.addChild(element);
                    element.setLevel(parentElement.getLevel() + 1);
                } else {
                    element.setLevel(0);
                }

                elementService.saveElement(element);

                return true;

            } catch (Exception e) {
                showError("Ошибка сохранения", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @FXML
    private void handleCancel() {
    }

    @FXML
    private void handleCreateTypeButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_ELEMENT_TYPE_FORM_PATH));
            DialogPane dialogPane = loader.load();

            ElementTypeFormController controller = loader.getController();
            controller.setElementService(App.getElementService());
            controller.setElementFormController(this);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Добавление нового типа");

            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setOnAction(event -> {
                if (controller.handleOk()) {
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                }
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                initializeComboBoxes();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    @FXML
    private void handleCreateQualityButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.ADD_ELEMENT_QUALITY_SHORT_FORM_PATH));
            DialogPane dialogPane = loader.load();

            ElementQualityFormController controller = loader.getController();
            controller.setElementQualityService(App.getElementQualityService());
            controller.setElementFormController(this);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Добавление нового качества");

            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setOnAction(event -> {
                if (controller.handleOk()) {
                    dialog.setResult(ButtonType.OK);
                    dialog.close();
                }
            });

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                initializeComboBoxes();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (codeField.getText() == null || codeField.getText().trim().isEmpty()) {
            errors.append("• Код не может быть пустым\n");
        } else if (codeField.getText().trim().length() < 3) {
            errors.append("• Код должен содержать минимум 3 символа\n");
        }

        if (typeComboBox.getValue() == null) {
            errors.append("• Необходимо выбрать тип элемента\n");
        }

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
}