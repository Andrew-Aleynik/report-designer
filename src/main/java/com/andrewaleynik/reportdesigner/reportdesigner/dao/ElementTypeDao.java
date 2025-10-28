package com.andrewaleynik.reportdesigner.reportdesigner.dao;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;

import java.util.Optional;

public interface ElementTypeDao extends BaseDao<ElementType> {
    Optional<ElementType> findByName(String name);
}
