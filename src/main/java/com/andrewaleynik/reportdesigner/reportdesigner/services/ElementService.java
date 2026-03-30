package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public interface ElementService {
    TreeSet<Element> getElementsTree(Element rootElement);

    List<Element> getAllElements();

    List<Element> getRootElements();

    List<ElementType> getAllElementTypes();

    void saveElementType(ElementType elementType);

    Optional<Element> findElementById(Long id);

    Optional<Element> findElementByQualityId(Long qualityId);

    void saveElement(Element element);

    void updateElement(Element element);

    void deleteElement(Element element);

    void validateElement(Element element);
}
