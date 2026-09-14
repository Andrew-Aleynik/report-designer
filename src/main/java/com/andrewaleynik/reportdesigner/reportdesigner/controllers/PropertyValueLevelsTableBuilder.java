package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;

public final class PropertyValueLevelsTableBuilder {

    private PropertyValueLevelsTableBuilder() {
    }

    public static void configure(TableView<PropertyValueDomain> tableView,
                                 List<PropertyValueDomain> rows,
                                 List<ExternalInfluenceLevel> levels,
                                 ObservableList<ExternalInfluence> influences,
                                 Consumer<PropertyValueDomain> onSave) {
        tableView.getColumns().clear();

        TableColumn<PropertyValueDomain, String> propertyColumn = JavaFxControls.textColumn(
                "Свойство", PropertyValueDomain::getPropertyDisplay);
        TableColumn<PropertyValueDomain, ExternalInfluence> influenceColumn =
                createInfluenceColumn(influences);

        propertyColumn.setPrefWidth(150);
        influenceColumn.setPrefWidth(150);

        tableView.getColumns().add(propertyColumn);
        tableView.getColumns().add(influenceColumn);

        for (ExternalInfluenceLevel level : levels) {
            tableView.getColumns().add(createLevelColumn(level, onSave));
        }

        tableView.getItems().setAll(rows);
        tableView.setEditable(true);
    }

    private static TableColumn<PropertyValueDomain, ExternalInfluence> createInfluenceColumn(
            ObservableList<ExternalInfluence> influences) {
        TableColumn<PropertyValueDomain, ExternalInfluence> influenceColumn =
                new TableColumn<>("Внешнее воздействие");

        influenceColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getExternalInfluence()));

        influenceColumn.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final ComboBox<ExternalInfluence> comboBox = new ComboBox<>(influences);

            {
                comboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(ExternalInfluence influence) {
                        return influence != null ? influence.getName() : "";
                    }

                    @Override
                    public ExternalInfluence fromString(String value) {
                        return null;
                    }
                });

                comboBox.setOnAction(event -> {
                    PropertyValueDomain row = getTableRow().getItem();
                    if (row != null) {
                        row.setExternalInfluence(comboBox.getValue());
                    }
                });
            }

            @Override
            protected void updateItem(ExternalInfluence item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : comboBox);
                if (!empty) {
                    comboBox.setValue(item);
                }
            }
        });

        return influenceColumn;
    }

    private static TableColumn<PropertyValueDomain, String> createLevelColumn(
            ExternalInfluenceLevel level,
            Consumer<PropertyValueDomain> onSave) {
        TableColumn<PropertyValueDomain, String> levelColumn = new TableColumn<>(level.getName());
        levelColumn.setUserData(level);
        levelColumn.setEditable(true);
        levelColumn.setPrefWidth(100);
        levelColumn.setCellValueFactory(cellData -> {
            String value = cellData.getValue().getLevelPair(level).value();
            return new SimpleStringProperty(value != null ? value : "");
        });
        levelColumn.setCellFactory(column -> new EditableLevelCell(onSave));
        return levelColumn;
    }

    private static final class EditableLevelCell extends javafx.scene.control.TableCell<PropertyValueDomain, String> {
        private final TextField textField = new TextField();
        private final Consumer<PropertyValueDomain> onSave;

        private EditableLevelCell(Consumer<PropertyValueDomain> onSave) {
            this.onSave = onSave;
            textField.setOnAction(event -> {
                if (isEditing()) {
                    commitEdit(textField.getText());
                }
            });
            textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if (Boolean.FALSE.equals(newValue) && isEditing()) {
                    commitEdit(textField.getText());
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);
                setGraphic(null);
            }
        }

        @Override
        public void startEdit() {
            if (getTableRow() == null || getTableRow().getItem() == null) {
                return;
            }

            PropertyValueDomain row = getTableView().getItems().get(getIndex());
            if (row.getExternalInfluence() == null) {
                AlertFactory.showConfirmation("Сообщение", "",
                        "Сначала укажите внешнее воздействие");
                return;
            }

            if (!isEmpty()) {
                super.startEdit();
                textField.setText(getItem() != null ? getItem() : "");
                setText(null);
                setGraphic(textField);
                textField.requestFocus();
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setGraphic(null);
            setText(getItem());
        }

        @Override
        public void commitEdit(String newValue) {
            if (getTableRow() == null || getTableRow().getItem() == null) {
                return;
            }

            PropertyValueDomain row = getTableView().getItems().get(getIndex());
            ExternalInfluenceLevel level = (ExternalInfluenceLevel) getTableColumn().getUserData();
            row.setLevelPairValue(level, newValue);
            onSave.accept(row);

            super.commitEdit(newValue);
        }
    }
}
