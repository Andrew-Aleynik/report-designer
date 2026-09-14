package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluenceLevel;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class RequirementsRow {

    private final String number;
    private final String propertyAndInfluence;
    private final String testRegime;
    private final String propertyIndicator;
    private final String norm;
    private final String controlMethod;

    public RequirementsRow(String number,
                           String propertyAndInfluence,
                           String testRegime,
                           String propertyIndicator,
                           String norm,
                           String controlMethod) {
        this.number = number;
        this.propertyAndInfluence = propertyAndInfluence;
        this.testRegime = testRegime;
        this.propertyIndicator = propertyIndicator;
        this.norm = norm;
        this.controlMethod = controlMethod;
    }

    public String number() {
        return number;
    }

    public String propertyAndInfluence() {
        return propertyAndInfluence;
    }

    public String testRegime() {
        return testRegime;
    }

    public String propertyIndicator() {
        return propertyIndicator;
    }

    public String norm() {
        return norm;
    }

    public String controlMethod() {
        return controlMethod;
    }

    public static List<RequirementsRow> fromProperty(int propertyIndex,
                                                     Property property,
                                                     List<PropertyValue> values) {
        return fromProperty(propertyIndex, new AtomicInteger(1), property, values);
    }

    public static List<RequirementsRow> fromProperty(int groupIndex,
                                                     AtomicInteger rowIndex,
                                                     Property property,
                                                     List<PropertyValue> values) {
        List<RequirementsRow> rows = new ArrayList<>();
        if (values == null || values.isEmpty()) {
            rows.add(new RequirementsRow(
                    groupIndex + "." + rowIndex.getAndIncrement(),
                    formatPropertyAndInfluence(property, null),
                    "—",
                    formatIndicator(property),
                    nullToDash(property.getQualityCriterionValue()),
                    "—"
            ));
            return rows;
        }

        var byInfluence = values.stream()
                .collect(Collectors.groupingBy(
                        pv -> pv.getExternalInfluence(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<ExternalInfluence> influences = byInfluence.keySet().stream()
                .sorted(Comparator.comparing(
                        influence -> influenceTitle(influence),
                        String.CASE_INSENSITIVE_ORDER))
                .toList();

        for (ExternalInfluence influence : influences) {
            List<PropertyValue> influenceValues = byInfluence.get(influence);
            rows.add(new RequirementsRow(
                    groupIndex + "." + rowIndex.getAndIncrement(),
                    formatPropertyAndInfluence(property, influence),
                    formatRegime(influenceValues),
                    formatIndicator(property),
                    nullToDash(property.getQualityCriterionValue()),
                    "—"
            ));
        }
        return rows;
    }

    private static String formatPropertyAndInfluence(Property property, ExternalInfluence influence) {
        StringBuilder text = new StringBuilder();
        text.append(property.getName() != null ? property.getName() : "Без названия");
        if (influence == null) {
            return text.toString();
        }

        String influenceText = influenceTitle(influence);
        if (!influenceText.isBlank()) {
            text.append(": ").append(influenceText);
        }
        return text.toString();
    }

    private static String influenceTitle(ExternalInfluence influence) {
        if (influence == null) {
            return "";
        }
        String description = influence.getDescription();
        if (description != null && !description.isBlank()) {
            return description.trim();
        }
        String name = influence.getName();
        if (name != null && !name.isBlank()) {
            return name.trim();
        }
        return "";
    }

    private static String formatRegime(List<PropertyValue> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        RequirementsRow::levelKey,
                        pv -> pv,
                        (first, second) -> first,
                        LinkedHashMap::new))
                .values()
                .stream()
                .sorted(Comparator.comparing(pv -> {
                    ExternalInfluenceLevel level = pv.getExternalInfluenceLevel();
                    return level != null && level.getName() != null ? level.getName() : "";
                }, String.CASE_INSENSITIVE_ORDER))
                .map(pv -> {
                    String level = pv.getExternalInfluenceLevel() != null
                            && pv.getExternalInfluenceLevel().getName() != null
                            ? pv.getExternalInfluenceLevel().getName().trim()
                            : "Без уровня";
                    String value = pv.getValue() != null && !pv.getValue().isBlank()
                            ? pv.getValue().trim()
                            : "—";
                    return level + " " + value;
                })
                .collect(Collectors.joining("\n"));
    }

    private static Object levelKey(PropertyValue propertyValue) {
        ExternalInfluenceLevel level = propertyValue.getExternalInfluenceLevel();
        if (level == null) {
            return "null-level";
        }
        if (level.getId() != null) {
            return level.getId();
        }
        return level.getName() != null ? level.getName() : "unnamed-level";
    }

    private static String formatIndicator(Property property) {
        if (property.getUnit() != null && property.getUnit().getName() != null) {
            return property.getUnit().getName();
        }
        return property.getName() != null ? property.getName() : "—";
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
