package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QualityDataModel {
    private ObservableList<ElementQuality> qualities = FXCollections.observableArrayList();

    private ElementQuality newQuality;

    private ElementQualityService elementQualityService;

    public QualityDataModel(ElementQualityService elementQualityService) {
        this.elementQualityService = elementQualityService;
    }

    public ObservableList<ElementQuality> getQualities() {
        return qualities;
    }

    public ElementQuality getNewQuality() {
        return newQuality;
    }

    public void refreshQualities() {
        qualities.setAll(elementQualityService.getAllQualities());
    }

    public void refreshNewQuality(ElementQuality quality) {
        newQuality = quality;
    }

    public void saveQuality(ElementQuality quality) {
        elementQualityService.saveQuality(quality);
        refreshQualities();
    }
}
