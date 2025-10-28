package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ElementDaoImpl extends BaseDaoImpl<Element> implements ElementDao {

    public ElementDaoImpl() {
        super(Element.class);
    }

    @Override
    public List<Element> findByParent(Element parent) {
        return execute(s -> {
            Query<Element> query = s.createQuery(
                    "SELECT e FROM Element e WHERE e.parent = :parent", Element.class);
            query.setParameter("parent", parent);
            return query.getResultList();
        });
    }

    public Optional<Element> findByCode(String code) {
        return execute(s -> {
            Query<Element> query = s.createQuery(
                    "SELECT e FROM Element e WHERE e.code = :code", Element.class);
            query.setParameter("code", code);
            return query.getResultStream().findFirst();
        });
    }

    public List<Element> findByName(String name) {
        return execute(s -> {
            Query<Element> query = s.createQuery(
                    "SELECT e FROM Element e WHERE e.name LIKE :name", Element.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        });
    }

    @Override
    public List<Element> findByType(ElementType type) {
        return execute(s -> {
            Query<Element> query = s.createQuery(
                    "SELECT e FROM Element e WHERE e.type = :type", Element.class);
            query.setParameter("type", type);
            return query.getResultList();
        });
    }

    @Override
    public List<Element> findByLevel(Integer level) {
        return execute(s -> {
            Query<Element> query = s.createQuery(
                    "SELECT e FROM Element e WHERE e.level = :level", Element.class);
            query.setParameter("level", level);
            return query.getResultList();
        });
    }

    @Override
    public List<Element> findRoots() {
        return execute(s -> {
            Query<Element> query = s.createQuery(
                    "SELECT e FROM Element e WHERE e.parent IS NULL", Element.class);
            return query.getResultList();
        });
    }

    @Override
    public boolean existsByCode(String code) {
        return execute(s -> {
            Query<Long> query = s.createQuery(
                    "SELECT COUNT(e) FROM Element e WHERE e.code = :code", Long.class);
            query.setParameter("code", code);
            return query.getSingleResult() > 0;
        });
    }
}
