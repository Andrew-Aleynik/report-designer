package com.andrewaleynik.reportdesigner.reportdesigner.dao;

import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;

import java.util.Optional;

public interface PropertyUnitDao extends BaseDao<PropertyUnit> {
    Optional<PropertyUnit> findByName(String name);
}