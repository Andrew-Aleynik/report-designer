package com.andrewaleynik.reportdesigner.reportdesigner.datamodels;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.services.ElementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ElementDataModel {
    private final ObservableList<Element> elements = FXCollections.observableArrayList();
    private final ObservableList<Element> rootElements = FXCollections.observableArrayList();
    private Element selectedParentElement;
    private final ObservableList<ElementType> elementTypes = FXCollections.observableArrayList();
    private ElementType newElementType;
    private Element newElement;
    private Element selectedEditElement;
    private final ElementService elementService;

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

    public void saveElementType(ElementType elementType) {
        elementService.saveElementType(elementType);
        refreshElementTypes();
    }
}
