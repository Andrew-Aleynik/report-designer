package com.andrewaleynik.reportdesigner.reportdesigner.dao;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;

import java.util.List;
import java.util.Optional;

public interface ElementDao extends BaseDao<Element> {
    List<Element> findByParent(Element parent);

    Optional<Element> findByCode(String code);

    List<Element> findByType(ElementType type);

    List<Element> findByLevel(Integer level);

    List<Element> findByName(String name);

    boolean existsByCode(String code);

    List<Element> findRoots();
}
