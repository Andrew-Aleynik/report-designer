package com.andrewaleynik.reportdesigner.reportdesigner.config;

import com.andrewaleynik.reportdesigner.reportdesigner.controllers.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ControllerFactory implements Callback<Class<?>, Object> {

    private static final Logger log = LoggerFactory.getLogger(ControllerFactory.class);

    private final ApplicationContext context;
    private final Map<Class<?>, Function<ApplicationContext, Object>> registry = new HashMap<>();

    public ControllerFactory(ApplicationContext context) {
        this.context = context;
        registerControllers();
    }

    private void registerControllers() {
        registry.put(MainController.class, ctx -> new MainController());
        registry.put(ElementsTreeTabController.class,
                ctx -> new ElementsTreeTabController(ctx.getElementDataModel()));
        registry.put(ElementQualitiesTabController.class,
                ctx -> new ElementQualitiesTabController(
                        ctx.getElementDataModel(), ctx.getQualityDataModel(), ctx.getPropertyDataModel()));
        registry.put(ElementFormController.class,
                ctx -> new ElementFormController(ctx.getElementDataModel(), ctx.getQualityDataModel()));
        registry.put(ElementQualityFormController.class,
                ctx -> new ElementQualityFormController(ctx.getQualityDataModel()));
        registry.put(ElementTypeFormController.class,
                ctx -> new ElementTypeFormController(ctx.getElementDataModel()));
        registry.put(PropertyFormController.class,
                ctx -> new PropertyFormController(ctx.getQualityDataModel(), ctx.getPropertyDataModel()));
        registry.put(PropertyUnitFormController.class,
                ctx -> new PropertyUnitFormController(ctx.getPropertyDataModel()));
        registry.put(PropertyGroupFormController.class,
                ctx -> new PropertyGroupFormController(ctx.getPropertyDataModel()));
        registry.put(PropertyIndicatorFormController.class,
                ctx -> new PropertyIndicatorFormController(ctx.getPropertyDataModel()));
        registry.put(PreviewController.class, ctx -> new PreviewController());
        registry.put(ExternalInfluencesTabController.class,
                ctx -> new ExternalInfluencesTabController(ctx.getExternalInfluencesDataModel()));
        registry.put(ExternalInfluenceFormController.class,
                ctx -> new ExternalInfluenceFormController(ctx.getExternalInfluencesDataModel()));
        registry.put(ExternalInfluenceGroupFormController.class,
                ctx -> new ExternalInfluenceGroupFormController(ctx.getExternalInfluencesDataModel()));
        registry.put(ExternalInfluenceLevelFormController.class,
                ctx -> new ExternalInfluenceLevelFormController(
                        ctx.getExternalInfluencesDataModel(), ctx.getPropertyDataModel()));
        registry.put(ExternalInfluenceLevelsTabController.class,
                ctx -> new ExternalInfluenceLevelsTabController(
                        ctx.getExternalInfluencesDataModel(),
                        ctx.getPropertyDataModel(),
                        ctx.getQualityDataModel()));
        registry.put(InheritPropertyFormController.class,
                ctx -> new InheritPropertyFormController(ctx.getQualityDataModel(), ctx.getPropertyDataModel()));
    }

    @Override
    public Object call(Class<?> controllerClass) {
        Function<ApplicationContext, Object> factory = registry.get(controllerClass);
        if (factory == null) {
            log.error("Unknown controller: {}", controllerClass.getSimpleName());
            throw new IllegalArgumentException("Unknown controller: " + controllerClass.getSimpleName());
        }

        log.debug("Instantiating controller: {}", controllerClass.getSimpleName());
        return factory.apply(context);
    }
}
