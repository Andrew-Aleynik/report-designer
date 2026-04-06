package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QualityDataModel {
    private final ObservableList<ElementQuality> qualities = FXCollections.observableArrayList();
    private ElementQuality newQuality;
    private ElementQuality selectedQuality;
    private final ElementQualityService elementQualityService;

    public QualityDataModel(ElementQualityService elementQualityService) {
        this.elementQualityService = elementQualityService;
        refreshQualities();
    }

    public ObservableList<ElementQuality> getQualities() {
        return qualities;
    }

    public ElementQuality getNewQuality() {
        return newQuality;
    }

    public ElementQuality getSelectedQuality() {
        return selectedQuality;
    }

    public void refreshQualities() {
        qualities.setAll(elementQualityService.getAllQualities());
    }

    public void refreshNewQuality(ElementQuality quality) {
        newQuality = quality;
    }

    public void refreshSelectedQuality(ElementQuality quality) {
        selectedQuality = quality;
    }

    public void saveQuality(ElementQuality quality) {
        elementQualityService.saveQuality(quality);
        refreshNewQuality(quality);
        refreshQualities();
    }

    public void updateQuality(ElementQuality quality) {
        elementQualityService.updateQuality(quality);
    }

    public void deleteQuality(ElementQuality quality) {
        elementQualityService.deleteQuality(quality);
    }
}
