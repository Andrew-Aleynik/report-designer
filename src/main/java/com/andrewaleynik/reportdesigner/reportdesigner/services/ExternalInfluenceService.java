package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;

import java.util.List;

public interface ExternalInfluenceService {
    List<ExternalInfluence> getAllExternalInfluences();
    void saveExternalInfluence(ExternalInfluence externalInfluence);
    void updateExternalInfluence(ExternalInfluence externalInfluence);
    void deleteExternalInfluence(ExternalInfluence externalInfluence);
    void validateExternalInfluence(ExternalInfluence externalInfluence);
}
