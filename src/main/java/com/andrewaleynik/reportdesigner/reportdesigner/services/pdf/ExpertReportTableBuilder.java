package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ExternalInfluence;
import com.andrewaleynik.reportdesigner.reportdesigner.models.Property;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyGroup;
import com.andrewaleynik.reportdesigner.reportdesigner.models.PropertyValue;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class ExpertReportTableBuilder {

    private final PropertyValueService propertyValueService;

    public ExpertReportTableBuilder(PropertyValueService propertyValueService) {
        this.propertyValueService = propertyValueService;
    }

    public Table buildRealInfluencesTable(TreeSet<Element> elementsTree,
                                          PdfFont normalFont,
                                          PdfFont boldFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{6, 54, 12, 14, 14}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(ExpertTableStyle.headerCell("#", boldFont, TextAlignment.CENTER));
        table.addHeaderCell(ExpertTableStyle.headerCell("Реальные воздействия", boldFont, TextAlignment.LEFT));
        table.addHeaderCell(ExpertTableStyle.headerCell("Ед. изм.", boldFont, TextAlignment.CENTER));
        table.addHeaderCell(ExpertTableStyle.headerCell("Вв. зн.", boldFont, TextAlignment.CENTER));
        table.addHeaderCell(ExpertTableStyle.headerCell("Норм. зн.", boldFont, TextAlignment.CENTER));

        List<ExternalInfluence> influences = collectInfluences(elementsTree);
        int index = 1;
        for (ExternalInfluence influence : influences) {
            table.addCell(ExpertTableStyle.bodyCell(String.valueOf(index++), normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell(formatInfluenceTitle(influence), normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.CENTER));
        }

        if (influences.isEmpty()) {
            table.addCell(ExpertTableStyle.bodyCell("1", normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell("Внешние воздействия не заданы", normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.CENTER));
        }

        return table;
    }

    public Table buildRequirementsTable(TreeSet<Element> elementsTree,
                                        PdfFont normalFont,
                                        PdfFont boldFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{6, 30, 16, 16, 18, 14}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(ExpertTableStyle.headerCell("#", boldFont, TextAlignment.CENTER));
        table.addHeaderCell(ExpertTableStyle.headerCell(
                "Свойства покр. и моделирующее возд-ие", boldFont, TextAlignment.LEFT));
        table.addHeaderCell(ExpertTableStyle.headerCell("Реж. при испытаниях", boldFont, TextAlignment.LEFT));
        table.addHeaderCell(ExpertTableStyle.headerCell("Показатель св-ва", boldFont, TextAlignment.LEFT));
        table.addHeaderCell(ExpertTableStyle.headerCell("Норма на показатель", boldFont, TextAlignment.LEFT));
        table.addHeaderCell(ExpertTableStyle.headerCell("Метод контроля", boldFont, TextAlignment.LEFT));

        List<RequirementsRow> rows = buildRequirementRows(elementsTree);
        for (RequirementsRow row : rows) {
            table.addCell(ExpertTableStyle.bodyCell(row.number(), normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell(row.propertyAndInfluence(), normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell(row.testRegime(), normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell(row.propertyIndicator(), normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell(row.norm(), normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell(row.controlMethod(), normalFont, TextAlignment.LEFT));
        }

        if (rows.isEmpty()) {
            table.addCell(ExpertTableStyle.bodyCell("1.1", normalFont, TextAlignment.CENTER));
            table.addCell(ExpertTableStyle.bodyCell("Свойства не заданы", normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.LEFT));
            table.addCell(ExpertTableStyle.bodyCell("—", normalFont, TextAlignment.LEFT));
        }

        return table;
    }

    private List<RequirementsRow> buildRequirementRows(TreeSet<Element> elementsTree) {
        List<RequirementsRow> rows = new ArrayList<>();
        Map<PropertyGroupKey, List<Map.Entry<Property, List<PropertyValue>>>> grouped =
                groupPropertiesByPropertyGroup(collectPropertiesWithValues(elementsTree));

        int groupIndex = 1;
        for (List<Map.Entry<Property, List<PropertyValue>>> groupEntries : grouped.values()) {
            AtomicInteger rowIndex = new AtomicInteger(1);
            for (Map.Entry<Property, List<PropertyValue>> entry : groupEntries) {
                rows.addAll(RequirementsRow.fromProperty(
                        groupIndex, rowIndex, entry.getKey(), entry.getValue()));
            }
            groupIndex++;
        }
        return rows;
    }

    private Map<PropertyGroupKey, List<Map.Entry<Property, List<PropertyValue>>>> groupPropertiesByPropertyGroup(
            Map<Property, List<PropertyValue>> properties) {
        Map<PropertyGroupKey, List<Map.Entry<Property, List<PropertyValue>>>> grouped = new LinkedHashMap<>();
        List<Map.Entry<Property, List<PropertyValue>>> sorted = properties.entrySet().stream()
                .sorted(Comparator
                        .comparing((Map.Entry<Property, List<PropertyValue>> e) -> groupSortName(e.getKey()),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(e -> nullToEmpty(e.getKey().getName()), String.CASE_INSENSITIVE_ORDER))
                .toList();

        for (Map.Entry<Property, List<PropertyValue>> entry : sorted) {
            grouped.computeIfAbsent(PropertyGroupKey.of(entry.getKey()), key -> new ArrayList<>())
                    .add(entry);
        }
        return grouped;
    }

    /**
     * Collects unique properties from the tree once. Shared qualities on parent/child
     * must not duplicate property values in the report.
     */
    private Map<Property, List<PropertyValue>> collectPropertiesWithValues(TreeSet<Element> elementsTree) {
        Map<Property, List<PropertyValue>> result = new LinkedHashMap<>();
        Set<Long> seenPropertyIds = new HashSet<>();

        for (Element element : elementsTree) {
            ElementQuality quality = element.getQuality();
            if (quality == null || quality.getProperties() == null) {
                continue;
            }
            List<Property> properties = quality.getProperties().stream()
                    .sorted(Comparator.comparing(
                            p -> nullToEmpty(p.getName()),
                            String.CASE_INSENSITIVE_ORDER))
                    .toList();
            for (Property property : properties) {
                if (property.getId() != null && !seenPropertyIds.add(property.getId())) {
                    continue;
                }
                if (property.getId() == null && result.containsKey(property)) {
                    continue;
                }
                result.put(property, new ArrayList<>(
                        propertyValueService.getPropertyValueOfProperty(property)));
            }
        }
        return result;
    }

    private List<ExternalInfluence> collectInfluences(TreeSet<Element> elementsTree) {
        Set<ExternalInfluence> influences = new LinkedHashSet<>();
        for (List<PropertyValue> values : collectPropertiesWithValues(elementsTree).values()) {
            for (PropertyValue value : values) {
                if (value.getExternalInfluence() != null) {
                    influences.add(value.getExternalInfluence());
                }
            }
        }
        return influences.stream()
                .sorted(Comparator.comparing(
                        this::formatInfluenceTitle,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private String formatInfluenceTitle(ExternalInfluence influence) {
        String name = influence.getName();
        String description = influence.getDescription();
        if (description != null && !description.isBlank()) {
            return description.trim();
        }
        if (name != null && !name.isBlank()) {
            return name.trim();
        }
        return "—";
    }

    private static String groupSortName(Property property) {
        PropertyGroup group = property.getPropertyGroup();
        return group != null && group.getName() != null ? group.getName() : "";
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record PropertyGroupKey(Long id, String name) {
        static PropertyGroupKey of(Property property) {
            PropertyGroup group = property.getPropertyGroup();
            if (group == null) {
                return new PropertyGroupKey(null, "");
            }
            return new PropertyGroupKey(group.getId(), group.getName() != null ? group.getName() : "");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PropertyGroupKey that)) {
                return false;
            }
            if (id != null || that.id != null) {
                return Objects.equals(id, that.id);
            }
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : Objects.hashCode(name);
        }
    }
}
