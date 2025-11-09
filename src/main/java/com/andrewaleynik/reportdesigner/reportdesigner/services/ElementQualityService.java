package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;

import java.util.List;
import java.util.Optional;

public interface ElementQualityService {
    List<ElementQuality> getAllQualities();

    Optional<ElementQuality> getElementQualityById(Long id);

    void saveQuality(ElementQuality elementQuality);

    void deleteQuality(ElementQuality elementQuality);

    void updateQuality(ElementQuality elementQuality);
}
