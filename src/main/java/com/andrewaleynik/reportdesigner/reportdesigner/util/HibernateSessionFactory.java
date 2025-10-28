package com.andrewaleynik.reportdesigner.reportdesigner.util;

import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactory() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                configuration.addAnnotatedClass(Element.class);
                configuration.addAnnotatedClass(ElementQuality.class);
                configuration.addAnnotatedClass(ElementType.class);
                configuration.addAnnotatedClass(ExternalInfluence.class);
                configuration.addAnnotatedClass(Property.class);
                configuration.addAnnotatedClass(PropertyUnit.class);

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());


                System.out.println("=== SessionFactory успешно создана ===");

            } catch (Exception e) {
                System.out.println("=== ОШИБКА при создании SessionFactory ===");
                e.printStackTrace();
                throw new RuntimeException("Could not create SessionFactory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
            System.out.println("=== SessionFactory закрыта ===");
        }
    }
}
