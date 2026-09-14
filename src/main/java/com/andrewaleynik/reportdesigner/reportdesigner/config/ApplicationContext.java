package com.andrewaleynik.reportdesigner.reportdesigner.config;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.*;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.impl.*;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ElementDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.ExternalInfluencesDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.PropertyDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.datamodels.QualityDataModel;
import com.andrewaleynik.reportdesigner.reportdesigner.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplicationContext {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);
    private static final ApplicationContext INSTANCE = new ApplicationContext();

    private final ElementDataModel elementDataModel;
    private final QualityDataModel qualityDataModel;
    private final PropertyDataModel propertyDataModel;
    private final ExternalInfluencesDataModel externalInfluencesDataModel;

    private ApplicationContext() {
        log.info("Components initialization...");

        ElementDao elementDao = new ElementDaoImpl();
        ElementQualityDao elementQualityDao = new ElementQualityDaoImpl();
        ElementTypeDao elementTypeDao = new ElementTypeDaoImpl();
        PropertyDao propertyDao = new PropertyDaoImpl();
        PropertyUnitDao propertyUnitDao = new PropertyUnitDaoImpl();
        PropertyGroupDao propertyGroupDao = new PropertyGroupDaoImpl();
        PropertyValueDao propertyValueDao = new PropertyValueDaoImpl();
        ExternalInfluenceDao externalInfluenceDao = new ExternalInfluenceDaoImpl();
        ExternalInfluenceGroupDao externalInfluenceGroupDao = new ExternalInfluenceGroupDaoImpl();
        ExternalInfluenceLevelDao externalInfluenceLevelDao = new ExternalInfluenceLevelDaoImpl();

        ElementService elementService = new ElementServiceImpl(elementDao, elementTypeDao);
        ElementQualityService elementQualityService =
                new ElementQualityServiceImpl(elementQualityDao, propertyDao, propertyUnitDao);
        PropertyService propertyService = new PropertyServiceImpl(
                propertyDao, propertyUnitDao, propertyGroupDao);
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
        propertyDataModel = new PropertyDataModel(
                elementService, propertyService, propertyValueService);
        externalInfluencesDataModel = new ExternalInfluencesDataModel(
                externalInfluenceService, externalInfluenceGroupService, externalInfluenceLevelService);

        log.info("Components were initialized");
    }

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public ElementDataModel getElementDataModel() {
        return elementDataModel;
    }

    public QualityDataModel getQualityDataModel() {
        return qualityDataModel;
    }

    public PropertyDataModel getPropertyDataModel() {
        return propertyDataModel;
    }

    public ExternalInfluencesDataModel getExternalInfluencesDataModel() {
        return externalInfluencesDataModel;
    }
}
