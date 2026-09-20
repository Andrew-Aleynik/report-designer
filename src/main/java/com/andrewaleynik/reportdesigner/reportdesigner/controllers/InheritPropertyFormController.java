package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class InheritPropertyFormController extends AbstractDialogController {

    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;
    @FXML
    private TableView<Property> parentPropertiesTableView;

    private static class ToggleButton extends Button {
        private final String[] stateNames;
        private final int statesCount;
        private int currentState = 0;

        public ToggleButton(String... stateNames) {
            super(stateNames[0]);
            this.stateNames = stateNames;
            statesCount = stateNames.length;
        }

        public void toggle() {
            currentState = (currentState + 1) % statesCount;
            setText(stateNames[currentState]);
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

    @FXML
    public void handleOk() {
        addInheritedProperties();
        propertyDataModel.clearInheritedProperties();
        markSavedAndClose();
    }

    @FXML
    public void handleCancel() {
        markCancelledAndClose();
    }

    private void initializeParentPropertiesTableView() {
        TableColumn<Property, String> unitColumn = JavaFxControls.textColumn("Единица измерения",
                property -> property.getUnit() != null ? property.getUnit().getName() : "");
        TableColumn<Property, String> criterionValueColumn = JavaFxControls.textColumn(
                "Критерий качества", Property::getQualityCriterionValue);
        TableColumn<Property, Void> actionsColumn = createInheritActionsColumn();

        unitColumn.setPrefWidth(150);
        criterionValueColumn.setPrefWidth(150);
        actionsColumn.setPrefWidth(100);

        parentPropertiesTableView.getColumns().setAll(unitColumn, criterionValueColumn, actionsColumn);
        JavaFxControls.configureConstrainedTable(parentPropertiesTableView);
        propertyDataModel.refreshParentProperties(qualityDataModel.getSelectedQuality());
        parentPropertiesTableView.setItems(propertyDataModel.getParentProperties());
    }

    private TableColumn<Property, Void> createInheritActionsColumn() {
        TableColumn<Property, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final ToggleButton inheritCancelButton = new ToggleButton("Наследовать", "Отменить");

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
        return actionsColumn;
    }

    private void addInheritedProperties() {
        ElementQuality currentQuality = qualityDataModel.getSelectedQuality();
        propertyDataModel.getInheritedProperties().forEach(currentQuality::addProperty);
    }
}
