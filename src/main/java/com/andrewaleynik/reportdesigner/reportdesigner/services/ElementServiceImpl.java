package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.ElementTypeDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementType;
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

        TreeSet<Element> treeSet = new TreeSet<>(Comparator.comparing(Element::getLevel)
                .thenComparing(Element::getName));

        buildTree(rootElement, treeSet);
        return treeSet;
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
        Optional<Element> existingElement = elementDao.findById(element.getId());
        if (existingElement.isEmpty()) {
            throw new IllegalArgumentException("Element with id " + element.getId() + " not found");
        }

        if (!element.getChildren().isEmpty()) {
            deleteChildrenRecursively(element);
        }

        elementDao.delete(element);
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

    private void buildTree(Element element, TreeSet<Element> treeSet) {
        treeSet.add(element);

        if (element.getChildren() != null && !element.getChildren().isEmpty()) {
            for (Element child : element.getChildren()) {
                buildTree(child, treeSet);
            }
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

    private void deleteChildrenRecursively(Element parent) {
        if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            List<Element> children = new ArrayList<>(parent.getChildren());
            for (Element child : children) {
                deleteChildrenRecursively(child);
                elementDao.delete(child);
            }
        }
    }
}
