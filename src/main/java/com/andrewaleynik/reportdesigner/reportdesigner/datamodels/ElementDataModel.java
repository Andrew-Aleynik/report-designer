package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ElementDataModel {
    private ObservableList<Element> elements = FXCollections.observableArrayList();
    private ObservableList<Element> rootElements = FXCollections.observableArrayList();
    private Element selectedParentElement;
    private ObservableList<ElementType> elementTypes = FXCollections.observableArrayList();

    private ElementType newElementType;

    private ElementService elementService;

    private Element newElement;

    public ElementDataModel(ElementService elementService) {
        this.elementService = elementService;
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

    public ElementService getElementService() {
        return elementService;
    }

    public void refreshElements() {
        elements.setAll(elementService.getAllElements());
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

    public void saveElement(Element element) {
        elementService.saveElement(element);
        refreshElements();
        refreshRootElements();
        refreshNewElement(element);
    }

    public void saveElementType(ElementType elementType) {
        elementService.saveElementType(elementType);
        refreshElementTypes();
    }
}
