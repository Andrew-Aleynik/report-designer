package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

public class ElementQualitiesTabController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElementQualitiesTabController.class);
    private final ElementDataModel elementDataModel;
    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;
    @FXML
    private ComboBox<ElementQuality> elementQualitiesComboBox;
    @FXML
    private TextField codeField;
    @FXML
    private TextField serviceLifeDaysField;
    @FXML
    private TextField satisfyingCostField;
    @FXML
    private TextField actualCostField;
    @FXML
    private TableView<Property> propertiesTableView;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;

    private boolean isSaved = false;

    public ElementQualitiesTabController(ElementDataModel elementDataModel, QualityDataModel qualityDataModel,
                                         PropertyDataModel propertyDataModel) {
        this.elementDataModel = elementDataModel;
        this.qualityDataModel = qualityDataModel;
        this.propertyDataModel = propertyDataModel;
    }

    @FXML
    public void initialize() {
        initializeElementQualitiesComboBox();
        initializePropertiesTableView();
        codeField.textProperty().addListener((obs, oldVal, newVal) -> {
            isSaved = false;
            updateSaveButtonState();
            updateDeleteButtonState();
        });
        serviceLifeDaysField.textProperty().addListener((obs, oldVal, newVal) -> {
            isSaved = false;
            updateSaveButtonState();
            updateDeleteButtonState();
        });
        satisfyingCostField.textProperty().addListener((obs, oldVal, newVal) -> {
            isSaved = false;
            updateSaveButtonState();
            updateDeleteButtonState();
        });
        actualCostField.textProperty().addListener((obs, oldVal, newVal) -> {
            isSaved = false;
            updateSaveButtonState();
            updateDeleteButtonState();
        });
        updateSaveButtonState();
        updateDeleteButtonState();
    }

    private void initializeElementQualitiesComboBox() {
        elementQualitiesComboBox.setCellFactory(param -> new ListCell<>() {
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

        elementQualitiesComboBox.setButtonCell(new ListCell<>() {
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

        elementQualitiesComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                qualityDataModel.refreshSelectedQuality(newVal);
                propertyDataModel.refreshCurrentProperties(qualityDataModel.getSelectedQuality().getProperties());
                populateFormWithQualityData();
            }
            isSaved = false;
            updateSaveButtonState();
            updateDeleteButtonState();
        });

        elementQualitiesComboBox.setItems(qualityDataModel.getQualities());
    }

    private void initializePropertiesTableView() {
        TableColumn<Property, String> unitColumn = new TableColumn<>("Единица измерения");
        unitColumn.setCellValueFactory(cellData -> {
            Property property = cellData.getValue();
            if (property.getUnit() != null) {
                return new SimpleStringProperty(property.getUnit().getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        unitColumn.setCellFactory(column -> new TableCell<Property, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        TableColumn<Property, String> currentValueColumn = new TableColumn<>("Показатель потребительского качества");
        currentValueColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCurrentValue()));
        currentValueColumn.setCellFactory(column -> new TableCell<Property, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });


        TableColumn<Property, String> criterionValueColumn = new TableColumn<>("Критерий потребительского качества");
        criterionValueColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getQualityCriterionValue()));
        criterionValueColumn.setCellFactory(column -> new TableCell<Property, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });

        TableColumn<Property, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");

            {
                editButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5;");
                deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5; -fx-text-fill: red;");

                editButton.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    handleEditProperty(property);
                    isSaved = false;
                    updateSaveButtonState();
                    updateDeleteButtonState();
                });

                deleteButton.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    handleDeleteProperty(property);
                    isSaved = false;
                    updateSaveButtonState();
                    updateDeleteButtonState();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        unitColumn.setPrefWidth(150);
        currentValueColumn.setPrefWidth(150);
        criterionValueColumn.setPrefWidth(150);
        actionsColumn.setPrefWidth(100);

        propertiesTableView.getColumns().clear();
        propertiesTableView.getColumns().addAll(unitColumn, currentValueColumn, criterionValueColumn, actionsColumn);
        propertiesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        propertiesTableView.setItems(propertyDataModel.getCurrentProperties());
    }

    private void populateFormWithQualityData() {
        ElementQuality quality = qualityDataModel.getSelectedQuality();
        if (quality != null) {
            codeField.setText(Optional.ofNullable(quality.getCode()).orElse(""));

            serviceLifeDaysField.setText(
                    Optional.ofNullable(quality.getServiceLife())
                            .map(duration -> Long.toString(duration.toDays()))
                            .orElse("")
            );

            satisfyingCostField.setText(
                    Optional.ofNullable(quality.getSatisfyingCost())
                            .map(BigDecimal::toString)
                            .orElse("")
            );

            actualCostField.setText(
                    Optional.ofNullable(quality.getActualCost())
                            .map(BigDecimal::toString)
                            .orElse("")
            );
        } else {
            codeField.setText("");
            serviceLifeDaysField.setText("");
            satisfyingCostField.setText("");
            actualCostField.setText("");
        }
    }

    private void updateSaveButtonState() {
        boolean isNotValid = validateForm();
        saveButton.setDisable(isNotValid || isSaved);
    }

    private void updateDeleteButtonState() {
        deleteButton.setDisable(elementQualitiesComboBox.getValue() == null || !isSaved);
    }

    @FXML
    public void handleCreateElementQuality() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_ELEMENT_QUALITY_SHORT_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ElementQualityFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление потребительского качества");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(elementQualitiesComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                elementQualitiesComboBox.getSelectionModel().select(qualityDataModel.getNewQuality());
                isSaved = true;
                updateSaveButtonState();
                updateDeleteButtonState();
            }
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    @FXML
    public void handleUpdateQuality() {
        boolean isValid = !validateForm();
        if (isValid) {
            ElementQuality quality = qualityDataModel.getSelectedQuality();
            quality.setCode(codeField.getText());
            try {
                quality.setServiceLife(Duration.ofDays(Long.parseLong(serviceLifeDaysField.getText())));
            } catch (NumberFormatException e) {
                quality.setServiceLife(null);
            }
            try {
                quality.setSatisfyingCost(BigDecimal.valueOf(Float.parseFloat(satisfyingCostField.getText())));
            } catch (NumberFormatException e) {
                quality.setSatisfyingCost(null);
            }
            try {
                quality.setActualCost(BigDecimal.valueOf(Float.parseFloat(actualCostField.getText())));
            } catch (NumberFormatException e) {
                quality.setActualCost(null);
            }
            quality.setProperties(propertiesTableView.getItems());
            qualityDataModel.updateQuality(quality);
            qualityDataModel.refreshSelectedQuality(quality);
            elementDataModel.refreshRootElements();
            isSaved = true;
            updateSaveButtonState();
            updateDeleteButtonState();
        }
    }

    @FXML
    public void handleDeleteQuality() {
        qualityDataModel.deleteQuality(elementQualitiesComboBox.getValue());
        qualityDataModel.refreshQualities();
        qualityDataModel.refreshSelectedQuality(null);
        populateFormWithQualityData();
        propertyDataModel.refreshCurrentProperties(Collections.emptyList());
        isSaved = true;
        updateSaveButtonState();
        updateDeleteButtonState();
    }

    @FXML
    public void showAddPropertyForm() {
        try {
            qualityDataModel.refreshSelectedQuality(elementQualitiesComboBox.getValue());
            propertyDataModel.refreshEditingProperty(null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_PROPERTY_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            PropertyFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление потребительского свойства");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(propertiesTableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            isSaved = false;
            updateSaveButtonState();
            updateDeleteButtonState();
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void handleEditProperty(Property property) {
        try {
            qualityDataModel.refreshSelectedQuality(elementQualitiesComboBox.getValue());
            propertyDataModel.refreshEditingProperty(property);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_PROPERTY_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            PropertyFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Редактирование потребительского свойства");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(propertiesTableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void handleDeleteProperty(Property property) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление свойства");
        alert.setContentText("Вы уверены, что хотите удалить это свойство?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ElementQuality currentQuality = elementQualitiesComboBox.getValue();
                if (currentQuality != null) {
                    currentQuality.removeProperty(property);
                    propertyDataModel.deleteProperty(property);
                }
            }
        });
    }

    private boolean validateForm() {
        return isCodeInvalid() ||
                isServiceLifeInvalid() ||
                isSatisfyingCostInvalid() ||
                isActualCostInvalid();
    }

    private boolean isCodeInvalid() {
        String code = codeField.getText();
        return code == null || code.trim().isEmpty() || code.trim().length() < 3;
    }

    private boolean isServiceLifeInvalid() {
        String serviceLife = serviceLifeDaysField.getText();
        if (serviceLife == null || serviceLife.trim().isEmpty()) {
            return false;
        }
        try {
            long days = Long.parseLong(serviceLife.trim());
            return days < 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isSatisfyingCostInvalid() {
        String cost = satisfyingCostField.getText();
        if (cost == null || cost.trim().isEmpty()) {
            return false;
        }
        try {
            BigDecimal amount = new BigDecimal(cost.trim());
            return amount.compareTo(BigDecimal.ZERO) < 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private boolean isActualCostInvalid() {
        String cost = actualCostField.getText();
        if (cost == null || cost.trim().isEmpty()) {
            return false;
        }
        try {
            BigDecimal amount = new BigDecimal(cost.trim());
            return amount.compareTo(BigDecimal.ZERO) < 0;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
