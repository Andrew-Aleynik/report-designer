package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class ElementQualitiesTabController implements Controller {
    private QualityDataModel qualityDataModel;
    @FXML
    private ComboBox<ElementQuality> elementQualitiesComboBox;

    public ElementQualitiesTabController(QualityDataModel qualityDataModel) {
        this.qualityDataModel = qualityDataModel;
    }

    @FXML
    public void initialize() {
        initializeElementQualitiesComboBox();
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

        elementQualitiesComboBox.setItems(qualityDataModel.getQualities());
    }

    @FXML
    private void handleCreateElementQuality() {
        // TODO
    }
}
