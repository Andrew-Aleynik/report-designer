package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ExternalInfluenceGroupService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ExternalInfluenceLevelService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ExternalInfluenceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalInfluencesDataModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalInfluencesDataModel.class);
    private ObservableList<ExternalInfluence> externalInfluences = FXCollections.observableArrayList();
    private ObservableList<ExternalInfluenceGroup> externalInfluenceGroups = FXCollections.observableArrayList();
    private ObservableList<ExternalInfluenceLevel> externalInfluenceLevels = FXCollections.observableArrayList();
    private ExternalInfluenceGroup newExternalInfluenceGroup;
    private ExternalInfluence selectedExternalInfluence;
    private final ExternalInfluenceService service;
    private final ExternalInfluenceGroupService groupService;
    private final ExternalInfluenceLevelService levelService;


    public ExternalInfluencesDataModel(ExternalInfluenceService externalInfluenceService,
                                       ExternalInfluenceGroupService externalInfluenceGroupService,
                                       ExternalInfluenceLevelService externalInfluenceLevelService) {
        this.service = externalInfluenceService;
        this.groupService = externalInfluenceGroupService;
        this.levelService = externalInfluenceLevelService;
    }

    public ExternalInfluenceGroup getNewExternalInfluenceGroup() {
        return newExternalInfluenceGroup;
    }

    public ObservableList<ExternalInfluence> getExternalInfluences() {
        return externalInfluences;
    }

    public ObservableList<ExternalInfluenceGroup> getExternalInfluenceGroups() {
        return externalInfluenceGroups;
    }

    public ObservableList<ExternalInfluenceLevel> getExternalInfluenceLevels() {
        return externalInfluenceLevels;
    }

    public ExternalInfluence getSelectedExternalInfluence() {
        return selectedExternalInfluence;
    }

    public void refreshExternalInfluences() {
        externalInfluences.setAll(service.getAllExternalInfluences());
    }

    public void refreshExternalInfluenceGroups() {
        externalInfluenceGroups.setAll(groupService.getAllExternalInfluenceGroups());
    }

    public void refreshExternalInfluenceLevels() {
        externalInfluenceLevels.setAll(levelService.getAllExternalInfluenceLevels());
    }

    public void refreshSelectedExternalInfluence(ExternalInfluence externalInfluence) {
        selectedExternalInfluence = externalInfluence;
    }

    public void saveExternalInfluence(ExternalInfluence externalInfluence) {
        service.saveExternalInfluence(externalInfluence);
        refreshExternalInfluences();
    }

    public void saveExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup) {
        groupService.saveExternalInfluenceGroup(externalInfluenceGroup);
        newExternalInfluenceGroup = externalInfluenceGroup;
        refreshExternalInfluenceGroups();
    }

    public void saveExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel) {
        levelService.saveExternalInfluenceLevel(externalInfluenceLevel);
        refreshExternalInfluenceLevels();
    }

    public void updateExternalInfluence(ExternalInfluence externalInfluence) {
        service.updateExternalInfluence(externalInfluence);
        refreshExternalInfluences();
    }

    public void deleteExternalInfluence(ExternalInfluence externalInfluence) {
        service.deleteExternalInfluence(externalInfluence);
        refreshExternalInfluences();
    }
}
