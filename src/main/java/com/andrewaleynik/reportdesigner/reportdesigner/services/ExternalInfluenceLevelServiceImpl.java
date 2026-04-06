package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ExternalInfluenceLevelDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;

import java.util.List;

public class ExternalInfluenceLevelServiceImpl implements ExternalInfluenceLevelService {
    private final ExternalInfluenceLevelDao dao;

    public ExternalInfluenceLevelServiceImpl(ExternalInfluenceLevelDao externalInfluenceLevelDao) {
        dao = externalInfluenceLevelDao;
    }

    @Override
    public List<ExternalInfluenceLevel> getAllExternalInfluenceLevels() {
        return dao.findAll();
    }

    @Override
    public void saveExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel) {
        dao.save(externalInfluenceLevel);
    }

    @Override
    public void updateExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel) {
        dao.update(externalInfluenceLevel);
    }

    @Override
    public void deleteExternalInfluenceLevel(ExternalInfluenceLevel externalInfluenceLevel) {
        dao.delete(externalInfluenceLevel);
    }
}
