module reportdesigner {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.validation;
    requires org.hibernate.validator;
    requires jakarta.persistence;
    requires lombok;
    requires java.naming;
    requires java.sql;
    requires java.transaction.xa;
    requires org.slf4j;
    requires kernel;
    requires io;
    requires layout;
    requires java.desktop;
    requires javafx.web;
    requires org.apache.pdfbox;
    requires javafx.swing;

    opens com.andrewaleynik.reportdesigner.reportdesigner.models to
            org.hibernate.orm.core,
            org.hibernate.validator,
            javafx.base;
    opens com.andrewaleynik.reportdesigner.reportdesigner to javafx.fxml;
    opens com.andrewaleynik.reportdesigner.reportdesigner.controllers to javafx.fxml;
    opens com.andrewaleynik.reportdesigner.reportdesigner.dao to org.junit.platform.commons, org.assertj.core, org.mockito;
    opens com.andrewaleynik.reportdesigner.reportdesigner.services to org.junit.platform.commons, org.assertj.core, org.mockito;
    opens com.andrewaleynik.reportdesigner.reportdesigner.util to org.assertj.core;
    opens com.andrewaleynik.reportdesigner.reportdesigner.dao.impl to org.junit.platform.commons, org.assertj.core, org.mockito;

    exports com.andrewaleynik.reportdesigner.reportdesigner;
    exports com.andrewaleynik.reportdesigner.reportdesigner.controllers;
    exports com.andrewaleynik.reportdesigner.reportdesigner.models;
    exports com.andrewaleynik.reportdesigner.reportdesigner.dao;
    exports com.andrewaleynik.reportdesigner.reportdesigner.services;
    exports com.andrewaleynik.reportdesigner.reportdesigner.util;
    exports com.andrewaleynik.reportdesigner.reportdesigner.dao.impl;
}