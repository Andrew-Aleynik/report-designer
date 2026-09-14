package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;

public final class PropertyDisplayFormatter {

    private PropertyDisplayFormatter() {
    }

    public static String format(Property property) {
        StringBuilder display = new StringBuilder();
        display.append(property.getName() != null ? property.getName() : "Без названия");

        if (property.getUnit() != null) {
            display.append("\nЕд.изм.: ").append(property.getUnit().getName());
        }
        if (property.getQualityCriterionValue() != null) {
            display.append("\nКритерий: ").append(property.getQualityCriterionValue());
        }

        return display.toString();
    }
}
