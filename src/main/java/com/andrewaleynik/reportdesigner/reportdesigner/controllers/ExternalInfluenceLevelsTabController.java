package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomainMapper;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class ExternalInfluenceLevelsTabController {

    private final ExternalInfluencesDataModel externalInfluencesDataModel;
    private final PropertyDataModel propertyDataModel;
    private final QualityDataModel qualityDataModel;

    @FXML
    private ComboBox<ElementQuality> qualitiesComboBox;
    @FXML
    private ComboBox<ExternalInfluenceLevel> levelsComboBox;
    @FXML
    private TableView<PropertyValueDomain> externalInfluenceLevelsTableView;

    public ExternalInfluenceLevelsTabController(ExternalInfluencesDataModel externalInfluencesDataModel,
                                                PropertyDataModel propertyDataModel,
                                                QualityDataModel qualityDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
        this.propertyDataModel = propertyDataModel;
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public void initialize() {
        qualitiesComboBox.valueProperty().addListener(l -> updateTable());
        JavaFxControls.bindComboBoxDisplay(qualitiesComboBox, ElementQuality::getCode);
        qualitiesComboBox.setItems(qualityDataModel.getQualities());

        JavaFxControls.bindComboBoxDisplay(levelsComboBox, ExternalInfluenceLevel::getName);
        levelsComboBox.setItems(externalInfluencesDataModel.getExternalInfluenceLevels());

        qualityDataModel.onChange(this::updateTable);
        propertyDataModel.onChange(this::updateTable);
        externalInfluencesDataModel.onChange(this::updateTable);
        updateTable();
    }

    @FXML
    public void showAddExternalInfluenceLevelForm() {
        DialogOpener.<ExternalInfluenceLevelFormController>open(
                App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_LEVEL_FORM,
                "Добавление интенсивности внешних воздействий",
                qualitiesComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                updateTable();
            }
        });
    }

    @FXML
    public void handleDeleteExternalInfluenceLevel() {
        ExternalInfluenceLevel selectedLevel = levelsComboBox.getValue();
        if (selectedLevel == null) {
            AlertFactory.showWarning("Удаление интенсивности",
                    "Выберите интенсивность воздействия для удаления.");
            return;
        }

        AlertFactory.showConfirmation(
                "Подтверждение удаления",
                "Удаление интенсивности",
                "Удалить интенсивность \"" + selectedLevel.getName() + "\"?"
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                externalInfluencesDataModel.deleteExternalInfluenceLevel(selectedLevel);
                levelsComboBox.getSelectionModel().clearSelection();
                updateTable();
            }
        });
    }

    private void updateTable() {
        ElementQuality selectedQuality = qualitiesComboBox.getSelectionModel().getSelectedItem();
        if (selectedQuality == null) {
            externalInfluenceLevelsTableView.getItems().clear();
            externalInfluenceLevelsTableView.getColumns().clear();
            return;
        }

        ElementQuality currentQuality = qualityDataModel.getQualities().stream()
                .filter(quality -> quality.getId() != null && quality.getId().equals(selectedQuality.getId()))
                .findFirst()
                .orElse(selectedQuality);

        PropertyValueLevelsTableBuilder.configure(
                externalInfluenceLevelsTableView,
                PropertyValueDomainMapper.fromQuality(
                        currentQuality,
                        propertyDataModel.getPropertyValuesOfQuality(currentQuality)),
                externalInfluencesDataModel.getExternalInfluenceLevels(),
                externalInfluencesDataModel.getExternalInfluences(),
                propertyDataModel::savePropertyValues
        );
    }
}
