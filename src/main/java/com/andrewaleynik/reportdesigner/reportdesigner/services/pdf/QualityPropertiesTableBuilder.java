package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.andrewaleynik.reportdesigner.reportdesigner.services.PropertyValueService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.util.*;
import java.util.stream.Collectors;

public class QualityPropertiesTableBuilder {

    private static final ExternalInfluence NO_INFLUENCE = createNoInfluencePlaceholder();

    private final PropertyValueService propertyValueService;

    public QualityPropertiesTableBuilder(PropertyValueService propertyValueService) {
        this.propertyValueService = propertyValueService;
    }

    public Table build(ElementQuality quality, PdfFont normalFont, PdfFont boldFont, int indent) {
        Table propsTable = createTable(indent);
        addHeaderRow(propsTable, boldFont);

        Map<Property, List<PropertyValue>> valuesByProperty = loadValuesGroupedByProperty(quality);
        for (Property property : quality.getProperties()) {
            addPropertyRows(propsTable, property, valuesByProperty, normalFont);
        }

        return propsTable;
    }

    public Paragraph createSectionHeader(PdfFont boldFont, int indent) {
        return new Paragraph("Свойства качества")
                .setFont(boldFont)
                .setFontSize(11)
                .setMarginLeft(indent)
                .setMarginBottom(5);
    }

    private Table createTable(int indent) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{25, 25, 50}));
        table.setWidth(UnitValue.createPercentValue(90));
        table.setMarginLeft(indent);
        table.setMarginBottom(15);
        return table;
    }

    private void addHeaderRow(Table table, PdfFont boldFont) {
        table.addHeaderCell(createCell("Свойство", boldFont, true, TextAlignment.CENTER));
        table.addHeaderCell(createCell("Внешнее воздействие", boldFont, true, TextAlignment.CENTER));
        table.addHeaderCell(createCell("Значения по уровням", boldFont, true, TextAlignment.CENTER));
    }

    private Map<Property, List<PropertyValue>> loadValuesGroupedByProperty(ElementQuality quality) {
        List<PropertyValue> propertyValues = quality.getProperties().stream()
                .map(propertyValueService::getPropertyValueOfProperty)
                .flatMap(List::stream)
                .toList();

        return propertyValues.stream()
                .collect(Collectors.groupingBy(PropertyValue::getProperty));
    }

    private void addPropertyRows(Table table, Property property,
                                 Map<Property, List<PropertyValue>> valuesByProperty,
                                 PdfFont normalFont) {
        List<PropertyValue> values = valuesByProperty.getOrDefault(property, Collections.emptyList());
        Map<ExternalInfluence, List<PropertyValue>> byInfluence = groupByInfluence(values);

        if (byInfluence.isEmpty()) {
            addEmptyPropertyRow(table, property, normalFont);
            return;
        }

        boolean firstRow = true;
        for (Map.Entry<ExternalInfluence, List<PropertyValue>> entry : byInfluence.entrySet()) {
            addInfluenceRow(table, property, entry.getKey(), entry.getValue(), normalFont, firstRow);
            firstRow = false;
        }
    }

    private Map<ExternalInfluence, List<PropertyValue>> groupByInfluence(List<PropertyValue> values) {
        return values.stream()
                .collect(Collectors.groupingBy(
                        pv -> pv.getExternalInfluence() != null ? pv.getExternalInfluence() : NO_INFLUENCE
                ));
    }

    private void addEmptyPropertyRow(Table table, Property property, PdfFont normalFont) {
        table.addCell(createCell(PropertyDisplayFormatter.format(property), normalFont, false, TextAlignment.LEFT));
        table.addCell(createCell("-", normalFont, false, TextAlignment.CENTER));
        table.addCell(createCell("-", normalFont, false, TextAlignment.CENTER));
    }

    private void addInfluenceRow(Table table, Property property, ExternalInfluence influence,
                                 List<PropertyValue> influenceValues, PdfFont normalFont, boolean firstRow) {
        String propertyCell = firstRow ? PropertyDisplayFormatter.format(property) : "";
        String influenceName = influence.getName() != null ? influence.getName() : "-";
        String valuesStr = formatLevelValues(influenceValues);

        table.addCell(createCell(propertyCell, normalFont, false, TextAlignment.LEFT));
        table.addCell(createCell(influenceName, normalFont, false, TextAlignment.LEFT));
        table.addCell(createCell(valuesStr, normalFont, false, TextAlignment.LEFT));
    }

    private String formatLevelValues(List<PropertyValue> influenceValues) {
        return influenceValues.stream()
                .sorted(Comparator.comparing(pv ->
                        pv.getExternalInfluenceLevel() != null
                                ? pv.getExternalInfluenceLevel().getName()
                                : ""))
                .map(pv -> {
                    String levelName = pv.getExternalInfluenceLevel() != null
                            ? pv.getExternalInfluenceLevel().getName()
                            : "Без уровня";
                    String val = pv.getValue() != null ? pv.getValue() : "-";
                    return levelName + ": " + val;
                })
                .collect(Collectors.joining("\n"));
    }

    private Cell createCell(String text, PdfFont font, boolean isHeader, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(8))
                .setPadding(4)
                .setTextAlignment(alignment);

        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }

        return cell;
    }

    private static ExternalInfluence createNoInfluencePlaceholder() {
        ExternalInfluence placeholder = new ExternalInfluence();
        placeholder.setName("Без воздействия");
        return placeholder;
    }
}
