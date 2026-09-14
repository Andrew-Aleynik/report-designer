package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementTypeDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
import com.andrewaleynik.reportdesigner.reportdesigner.util.ElementTreeBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.*;
import java.util.stream.Collectors;

public class ElementServiceImpl implements ElementService {
    private final ElementDao elementDao;
    private final ElementTypeDao elementTypeDao;
    private final Validator validator;

    public ElementServiceImpl(ElementDao elementDao, ElementTypeDao elementTypeDao) {
        this.elementDao = elementDao;
        this.elementTypeDao = elementTypeDao;
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public TreeSet<Element> getElementsTree(Element rootElement) {
        if (rootElement == null) {
            throw new IllegalArgumentException("Root element cannot be null");
        }

        return ElementTreeBuilder.buildFromRoot(rootElement);
    }

    @Override
    public List<Element> getAllElements() {
        return elementDao.findAll();
    }

    @Override
    public List<Element> getRootElements() {
        return elementDao.findRoots();
    }

    @Override
    public List<ElementType> getAllElementTypes() {
        return elementTypeDao.findAll();
    }

    @Override
    public void saveElementType(ElementType elementType) {
        elementTypeDao.save(elementType);
    }

    @Override
    public Optional<Element> findElementById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return elementDao.findById(id);
    }

    @Override
    public Optional<Element> findElementByQualityId(Long qualityId) {
        if (qualityId == null) {
            return Optional.empty();
        }
        return elementDao.findAll().stream()
                .filter(element -> element.getQuality() != null
                        && qualityId.equals(element.getQuality().getId()))
                .findFirst();
    }

    @Override
    public void saveElement(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("Element must be present!");
        }
        validateElement(element);
        if (element.getParent() != null) {
            element.setLevel(element.getParent().getLevel() + 1);
        } else {
            element.setLevel(1);
        }
        elementDao.save(element);
        if (!element.getChildren().isEmpty()) {
            updateChildrenLevels(element);
        }
    }

    @Override
    public void updateElement(Element element) {
        validateElement(element);
        Optional<Element> existingElement = elementDao.findById(element.getId());
        if (existingElement.isEmpty()) {
            throw new IllegalArgumentException("Element with id " + element.getId() + " not found");
        }
        if (element.getParent() != null) {
            element.setLevel(element.getParent().getLevel() + 1);
        } else {
            element.setLevel(1);
        }

        elementDao.update(element);
        updateChildrenLevels(element);
    }

    @Override
    public void deleteElement(Element element) {
        Element existing = elementDao.findById(element.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Element with id " + element.getId() + " not found"));

        Element parent = existing.getParent();
        if (parent != null) {
            parent.getChildren().remove(existing);
            elementDao.update(parent);
        } else {
            elementDao.delete(existing);
        }
    }

    @Override
    public void validateElement(Element element) {
        Set<ConstraintViolation<Element>> violations = validator.validate(element);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }
    }

    private void updateChildrenLevels(Element parent) {
        if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            for (Element child : parent.getChildren()) {
                child.setLevel(parent.getLevel() + 1);
                elementDao.update(child);
                updateChildrenLevels(child);
            }
        }
    }
}
