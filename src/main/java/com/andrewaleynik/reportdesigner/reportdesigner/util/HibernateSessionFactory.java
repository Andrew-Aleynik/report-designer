package com.andrewaleynik.reportdesigner.reportdesigner.util;

import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import org.hibernate.HibernateError;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSessionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFactory.class);
    private static SessionFactory sessionFactory;

    private HibernateSessionFactory() {
    }

    public static SessionFactory getSessionFactory() {
        if (!isInitialized()) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");

                addAnnotatedClasses(configuration);

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
                LOGGER.info("SessionFactory was created successful");
            } catch (Exception e) {
                LOGGER.error("Error creating SessionFactory");
                throw new HibernateError("Could not create SessionFactory", e);
            }
        }
        return sessionFactory;
    }

    private static void addAnnotatedClasses(Configuration configuration) {
        Class<?>[] annotatedClasses = {
                Element.class,
                ElementQuality.class,
                ElementType.class,
                ExternalInfluence.class,
                Property.class,
                PropertyUnit.class
        };

        for (Class<?> clazz : annotatedClasses) {
            configuration.addAnnotatedClass(clazz);
            LOGGER.debug("Added annotated class: {}", clazz.getSimpleName());
        }
    }

    public static void shutdown() {
        if (isInitialized()) {
            try {
                sessionFactory.close();
                sessionFactory = null;
                LOGGER.info("SessionFactory shut down successfully");
            } catch (Exception e) {
                LOGGER.error("Error shutting down SessionFactory: {}", e.getMessage(), e);
            }
        }
    }

    public static boolean isInitialized() {
        return sessionFactory != null;
    }
}
