module com.andrewaleynik.reportdesigner.reportdesigner {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;

    opens com.andrewaleynik.reportdesigner.reportdesigner to javafx.fxml;
    exports com.andrewaleynik.reportdesigner.reportdesigner;
    exports com.andrewaleynik.reportdesigner.reportdesigner.enums;
    opens com.andrewaleynik.reportdesigner.reportdesigner.enums to javafx.fxml;
    exports com.andrewaleynik.reportdesigner.reportdesigner.components;
    opens com.andrewaleynik.reportdesigner.reportdesigner.components to javafx.fxml;
    exports com.andrewaleynik.reportdesigner.reportdesigner.controllers;
    opens com.andrewaleynik.reportdesigner.reportdesigner.controllers to javafx.fxml;
    exports com.andrewaleynik.reportdesigner.reportdesigner.components.comboboxes;
    opens com.andrewaleynik.reportdesigner.reportdesigner.components.comboboxes to javafx.fxml;
    exports com.andrewaleynik.reportdesigner.reportdesigner.components.buttons;
    opens com.andrewaleynik.reportdesigner.reportdesigner.components.buttons to javafx.fxml;
}