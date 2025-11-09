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

    opens com.andrewaleynik.reportdesigner.reportdesigner.models to
            org.hibernate.orm.core,
            org.hibernate.validator,
            javafx.base;
    opens com.andrewaleynik.reportdesigner.reportdesigner to javafx.fxml;
    opens com.andrewaleynik.reportdesigner.reportdesigner.controllers to javafx.fxml;
    exports com.andrewaleynik.reportdesigner.reportdesigner;
    exports com.andrewaleynik.reportdesigner.reportdesigner.controllers;
    exports com.andrewaleynik.reportdesigner.reportdesigner.models;
}