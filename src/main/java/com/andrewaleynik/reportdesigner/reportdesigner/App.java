package com.andrewaleynik.reportdesigner.reportdesigner;

import com.andrewaleynik.reportdesigner.reportdesigner.config.ApplicationContext;
import com.andrewaleynik.reportdesigner.reportdesigner.config.ControllerFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
import com.andrewaleynik.reportdesigner.reportdesigner.util.HibernateSessionFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class App extends javafx.application.Application {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    private static final String APP_NAME = "Конструктор отчетов";
    private static final int STARTUP_WIDTH = 600;
    private static final int STARTUP_HEIGHT = 800;

    public static final class FxmlPaths {
        public static final String MAIN = "/templates/Main.fxml";
        public static final String ADD_ROOT_ELEMENT_FORM = "/templates/AddRootElementForm.fxml";
        public static final String ADD_CHILD_ELEMENT_FORM = "/templates/AddChildElementForm.fxml";
        public static final String ADD_ELEMENT_TYPE_FORM = "/templates/AddElementTypeForm.fxml";
        public static final String ADD_ELEMENT_QUALITY_SHORT_FORM = "/templates/AddElementQualityShortForm.fxml";
        public static final String EDIT_ELEMENT_FORM = "/templates/EditElementForm.fxml";
        public static final String ADD_PROPERTY_FORM = "/templates/AddPropertyForm.fxml";
        public static final String ADD_PROPERTY_UNIT_FORM = "/templates/AddPropertyUnitForm.fxml";
        public static final String ADD_PROPERTY_GROUP_FORM = "/templates/AddPropertyGroupForm.fxml";
        public static final String EXPORT_PREVIEW = "/templates/PdfPreview.fxml";
        public static final String ADD_EXTERNAL_INFLUENCE_FORM = "/templates/ExternalInfluenceForm.fxml";
        public static final String EDIT_EXTERNAL_INFLUENCE_FORM = "/templates/ExternalInfluenceForm.fxml";
        public static final String INHERIT_PROPERTY_FORM = "/templates/InheritPropertyForm.fxml";
        public static final String ADD_EXTERNAL_INFLUENCE_GROUP_FORM = "/templates/AddExternalInfluenceGroup.fxml";
        public static final String ADD_EXTERNAL_INFLUENCE_LEVEL_FORM = "/templates/AddExternalInfluenceLevel.fxml";

        private FxmlPaths() {
        }
    }

    public static final class FontPaths {
        public static final String ARIAL = "/fonts/arialmt.ttf";
        public static final String ARIAL_BOLD_ITALIC = "/fonts/arialmt.ttf";

        private FontPaths() {
        }
    }

    private static final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private static final ControllerFactory controllerFactory = new ControllerFactory(applicationContext);

    @Override
    public void start(Stage primaryStage) {
        try {
            initializePrimaryStage(primaryStage);
            log.info("App started successful");
        } catch (Exception e) {
            log.error("Starting critical error: {}", e.getMessage(), e);
            AlertFactory.showError("Ошибка запуска", "Не удалось запустить приложение");
            System.exit(1);
        }
    }

    private void initializePrimaryStage(Stage primaryStage) throws IOException {
        FXMLLoader loader = createFxmlLoader(FxmlPaths.MAIN);
        Parent root = loader.load();

        Scene scene = new Scene(root, STARTUP_WIDTH, STARTUP_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle(APP_NAME);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private FXMLLoader createFxmlLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlPath)));
        loader.setControllerFactory(controllerFactory);
        return loader;
    }

    @Override
    public void stop() {
        HibernateSessionFactory.shutdown();
        log.info("App was shut down");
    }

    public static Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    public static void main(String[] args) {
        log.info("Launching app...");
        try {
            launch(args);
        } catch (Exception e) {
            log.error("Critical exception: {}", e.getMessage(), e);
        }
    }
}
