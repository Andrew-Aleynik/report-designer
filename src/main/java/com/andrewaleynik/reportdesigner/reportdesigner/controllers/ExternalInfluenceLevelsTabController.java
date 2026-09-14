package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomain;
import com.andrewaleynik.reportdesigner.reportdesigner.domains.PropertyValueDomainMapper;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.DialogOpener;
import com.andrewaleynik.reportdesigner.reportdesigner.util.JavaFxControls;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExternalInfluenceLevelsTabController {

    private final ExternalInfluencesDataModel externalInfluencesDataModel;
    private final PropertyDataModel propertyDataModel;
    private final QualityDataModel qualityDataModel;

    @FXML
    private ComboBox<ElementQuality> qualitiesComboBox;
    @FXML
    private ComboBox<ExternalInfluenceLevel> levelsComboBox;
    @FXML
    private VBox propertyGroupTablesContainer;

    public ExternalInfluenceLevelsTabController(ExternalInfluencesDataModel externalInfluencesDataModel,
                                                PropertyDataModel propertyDataModel,
                                                QualityDataModel qualityDataModel) {
        this.externalInfluencesDataModel = externalInfluencesDataModel;
        this.propertyDataModel = propertyDataModel;
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public void initialize() {
        qualitiesComboBox.valueProperty().addListener(l -> updateTables());
        JavaFxControls.bindComboBoxDisplay(qualitiesComboBox, ElementQuality::getCode);
        qualitiesComboBox.setItems(qualityDataModel.getQualities());

        JavaFxControls.bindComboBoxDisplay(levelsComboBox, this::formatLevelDisplay);
        levelsComboBox.setItems(externalInfluencesDataModel.getExternalInfluenceLevels());

        qualityDataModel.onChange(this::updateTables);
        propertyDataModel.onChange(this::updateTables);
        externalInfluencesDataModel.onChange(this::updateTables);
        updateTables();
    }

    @FXML
    public void showAddExternalInfluenceLevelForm() {
        DialogOpener.<ExternalInfluenceLevelFormController>open(
                App.FxmlPaths.ADD_EXTERNAL_INFLUENCE_LEVEL_FORM,
                "Добавление интенсивности внешних воздействий",
                qualitiesComboBox
        ).ifPresent(result -> {
            if (result.saved()) {
                updateTables();
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
                "Удалить интенсивность \"" + formatLevelDisplay(selectedLevel) + "\"?"
        ).ifPresent(response -> {
            if (response == AlertFactory.OK) {
                externalInfluencesDataModel.deleteExternalInfluenceLevel(selectedLevel);
                levelsComboBox.getSelectionModel().clearSelection();
                updateTables();
            }
        });
    }

    private void updateTables() {
        propertyGroupTablesContainer.getChildren().clear();

        ElementQuality selectedQuality = qualitiesComboBox.getSelectionModel().getSelectedItem();
        if (selectedQuality == null) {
            return;
        }

        ElementQuality currentQuality = qualityDataModel.getQualities().stream()
                .filter(quality -> quality.getId() != null && quality.getId().equals(selectedQuality.getId()))
                .findFirst()
                .orElse(selectedQuality);

        List<PropertyValueDomain> allRows = PropertyValueDomainMapper.fromQuality(
                currentQuality,
                propertyDataModel.getPropertyValuesOfQuality(currentQuality));

        Map<PropertyGroup, List<PropertyValueDomain>> rowsByGroup = groupRows(allRows);
        List<ExternalInfluenceLevel> allLevels = externalInfluencesDataModel.getExternalInfluenceLevels();

        for (Map.Entry<PropertyGroup, List<PropertyValueDomain>> entry : rowsByGroup.entrySet()) {
            PropertyGroup group = entry.getKey();
            List<PropertyValueDomain> rows = entry.getValue();
            List<ExternalInfluenceLevel> groupLevels = allLevels.stream()
                    .filter(level -> Objects.equals(level.getPropertyGroup(), group))
                    .toList();

            Label groupLabel = new Label(group != null ? group.getName() : "Без группы");
            groupLabel.setFont(Font.font(null, FontWeight.BOLD, 13));
            VBox.setMargin(groupLabel, new Insets(4, 0, 0, 0));

            TableView<PropertyValueDomain> tableView = new TableView<>();
            tableView.setPrefHeight(Math.max(120, 40 + rows.size() * 28.0));
            PropertyValueLevelsTableBuilder.configure(
                    tableView,
                    rows,
                    groupLevels,
                    externalInfluencesDataModel.getExternalInfluences(),
                    propertyDataModel::savePropertyValues
            );

            propertyGroupTablesContainer.getChildren().addAll(groupLabel, tableView);
        }
    }

    private Map<PropertyGroup, List<PropertyValueDomain>> groupRows(List<PropertyValueDomain> rows) {
        return rows.stream()
                .sorted(Comparator.comparing(
                        row -> groupName(row.getProperty()),
                        Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.groupingBy(
                        row -> row.getProperty().getPropertyGroup(),
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    private String groupName(Property property) {
        PropertyGroup group = property.getPropertyGroup();
        return group != null ? group.getName() : null;
    }

    private String formatLevelDisplay(ExternalInfluenceLevel level) {
        if (level == null) {
            return "";
        }
        PropertyGroup group = level.getPropertyGroup();
        if (group == null || group.getName() == null) {
            return level.getName();
        }
        return group.getName() + ": " + level.getName();
    }
}
