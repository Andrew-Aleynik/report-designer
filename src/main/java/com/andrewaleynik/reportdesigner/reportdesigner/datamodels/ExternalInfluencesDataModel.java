package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ExternalInfluenceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalInfluencesDataModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalInfluencesDataModel.class);
    private ObservableList<ExternalInfluence> externalInfluences = FXCollections.observableArrayList();
    private ExternalInfluence selectedExternalInfluence;
    private final ExternalInfluenceService externalInfluenceService;

    public ExternalInfluencesDataModel(ExternalInfluenceService externalInfluenceService) {
        this.externalInfluenceService = externalInfluenceService;
    }

    public ObservableList<ExternalInfluence> getExternalInfluences() {
        return externalInfluences;
    }

    public ExternalInfluence getSelectedExternalInfluence() {
        return selectedExternalInfluence;
    }

    public void refreshExternalInfluences() {
        externalInfluences.setAll(externalInfluenceService.getAllExternalInfluences());
    }

    public void refreshSelectedExternalInfluence(ExternalInfluence externalInfluence) {
        selectedExternalInfluence = externalInfluence;
    }

    public void saveExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluenceService.saveExternalInfluence(externalInfluence);
        refreshExternalInfluences();
    }

    public void updateExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluenceService.updateExternalInfluence(externalInfluence);
        refreshExternalInfluences();
    }

    public void deleteExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluenceService.deleteExternalInfluence(externalInfluence);
        refreshExternalInfluences();
    }
}
