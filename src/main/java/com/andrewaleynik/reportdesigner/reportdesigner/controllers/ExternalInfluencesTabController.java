package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
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


public class ExternalInfluencesTabController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalInfluencesTabController.class);
    @FXML
    private TableView<ExternalInfluence> externalInfluencesTableView;
    private final ExternalInfluencesDataModel externalInfluencesDataModel;

    public ExternalInfluencesTabController(ExternalInfluencesDataModel externalInfluencesDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
    }

    @FXML
    private void initialize() {
        TableColumn<ExternalInfluence, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setCellValueFactory(cellData -> {
            ExternalInfluence externalInfluence = cellData.getValue();
            if (externalInfluence.getName() != null) {
                return new SimpleStringProperty(externalInfluence.getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        nameColumn.setCellFactory(column -> new TableCell<ExternalInfluence, String>() {
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

        TableColumn<ExternalInfluence, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(cellData -> {
            ExternalInfluence externalInfluence = cellData.getValue();
            if (externalInfluence.getDescription() != null) {
                return new SimpleStringProperty(externalInfluence.getDescription());
            } else {
                return new SimpleStringProperty("");
            }
        });
        descriptionColumn.setCellFactory(column -> new TableCell<ExternalInfluence, String>() {
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

        TableColumn<ExternalInfluence, String> externalInfluenceGroupColumn = new TableColumn<>("Группа");
        externalInfluenceGroupColumn.setCellValueFactory(cellData -> {
            ExternalInfluence externalInfluence = cellData.getValue();
            if (externalInfluence.getDescription() != null) {
                return new SimpleStringProperty(externalInfluence.getExternalInfluenceGroup().getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        descriptionColumn.setCellFactory(column -> new TableCell<ExternalInfluence, String>() {
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

        TableColumn<ExternalInfluence, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");

            {
                editButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5;");
                deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5; -fx-text-fill: red;");

                editButton.setOnAction(event -> {
                    ExternalInfluence externalInfluence = getTableView().getItems().get(getIndex());
                    handleEditExternalInfluence(externalInfluence);
                });

                deleteButton.setOnAction(event -> {
                    ExternalInfluence externalInfluence = getTableView().getItems().get(getIndex());
                    handleDeleteExternalInfluence(externalInfluence);
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

        nameColumn.setPrefWidth(200);
        descriptionColumn.setPrefWidth(200);
        externalInfluenceGroupColumn.setPrefWidth(200);
        actionsColumn.setPrefWidth(200);

        externalInfluencesTableView.getColumns().clear();
        externalInfluencesTableView.getColumns().addAll(nameColumn, descriptionColumn, externalInfluenceGroupColumn,
                actionsColumn);
        externalInfluencesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        externalInfluencesTableView.setItems(externalInfluencesDataModel.getExternalInfluences());
    }

    @FXML
    public void showAddExternalInfluenceForm() {
        try {
            externalInfluencesDataModel.refreshSelectedExternalInfluence(null);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ExternalInfluenceFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Добавление внешнего воздействия");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(externalInfluencesTableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                externalInfluencesDataModel.refreshSelectedExternalInfluence(null);
            }
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void handleEditExternalInfluence(ExternalInfluence externalInfluence) {
        try {
            externalInfluencesDataModel.refreshSelectedExternalInfluence(externalInfluence);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(App.FxmlPaths.EDIT_EXTERNAL_INFLUENCE_FORM));
            loader.setControllerFactory(App.getControllerFactory());
            Parent root = loader.load();
            ExternalInfluenceFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Изменение внешнего воздействия");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(externalInfluencesTableView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaved()) {
                externalInfluencesDataModel.refreshSelectedExternalInfluence(null);
            }
        } catch (IOException e) {
            LOGGER.error("Error opening form: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка при открытии формы", e.getMessage());
        }
    }

    private void handleDeleteExternalInfluence(ExternalInfluence externalInfluence) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление внешнего воздействия");
        alert.setContentText("Вы уверены, что хотите удалить это внешнее воздействие?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (externalInfluence != null) {
                    externalInfluencesDataModel.deleteExternalInfluence(externalInfluence);
                }
            }
        });
    }
}
