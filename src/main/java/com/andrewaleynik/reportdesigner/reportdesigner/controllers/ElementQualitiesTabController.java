package com.andrewaleynik.reportdesigner.reportdesigner.controllers;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.util.List;

public class ElementQualitiesTabController implements Controller {
    private ElementQualityService elementQualityService;
    @FXML
    private ComboBox<ElementQuality> elementQualitiesComboBox;

    public ElementQualitiesTabController(ElementQualityService elementQualityService) {
        this.elementQualityService = elementQualityService;
    }

    @FXML
    public void initialize() {
        initializeElementQualitiesComboBox();
        updateViews();
    }

    @Override
    public void updateViews() {
        List<ElementQuality> qualities = elementQualityService.getAllQualities();
        elementQualitiesComboBox.getItems().setAll(qualities);
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
    }

    @FXML
    private void handleCreateElementQuality() {
        // TODO
    }
}
