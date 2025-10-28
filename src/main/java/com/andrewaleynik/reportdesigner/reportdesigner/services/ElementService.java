package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public interface ElementService {
    TreeSet<Element> getElementsTree(Element rootElement);

    List<Element> getRootElements();

    List<ElementType> getAllElementTypes();
    void saveElementType(ElementType elementType);

    List<ElementQuality> getAllElementQualities();

    Optional<Element> findElementById(Long id);

    void saveElement(Element element);

    void updateElement(Element element);

    void deleteElement(Element element);

    void validateElement(Element element);
}
