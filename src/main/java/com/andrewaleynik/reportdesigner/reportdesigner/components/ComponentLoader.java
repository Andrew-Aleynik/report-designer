package com.andrewaleynik.reportdesigner.reportdesigner.components;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.ComponentNameHolder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class ComponentLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String COMPONENTS_PATH = "components/";

    public Node load(ComponentNameHolder componentNameHolder) {
        String componentName = componentNameHolder.getComponentName();
        Node result;
        try {
            result = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(COMPONENTS_PATH + componentName)));
        } catch (IOException e) {
            LOGGER.error("Error with load {}: {}", componentName, e);
            result = null;
        }

        return result;
    }
}
