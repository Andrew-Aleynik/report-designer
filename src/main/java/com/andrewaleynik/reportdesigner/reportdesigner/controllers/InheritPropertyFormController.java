package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class InheritPropertyFormController {
    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;
    @FXML
    private Label parentQualityLabel;
    @FXML
    private TableView<Property> parentPropertiesTableView;
    @FXML
    private Button okButton;
    private boolean saved = false;
    private Stage dialogStage;

    private class ToggleButton extends Button {
        private final String[] stateNames;
        private final int statesCount;
        private int currentState = 0;

        public ToggleButton(String... stateNames) {
            super(stateNames[0]);
            this.stateNames = stateNames;
            statesCount = stateNames.length;
        }

        public void toggle() {
            currentState++;
            currentState %= statesCount;
            this.textProperty().set(stateNames[currentState]);
        }

        public int getCurrentState() {
            return currentState;
        }
    }

    public InheritPropertyFormController(QualityDataModel qualityDataModel,
                                         PropertyDataModel propertyDataModel) {
        this.qualityDataModel = qualityDataModel;
        this.propertyDataModel = propertyDataModel;
    }

    @FXML
    public void initialize() {
        initializeParentPropertiesTableView();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    public void handleOk() {
        saved = true;
        addInheritedProperties();
        propertyDataModel.clearInheritedProperties();
        closeDialog();
    }

    @FXML
    public void handleCancel() {
        saved = false;
        closeDialog();
    }

    public boolean isSaved() {
        return saved;
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void initializeParentPropertiesTableView() {
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
            private final ToggleButton inheritCancelButton = new ToggleButton(
                    "Наследовать", "Отменить"
            );

            {
                inheritCancelButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5;");
                inheritCancelButton.setOnAction(event -> {
                    Property property = getTableView().getItems().get(getIndex());
                    if (inheritCancelButton.getCurrentState() == 0) {
                        propertyDataModel.addInheritedProperty(property);
                    } else {
                        propertyDataModel.removeInheritedProperty(property);
                    }
                    inheritCancelButton.toggle();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, inheritCancelButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        unitColumn.setPrefWidth(150);
        currentValueColumn.setPrefWidth(150);
        criterionValueColumn.setPrefWidth(150);
        actionsColumn.setPrefWidth(100);

        parentPropertiesTableView.getColumns().clear();
        parentPropertiesTableView.getColumns().addAll(unitColumn, currentValueColumn, criterionValueColumn, actionsColumn);
        parentPropertiesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        propertyDataModel.refreshParentProperties(qualityDataModel.getSelectedQuality());
        parentPropertiesTableView.setItems(propertyDataModel.getParentProperties());
    }

    private void addInheritedProperties() {
        ElementQuality currentQuality = qualityDataModel.getSelectedQuality();
        propertyDataModel.getInheritedProperties().forEach(currentQuality::addProperty);
    }
}
