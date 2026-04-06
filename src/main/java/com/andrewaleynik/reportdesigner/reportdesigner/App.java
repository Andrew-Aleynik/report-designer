package com.andrewaleynik.reportdesigner.reportdesigner;

import com.andrewaleynik.reportdesigner.reportdesigner.controllers.*;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.*;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.*;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.services.*;
import com.andrewaleynik.reportdesigner.reportdesigner.util.AlertFactory;
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
        public static final String ADD_ROOT_ELEMENT_FORM =
                "/templates/AddRootElementForm.fxml";
        public static final String ADD_CHILD_ELEMENT_FORM =
                "/templates/AddChildElementForm.fxml";
        public static final String ADD_ELEMENT_TYPE_FORM =
                "/templates/AddElementTypeForm.fxml";
        public static final String ADD_ELEMENT_QUALITY_SHORT_FORM =
                "/templates/AddElementQualityShortForm.fxml";
        public static final String EDIT_ELEMENT_FORM =
                "/templates/EditElementForm.fxml";
        public static final String ADD_PROPERTY_FORM =
                "/templates/AddPropertyForm.fxml";
        public static final String ADD_PROPERTY_UNIT_FORM =
                "/templates/AddPropertyUnitForm.fxml";
        public static final String EXPORT_PREVIEW =
                "/templates/PdfPreview.fxml";
        public static final String ADD_EXTERNAL_INFLUENCE_FORM =
                "/templates/ExternalInfluenceForm.fxml";
        public static final String EDIT_EXTERNAL_INFLUENCE_FORM =
                "/templates/ExternalInfluenceForm.fxml";
        public static final String INHERIT_PROPERTY_FORM =
                "/templates/InheritPropertyForm.fxml";
        public static final String ADD_EXTERNAL_INFLUENCE_GROUP_FORM =
                "/templates/AddExternalInfluenceGroup.fxml";
        public static final String ADD_EXTERNAL_INFLUENCE_LEVEL_FORM =
                "/templates/AddExternalInfluenceLevel.fxml";

        private FxmlPaths() {
        }
    }

    public static final class FontPaths {
        public static final String ARIAL = "/fonts/arialmt.ttf";
        public static final String ARIAL_BOLD_ITALIC =
                "/fonts/arialmt.ttf";

        private FontPaths() {
        }
    }

    private static final ElementDataModel elementDataModel;
    private static final QualityDataModel qualityDataModel;
    private static final PropertyDataModel propertyDataModel;
    private static final ExternalInfluencesDataModel externalInfluencesDataModel;

    static {
        log.info("Components initialization...");

        try {
            ElementDao elementDao = new ElementDaoImpl();
            ElementQualityDao elementQualityDao = new ElementQualityDaoImpl();
            ElementTypeDao elementTypeDao = new ElementTypeDaoImpl();
            PropertyDao propertyDao = new PropertyDaoImpl();
            PropertyUnitDao propertyUnitDao = new PropertyUnitDaoImpl();
            PropertyValueDao propertyValueDao = new PropertyValueDaoImpl();
            ExternalInfluenceDao externalInfluenceDao = new ExternalInfluenceDaoImpl();
            ExternalInfluenceGroupDao externalInfluenceGroupDao = new ExternalInfluenceGroupDaoImpl();
            ExternalInfluenceLevelDao externalInfluenceLevelDao = new ExternalInfluenceLevelDaoImpl();

            ElementService elementService = new ElementServiceImpl(elementDao, elementTypeDao);
            ElementQualityService elementQualityService =
                    new ElementQualityServiceImpl(elementQualityDao, propertyDao, propertyUnitDao);
            PropertyService propertyService = new PropertyServiceImpl(propertyDao, propertyUnitDao);
            PropertyValueService propertyValueService = new PropertyValueServiceImpl(propertyValueDao);
            PdfExportService pdfExportService = new PdfExportService(
                    new ElementsTreePdfExportService(propertyValueService)
            );
            ExternalInfluenceService externalInfluenceService = new ExternalInfluenceServiceImpl(externalInfluenceDao);
            ExternalInfluenceGroupService externalInfluenceGroupService =
                    new ExternalInfluenceGroupServiceImpl(externalInfluenceGroupDao);
            ExternalInfluenceLevelService externalInfluenceLevelService =
                    new ExternalInfluenceLevelServiceImpl(externalInfluenceLevelDao);

            elementDataModel = new ElementDataModel(elementService, pdfExportService);
            qualityDataModel = new QualityDataModel(elementQualityService);
            propertyDataModel = new PropertyDataModel(elementService, elementQualityService, propertyService, propertyValueService);
            externalInfluencesDataModel = new ExternalInfluencesDataModel(externalInfluenceService,
                    externalInfluenceGroupService, externalInfluenceLevelService);

            log.info("Components were initialized");
        } catch (Exception e) {
            log.error("Components initialization error: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось инициализировать приложение", e);
        }
    }

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
        loader.setControllerFactory(createControllerFactory());
        return loader;
    }

    public static ElementDataModel getElementDataModel() {
        return elementDataModel;
    }

    public static QualityDataModel getQualityDataModel() {
        return qualityDataModel;
    }

    public static PropertyDataModel getPropertyDataModel() {
        return propertyDataModel;
    }

    public static ExternalInfluencesDataModel getExternalInfluencesDataModel() {
        return externalInfluencesDataModel;
    }

    public static void main(String[] args) {
        log.info("Launching app...");
        try {
            launch(args);
        } catch (Exception e) {
            log.error("Critical exception: {}", e.getMessage(), e);
        } finally {
            log.info("App was shut down");
        }
    }

    public static Callback<Class<?>, Object> getControllerFactory() {
        return createControllerFactory();
    }

    private static Callback<Class<?>, Object> createControllerFactory() {
        return controllerClass -> {
            try {
                return instantiateController(controllerClass);
            } catch (Exception e) {
                log.error("Can't instantiate controller {}: {}", controllerClass.getSimpleName(), e.getMessage(), e);
                throw new InstantiationError("Can't instantiate controller: " + controllerClass.getSimpleName());
            }
        };
    }

    private static Object instantiateController(Class<?> controllerClass) {
        log.debug("Try instantiate controller: {}", controllerClass.getSimpleName());

        if (ElementsTreeTabController.class.equals(controllerClass)) {
            return new ElementsTreeTabController(getElementDataModel());
        } else if (ElementQualitiesTabController.class.equals(controllerClass)) {
            return new ElementQualitiesTabController(getElementDataModel(), getQualityDataModel(), getPropertyDataModel());
        } else if (ElementFormController.class.equals(controllerClass)) {
            return new ElementFormController(getElementDataModel(), getQualityDataModel());
        } else if (ElementQualityFormController.class.equals(controllerClass)) {
            return new ElementQualityFormController(getQualityDataModel());
        } else if (ElementTypeFormController.class.equals(controllerClass)) {
            return new ElementTypeFormController(getElementDataModel());
        } else if (PropertyFormController.class.equals(controllerClass)) {
            return new PropertyFormController(getQualityDataModel(), getPropertyDataModel());
        } else if (PropertyUnitFormController.class.equals(controllerClass)) {
            return new PropertyUnitFormController(getPropertyDataModel());
        } else if (PreviewController.class.equals(controllerClass)) {
            return new PreviewController();
        } else if (ExternalInfluencesTabController.class.equals(controllerClass)) {
            return new ExternalInfluencesTabController(getExternalInfluencesDataModel());
        } else if (ExternalInfluenceFormController.class.equals(controllerClass)) {
            return new ExternalInfluenceFormController(getExternalInfluencesDataModel());
        } else if (ExternalInfluenceGroupFormController.class.equals(controllerClass)) {
            return new ExternalInfluenceGroupFormController(getExternalInfluencesDataModel());
        } else if (ExternalInfluenceLevelFormController.class.equals(controllerClass)) {
            return new ExternalInfluenceLevelFormController(getExternalInfluencesDataModel());
        } else if (ExternalInfluenceLevelsTabController.class.equals(controllerClass)) {
            return new ExternalInfluenceLevelsTabController(getExternalInfluencesDataModel(), getPropertyDataModel(),
                    getQualityDataModel());
        } else if (InheritPropertyFormController.class.equals(controllerClass)) {
            return new InheritPropertyFormController(getQualityDataModel(), getPropertyDataModel());
        } else {
            log.error("Unknown controller: {}", controllerClass.getSimpleName());
            throw new IllegalArgumentException("Unknown controller: " + controllerClass.getSimpleName());
        }
    }
}