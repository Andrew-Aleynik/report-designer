package com.andrewaleynik.reportdesigner.reportdesigner.dao;

import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T> {
    Optional<T> findById(Long id);

    List<T> findAll();

    void save(T entity);

    void update(T entity);

    void delete(T entity);

    void deleteById(Long id);

    Session openSession();
}
