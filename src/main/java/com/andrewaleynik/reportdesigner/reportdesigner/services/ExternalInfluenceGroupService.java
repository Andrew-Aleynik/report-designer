package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;

import java.util.List;

public interface ExternalInfluenceGroupService {
    List<ExternalInfluenceGroup> getAllExternalInfluenceGroups();

    void saveExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup);

    void updateExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup);

    void deleteExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup);
}
