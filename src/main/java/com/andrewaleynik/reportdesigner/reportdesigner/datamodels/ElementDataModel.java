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

public class ElementDataModel extends ObservableDataModel {

    private final ObservableList<Element> rootElements = FXCollections.observableArrayList();
    private final ObservableList<ElementType> elementTypes = FXCollections.observableArrayList();
    private Element selectedParentElement;
    private ElementType newElementType;
    private Element newElement;
    private Element selectedEditElement;
    private final ElementService elementService;
    private final PdfExportService pdfExportService;

    public ElementDataModel(ElementService elementService, PdfExportService pdfExportService) {
        this.elementService = elementService;
        this.pdfExportService = pdfExportService;
        refreshElementTypes();
        refreshRootElements();
    }

    public ObservableList<Element> getRootElements() {
        return rootElements;
    }

    public Element getSelectedParentElement() {
        return selectedParentElement;
    }

    public void setSelectedParentElement(Element parent) {
        selectedParentElement = parent;
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

    public void setSelectedEditElement(Element editElement) {
        this.selectedEditElement = editElement;
    }

    public void refreshRootElements() {
        rootElements.setAll(elementService.getRootElements());
        fireChanged();
    }

    public void refreshElementTypes() {
        elementTypes.setAll(elementService.getAllElementTypes());
        fireChanged();
    }

    public void saveElement(Element element) {
        elementService.saveElement(element);
        newElement = element;
        refreshRootElements();
    }

    public void updateElement(Element element) {
        elementService.updateElement(element);
        refreshRootElements();
    }

    public void deleteElement(Element element) {
        elementService.deleteElement(element);
        fireChanged();
    }

    public void saveElementType(ElementType elementType) {
        elementService.saveElementType(elementType);
        newElementType = elementType;
        refreshElementTypes();
    }

    public File exportElementsTreeToPdf(Element root) {
        return pdfExportService.exportElementsTree(root);
    }

    public Optional<Element> findElementByQuality(ElementQuality quality) {
        if (quality == null || quality.getId() == null) {
            return Optional.empty();
        }
        return elementService.findElementByQualityId(quality.getId());
    }
}
