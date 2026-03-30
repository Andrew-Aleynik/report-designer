package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PdfExportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Optional;

public class ElementDataModel {
    private final ObservableList<Element> elements = FXCollections.observableArrayList();
    private final ObservableList<Element> rootElements = FXCollections.observableArrayList();
    private Element selectedParentElement;
    private final ObservableList<ElementType> elementTypes = FXCollections.observableArrayList();
    private ElementType newElementType;
    private Element newElement;
    private Element selectedEditElement;
    private final ElementService elementService;
    private final PdfExportService pdfExportService;

    public ElementDataModel(ElementService elementService, PdfExportService pdfExportService) {
        this.elementService = elementService;
        this.pdfExportService = pdfExportService;
    }

    public ObservableList<Element> getElements() {
        return elements;
    }

    public ObservableList<Element> getRootElements() {
        return rootElements;
    }

    public Element getSelectedParentElement() {
        return selectedParentElement;
    }

    public ObservableList<ElementType> getElementTypes() {
        return elementTypes;
    }

    public ElementType getNewElementType() {
        return newElementType;
    }

    public Element getNewElement() {
        return newElement;
    }

    public Element getSelectedEditElement() {
        return selectedEditElement;
    }

    public ElementService getElementService() {
        return elementService;
    }

    public void refreshRootElements() {
        rootElements.setAll(elementService.getRootElements());
    }

    public void refreshSelectedParentElement(Element parent) {
        selectedParentElement = parent;
    }

    public void refreshElementTypes() {
        elementTypes.setAll(elementService.getAllElementTypes());
    }

    public void refreshNewElementType(ElementType elementType) {
        this.newElementType = elementType;
    }

    public void refreshNewElement(Element element) {
        this.newElement = element;
    }

    public void refreshSelectedEditElement(Element editElement) {
        this.selectedEditElement = editElement;
    }

    public void saveElement(Element element) {
        elementService.saveElement(element);
        refreshRootElements();
        refreshNewElement(element);
    }

    public void updateElement(Element element) {
        elementService.updateElement(element);
        refreshRootElements();
    }

    public void deleteElement(Element element) {
        elementService.deleteElement(element);
    }

    public void saveElementType(ElementType elementType) {
        elementService.saveElementType(elementType);
        refreshElementTypes();
    }

    public File exportElementsTreeToPdf(Element root) {
        return pdfExportService.exportElementsTree(root);
    }

    public Optional<Element> findElementByQuality(ElementQuality quality) {
        return elementService.findElementByQualityId(quality.getId());
    }
}
