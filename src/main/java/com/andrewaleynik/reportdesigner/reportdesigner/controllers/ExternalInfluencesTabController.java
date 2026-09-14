package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ExternalInfluencesTabController {

    @FXML
    private TableView<ExternalInfluence> externalInfluencesTableView;

    private final ExternalInfluencesDataModel externalInfluencesDataModel;

    public ExternalInfluencesTabController(ExternalInfluencesDataModel externalInfluencesDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
    }

    @FXML
    private void initialize() {
        TableColumn<ExternalInfluence, String> nameColumn =
                JavaFxControls.textColumn("Название", ExternalInfluence::getName);
        TableColumn<ExternalInfluence, String> descriptionColumn =
                JavaFxControls.textColumn("Описание", ExternalInfluence::getDescription);
        TableColumn<ExternalInfluence, String> groupColumn = JavaFxControls.textColumn("Группа",
                influence -> influence.getExternalInfluenceGroup() != null
                        ? influence.getExternalInfluenceGroup().getName()
                        : "");
        TableColumn<ExternalInfluence, Void> actionsColumn = JavaFxControls.actionsColumn(
                this::handleEditExternalInfluence,
                this::handleDeleteExternalInfluence
        );

        nameColumn.setPrefWidth(200);
        descriptionColumn.setPrefWidth(200);
        groupColumn.setPrefWidth(200);
        actionsColumn.setPrefWidth(200);

        externalInfluencesTableView.getColumns().setAll(
                nameColumn, descriptionColumn, groupColumn, actionsColumn);
        JavaFxControls.configureConstrainedTable(externalInfluencesTableView);
        externalInfluencesTableView.setItems(externalInfluencesDataModel.getExternalInfluences());
    }

    @FXML
    public void showAddExternalInfluenceForm() {
        externalInfluencesDataModel.setSelectedExternalInfluence(null);

        DialogOpener.<ExternalInfluenceFormController>open(
                App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_FORM,
                "Добавление внешнего воздействия",
                externalInfluencesTableView
        ).ifPresent(result -> {
            if (result.saved()) {
                externalInfluencesDataModel.setSelectedExternalInfluence(null);
            }
        });
    }

    private void handleEditExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluencesDataModel.setSelectedExternalInfluence(externalInfluence);

        DialogOpener.<ExternalInfluenceFormController>open(
                App.FxmlPaths.EDIT_EXTERNAL_INFLUENCE_FORM,
                "Изменение внешнего воздействия",
                externalInfluencesTableView
        ).ifPresent(result -> {
            if (result.saved()) {
                externalInfluencesDataModel.setSelectedExternalInfluence(null);
            }
        });
    }

    private void handleDeleteExternalInfluence(ExternalInfluence externalInfluence) {
        AlertFactory.showConfirmation(
                "Подтверждение удаления",
                "Удаление внешнего воздействия",
                "Вы уверены, что хотите удалить это внешнее воздействие?"
        ).ifPresent(response -> {
            if (response == AlertFactory.OK && externalInfluence != null) {
                externalInfluencesDataModel.deleteExternalInfluence(externalInfluence);
            }
        });
    }
}
