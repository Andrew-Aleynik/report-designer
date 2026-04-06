package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ExternalInfluenceGroupDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceGroup;

import java.util.List;

public class ExternalInfluenceGroupServiceImpl implements ExternalInfluenceGroupService {
    private final ExternalInfluenceGroupDao dao;

    public ExternalInfluenceGroupServiceImpl(ExternalInfluenceGroupDao externalInfluenceGroupDao) {
        dao = externalInfluenceGroupDao;
    }

    @Override
    public List<ExternalInfluenceGroup> getAllExternalInfluenceGroups() {
        return dao.findAll();
    }

    @Override
    public void saveExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup) {
        dao.save(externalInfluenceGroup);
    }

    @Override
    public void updateExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup) {
        dao.update(externalInfluenceGroup);
    }

    @Override
    public void deleteExternalInfluenceGroup(ExternalInfluenceGroup externalInfluenceGroup) {
        dao.delete(externalInfluenceGroup);
    }
}
