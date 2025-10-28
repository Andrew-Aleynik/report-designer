package com.andrewaleynik.reportdesigner.reportdesigner;

import com.andrewaleynik.reportdesigner.reportdesigner.controllers.MainController;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.*;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementQualityServiceImpl;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementServiceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends javafx.application.Application {
    private static final String APP_NAME = "Конструктор отчетов";
    private static final Integer STARTUP_WIDTH = 750;
    private static final Integer STARTUP_HEIGHT = 1000;

    public static final String MAIN_PATH = "/com/andrewaleynik/reportdesigner/reportdesigner/templates/Main.fxml";
    public static final String ADD_ROOT_ELEMENT_FORM_PATH =
            "/com/andrewaleynik/reportdesigner/reportdesigner/templates/AddRootElementForm.fxml";
    public static final String ADD_CHILD_ELEMENT_FORM_PATH =
            "/com/andrewaleynik/reportdesigner/reportdesigner/templates/AddChildElementForm.fxml";
    public static final String ADD_ELEMENT_TYPE_FORM_PATH =
            "/com/andrewaleynik/reportdesigner/reportdesigner/templates/AddElementTypeForm.fxml";
    public static final String ADD_ELEMENT_QUALITY_SHORT_FORM_PATH =
            "/com/andrewaleynik/reportdesigner/reportdesigner/templates/AddElementQualityShortForm.fxml";

    private static final ElementService elementService;
    private static final ElementQualityService elementQualityService;

    static {
        ElementDao elementDao = new ElementDaoImpl();
        ElementQualityDao elementQualityDao = new ElementQualityDaoImpl();
        ElementTypeDao elementTypeDao = new ElementTypeDaoImpl();
        PropertyDao propertyDao = new PropertyDaoImpl();
        PropertyUnitDao propertyUnitDao = new PropertyUnitDaoImpl();

        elementService = new ElementServiceImpl(elementDao, elementTypeDao, elementQualityDao);
        elementQualityService = new ElementQualityServiceImpl(elementQualityDao, propertyDao, propertyUnitDao);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(MAIN_PATH)));


        loader.setControllerFactory(type -> {
            if (type == MainController.class) {
                return new MainController(getElementService());
            } else {
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Parent root = loader.load();

        Scene scene = new Scene(root, STARTUP_WIDTH, STARTUP_HEIGHT);

        stage.setScene(scene);

        stage.setTitle(APP_NAME);
        stage.centerOnScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static ElementService getElementService() {
        return elementService;
    }

    public static ElementQualityService getElementQualityService() {
        return elementQualityService;
    }
}