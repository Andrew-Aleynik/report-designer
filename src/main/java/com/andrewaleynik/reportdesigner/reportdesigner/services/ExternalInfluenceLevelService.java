package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;

import java.util.List;

public interface ExternalInfluenceLevelService {
    List<ExternalInfluenceLevel> getAllExternalInfluenceLevels();

    void saveExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel);

    void updateExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel);

    void deleteExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel);
}
