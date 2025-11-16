package com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.BaseDao;
import com.andrewaleynik.reportdesigner.reportdesigner.util.HibernateSessionFactory;
import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseDaoImpl<T> implements BaseDao<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDaoImpl.class);

    private final Class<T> entityClass;

    protected Session openSession() {
        return HibernateSessionFactory.getSessionFactory().openSession();
    }

    protected BaseDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void executeInTransaction(Consumer<Session> operation) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Transaction failed: {}", e.getMessage(), e);
            throw new PersistenceException("Transaction failed", e);
        }
    }

    protected <R> R executeInTransaction(Function<Session, R> operation) {
        Transaction transaction = null;
        try (Session session = openSession()) {
            transaction = session.beginTransaction();
            R result = operation.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Transaction failed: {}", e.getMessage(), e);
            throw new PersistenceException("Transaction failed", e);
        }
    }

    protected <R> R execute(Function<Session, R> operation) {
        try (Session session = openSession()) {
            return operation.apply(session);
        }
    }

    public void save(T entity) {
        executeInTransaction((Consumer<Session>) session -> session.persist(entity));
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(em -> {
            boolean isManaged = em.contains(entity);

            T entityToRemove = isManaged ? entity : em.merge(entity);

            em.remove(entityToRemove);
        });
    }

    @Override
    public void deleteById(Long id) {
        executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    public void update(T entity) {
        executeInTransaction((Consumer<Session>) em -> em.merge(entity));
    }

    public Optional<T> findById(Long id) {
        return execute(em -> Optional.ofNullable(em.find(entityClass, id)));
    }

    public List<T> findAll() {
        return execute(em -> {
            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(query, entityClass).getResultList();
        });
    }
}
