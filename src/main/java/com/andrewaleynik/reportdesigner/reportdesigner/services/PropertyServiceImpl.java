package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyDao;
import com.andrewaleynik.reportdesigner.reportdesigner.dao.PropertyUnitDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyUnit;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyServiceImpl implements PropertyService {
    private final PropertyDao propertyDao;
    private final PropertyUnitDao propertyUnitDao;

    private final Validator validator;

    public PropertyServiceImpl(PropertyDao propertyDao, PropertyUnitDao propertyUnitDao) {
        this.propertyDao = propertyDao;
        this.propertyUnitDao = propertyUnitDao;
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public List<Property> getPropertiesOf(ElementQuality quality) {
        return propertyDao.findAll().stream()
                .filter(property -> property.getQualities().contains(quality))
                .toList();
    }

    @Override
    public List<PropertyUnit> getPropertyUnits() {
        return propertyUnitDao.findAll();
    }

    @Override
    public void saveProperty(Property property) {
        propertyDao.save(property);
    }

    @Override
    public Optional<Property> findPropertyById(Long id) {
        return propertyDao.findById(id);
    }

    @Override
    public void updateProperty(Property property) {
        propertyDao.update(property);
    }

    @Override
    public void deleteProperty(Property property) {
        propertyDao.delete(property);
    }

    @Override
    public void validateProperty(Property property) {
        Set<ConstraintViolation<Property>> violations = validator.validate(property);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }
    }

    @Override
    public void savePropertyUnit(PropertyUnit propertyUnit) {
        propertyUnitDao.save(propertyUnit);
    }
}
