package com.andrewaleynik.reportdesigner.reportdesigner.components.buttons;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.ComponentNameHolder;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SpecificButton extends Button implements ComponentNameHolder {
    private static final Logger LOGGER = LogManager.getLogger();

    SpecificButton() {
        super();
        setOnAction(e -> addElement());
    }

    private void addElement() {
        try {
            Node element = App.getComponentLoader().load(this);

            Parent parent = getParent();
            if (parent instanceof Pane container) {
                int buttonIndex = container.getChildren().indexOf(this);
                if (buttonIndex >= 0) {
                    container.getChildren().add(buttonIndex, element);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error adding element: ", e);
        }
    }
}
