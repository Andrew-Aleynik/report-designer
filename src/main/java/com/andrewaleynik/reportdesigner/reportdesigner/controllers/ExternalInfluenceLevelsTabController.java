package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ExternalInfluenceLevelsTabController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalInfluenceLevelsTabController.class);
    private final ExternalInfluencesDataModel externalInfluencesDataModel;
    private final PropertyDataModel propertyDataModel;
    private final QualityDataModel qualityDataModel;

    public ExternalInfluenceLevelsTabController(ExternalInfluencesDataModel externalInfluencesDataModel,
                                                PropertyDataModel propertyDataModel,
                                                QualityDataModel qualityDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
        this.propertyDataModel = propertyDataModel;
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public ComboBox<ElementQuality> qualitiesComboBox;
    @FXML
    public TableView<PropertyValueDomain> externalInfluenceLevelsTableView;

    @FXML
    public void initialize() {
        qualitiesComboBox.valueProperty().addListener(l -> updateTable());
        qualitiesComboBox.setCellFactory(param -> new ListCell<>() {
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
        qualitiesComboBox.setButtonCell(new ListCell<>() {
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
        qualitiesComboBox.setItems(qualityDataModel.getQualities());
        updateTable();
    }

    @FXML
    public void showAddExternalInfluenceLevelForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_LEVEL_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ExternalInfluenceLevelFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление интенсивности внешних воздействий");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(qualitiesComboBox.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                updateTable();
            }
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void updateTable() {
        externalInfluenceLevelsTableView.getItems().clear();
        externalInfluenceLevelsTableView.getColumns().clear();

        ElementQuality selectedQuality = qualitiesComboBox.getSelectionModel().getSelectedItem();
        if (selectedQuality == null) return;

        List<ExternalInfluenceLevel> levels = externalInfluencesDataModel.getExternalInfluenceLevels();
        List<PropertyValue> propertyValues = propertyDataModel.getPropertyValuesOfQuality(selectedQuality);
        ObservableList<ExternalInfluence> influences = externalInfluencesDataModel.getExternalInfluences();

        // Создаём доменные объекты
        List<PropertyValueDomain> rows = selectedQuality.getProperties().stream()
                .map(property -> {
                    PropertyValueDomain row = new PropertyValueDomain(property);

                    // Заполняем из PropertyValue
                    propertyValues.stream()
                            .filter(pv -> pv.getProperty().equals(property))
                            .forEach(pv -> {
                                row.setExternalInfluence(pv.getExternalInfluence());
                                if (pv.getExternalInfluenceLevel() != null && pv.getValue() != null) {
                                    row.setLevelPair(pv.getExternalInfluenceLevel(), pv.getId(), pv.getValue());
                                }
                            });

                    return row;
                })
                .toList();

        // Колонка Property
        TableColumn<PropertyValueDomain, String> propertyColumn = new TableColumn<>("Свойство");
        propertyColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getPropertyDisplay())
        );

        // Колонка ExternalInfluence (ComboBox)
        TableColumn<PropertyValueDomain, ExternalInfluence> influenceColumn =
                new TableColumn<>("Внешнее воздействие");

        influenceColumn.setCellValueFactory(cd ->
                new SimpleObjectProperty<>(cd.getValue().getExternalInfluence())
        );

        influenceColumn.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<ExternalInfluence> comboBox = new ComboBox<>(influences);

            {
                comboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(ExternalInfluence i) {
                        return i != null ? i.getName() : "";
                    }

                    @Override
                    public ExternalInfluence fromString(String s) {
                        return null;
                    }
                });

                comboBox.setOnAction(e -> {
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
                if (!empty) comboBox.setValue(item);
            }
        });

        externalInfluenceLevelsTableView.getColumns().clear();
        for (ExternalInfluenceLevel level : levels) {
            TableColumn<PropertyValueDomain, String> levelColumn = new TableColumn<>(level.getName());
            levelColumn.setUserData(level);

            levelColumn.setEditable(true);

            levelColumn.setCellValueFactory(cd -> {
                String val = cd.getValue().getLevelPair(level).value();
                return new SimpleStringProperty(val != null ? val : "");
            });

            levelColumn.setCellFactory(col -> new TableCell<PropertyValueDomain, String>() {
                private final TextField textField = new TextField();

                {
                    textField.setOnAction(e -> {
                        if (isEditing()) {
                            commitEdit(textField.getText());
                        }
                    });

                    textField.focusedProperty().addListener((obs, old, neu) -> {
                        if (Boolean.FALSE.equals(neu) && isEditing()) {
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
                        // ✅ Показываем текст вне режима редактирования
                        setText(item);
                        setGraphic(null);
                    }
                }

                @Override
                public void startEdit() {
                    if (getTableRow() == null || getTableRow().getItem() == null) return;
                    PropertyValueDomain row = getTableView().getItems().get(getIndex());
                    if (row.getExternalInfluence() == null) {
                        AlertFactory.showConfirmation("Сообщение", "",
                                "Сначала укажите внешнее воздействие");
                        return;
                    }
                    if (!isEmpty() && getTableRow() != null && getTableRow().getItem() != null) {
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
                    if (getTableRow() == null || getTableRow().getItem() == null) return;

                    PropertyValueDomain row = getTableView().getItems().get(getIndex());
                    ExternalInfluenceLevel lvl = (ExternalInfluenceLevel) getTableColumn().getUserData();

                    // Сохраняем в БД и обновляем домен
                    row.setLevelPairValue(lvl, newValue);
                    propertyDataModel.savePropertyValues(row);

                    super.commitEdit(newValue);
                }
            });
            levelColumn.setPrefWidth(100);
            externalInfluenceLevelsTableView.getColumns().add(levelColumn);
        }
        propertyColumn.setPrefWidth(150);
        influenceColumn.setPrefWidth(150);
        externalInfluenceLevelsTableView.getColumns().add(0, propertyColumn);
        externalInfluenceLevelsTableView.getColumns().add(1, influenceColumn);
        externalInfluenceLevelsTableView.getItems().setAll(rows);
        externalInfluenceLevelsTableView.setEditable(true);
    }
}
