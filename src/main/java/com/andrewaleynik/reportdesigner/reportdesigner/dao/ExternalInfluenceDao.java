package com.andrewaleynik.reportdesigner.reportdesigner.dao;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;

import java.util.Optional;

public interface ExternalInfluenceDao extends BaseDao<ExternalInfluence> {
    Optional<ExternalInfluence> findByName(String name);
}
