package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.dao.ExternalInfluenceDao;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExternalInfluenceServiceImpl implements ExternalInfluenceService {
    private final ExternalInfluenceDao externalInfluenceDao;
    private final Validator validator;

    public ExternalInfluenceServiceImpl(ExternalInfluenceDao externalInfluenceDao) {
        this.externalInfluenceDao = externalInfluenceDao;
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public List<ExternalInfluence> getAllExternalInfluences() {
        return externalInfluenceDao.findAll();
    }

    @Override
    public void saveExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluenceDao.save(externalInfluence);
    }

    @Override
    public void updateExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluenceDao.update(externalInfluence);
    }

    @Override
    public void deleteExternalInfluence(ExternalInfluence externalInfluence) {
        externalInfluenceDao.delete(externalInfluence);
    }

    @Override
    public void validateExternalInfluence(ExternalInfluence externalInfluence) {
        Set<ConstraintViolation<ExternalInfluence>> violations = validator.validate(externalInfluence);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }
    }
}
