package com.andrewaleynik.reportdesigner.reportdesigner;

import com.andrewaleynik.reportdesigner.reportdesigner.components.ComponentLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends javafx.application.Application implements ComponentNameHolder {
    private static final ComponentLoader componentLoader = new ComponentLoader();

    private static final String APPLICATION_NAME = "Конструктор отчетов";
    private static final Integer STARTUP_WIDTH = 750;
    private static final Integer STARTUP_HEIGHT = 1000;

    @Override
    public void start(Stage stage) {
        Parent root = (Parent) componentLoader.load(this);
        Scene scene = new Scene(root, STARTUP_WIDTH, STARTUP_HEIGHT);

        stage.setScene(scene);

        stage.setTitle(APPLICATION_NAME);
        stage.centerOnScreen();

        stage.show();
    }

    public static ComponentLoader getComponentLoader() {
        return componentLoader;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public String getComponentName() {
        return "Main.fxml";
    }
}