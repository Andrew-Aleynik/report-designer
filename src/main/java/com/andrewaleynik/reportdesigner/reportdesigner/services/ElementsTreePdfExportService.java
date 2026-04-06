package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.App;
import com.andrewaleynik.reportdesigner.reportdesigner.models.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ElementsTreePdfExportService implements ExportService<TreeSet<Element>> {
    private final PropertyValueService propertyValueService;

    public ElementsTreePdfExportService(PropertyValueService propertyValueService) {
        this.propertyValueService = propertyValueService;
    }

    @Override
    public File export(TreeSet<Element> elementsTree) {
        if (elementsTree == null || elementsTree.isEmpty()) {
            throw new IllegalArgumentException("Дерево элементов не может быть пустым");
        }

        try {
            File tempFile = File.createTempFile("system_", ".pdf");
            tempFile.deleteOnExit();

            String filePath = tempFile.getAbsolutePath();
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont headerFont = loadFont(App.FontPaths.ARIAL_BOLD_ITALIC);
            PdfFont normalFont = loadFont(App.FontPaths.ARIAL);
            PdfFont boldFont = loadFont(App.FontPaths.ARIAL_BOLD_ITALIC);

            addReportHeader(document, headerFont, normalFont);

            addTreeStatistics(document, elementsTree, boldFont, normalFont);

            document.add(new Paragraph("\n"));

            addElementsTree(document, elementsTree, headerFont, normalFont, boldFont);

            document.close();
            return tempFile;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF файла: " + e.getMessage(), e);
        }
    }

    private void addReportHeader(Document document, PdfFont headerFont, PdfFont normalFont) {
        Paragraph title = new Paragraph("Отчет по системе")
                .setFont(headerFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);

        Paragraph date = new Paragraph("Создан: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(30);
        document.add(date);

        Paragraph subtitle = new Paragraph("Элементы системы")
                .setFont(headerFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(20);
        document.add(subtitle);
    }

    private void addTreeStatistics(Document document, TreeSet<Element> elementsTree,
                                   PdfFont boldFont, PdfFont normalFont) {
        int totalElements = elementsTree.size();
        int elementsWithQuality = countElementsWithQuality(elementsTree);

        Table statsTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}));
        statsTable.setWidth(UnitValue.createPercentValue(90));

        addStatRow(statsTable, "Общее количество элементов:", String.valueOf(totalElements), boldFont, normalFont);
        addStatRow(statsTable, "Элементов с качеством:", String.valueOf(elementsWithQuality), boldFont, normalFont);
        addStatRow(statsTable, "Элементов без качества:", String.valueOf(totalElements - elementsWithQuality), boldFont, normalFont);

        document.add(statsTable);
    }

    private int countElementsWithQuality(TreeSet<Element> elements) {
        return (int) elements.stream()
                .filter(element -> element.getQuality() != null)
                .count();
    }

    private int calculateMaxDepth(TreeSet<Element> elements) {
        int maxDepth = 0;
        for (Element element : elements) {
            maxDepth = Math.max(maxDepth, calculateElementDepth(element, 1));
        }
        return maxDepth;
    }

    private int calculateElementDepth(Element element, int currentDepth) {
        int maxDepth = currentDepth;
        for (Element child : element.getChildren()) {
            maxDepth = Math.max(maxDepth, calculateElementDepth(child, currentDepth + 1));
        }
        return maxDepth;
    }

    private void addStatRow(Table table, String label, String value, PdfFont labelFont, PdfFont valueFont) {
        table.addCell(createCell(label, labelFont, true, TextAlignment.LEFT));
        table.addCell(createCell(value, valueFont, false, TextAlignment.RIGHT));
    }

    private void addElementsTree(Document document, TreeSet<Element> elementsTree,
                                 PdfFont headerFont, PdfFont normalFont, PdfFont boldFont) throws IOException {
        for (Element element : elementsTree) {
            addElementWithHierarchy(document, element, headerFont, normalFont, boldFont, 0);
        }
    }

    private void addElementWithHierarchy(Document document, Element element,
                                         PdfFont headerFont, PdfFont normalFont, PdfFont boldFont,
                                         int level) throws IOException {
        int indent = level * 15;

        Paragraph elementParagraph = new Paragraph(element.getName())
                .setFont(level == 0 ? headerFont : boldFont)
                .setFontSize(14)
                .setMarginLeft(indent)
                .setMarginTop(level == 0 ? 10 : 5)
                .setMarginBottom(3);
        document.add(elementParagraph);

        addElementDetails(document, element, normalFont, indent);

        if (element.getQuality() != null) {
            addQualityDetails(document, element.getQuality(), normalFont, boldFont, indent);
        }

        for (Element child : element.getChildren()) {
            addElementWithHierarchy(document, child, headerFont, normalFont, boldFont, level + 1);
        }
    }

    private void addElementDetails(Document document, Element element, PdfFont font, int indent) {
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{25, 75}));
        detailsTable.setWidth(UnitValue.createPercentValue(70));
        detailsTable.setMarginLeft(indent);
        detailsTable.setMarginBottom(5);

        addDetailRow(detailsTable, "Код:", element.getCode(), font);
        addDetailRow(detailsTable, "Тип:",
                element.getType() != null ? element.getType().getName() : "Не указан", font);
        addDetailRow(detailsTable, "Описание:", element.getDescription(), font);

        document.add(detailsTable);
    }

    private void addQualityDetails(Document document, ElementQuality quality,
                                   PdfFont normalFont, PdfFont boldFont, int indent) throws IOException {
        Paragraph qualityLabel = new Paragraph("Качество: " + quality.getCode())
                .setFont(boldFont)
                .setFontSize(10)
                .setMarginLeft(indent)
                .setMarginBottom(3);
        document.add(qualityLabel);

        Table qualityTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}));
        qualityTable.setWidth(UnitValue.createPercentValue(70));
        qualityTable.setMarginLeft(indent);
        qualityTable.setMarginBottom(8);

        if (quality.getServiceLife() != null) {
            addDetailRow(qualityTable, "Срок службы:",
                    quality.getServiceLife().toDays() + " дней", normalFont);
        }

        if (quality.getSatisfyingCost() != null) {
            addDetailRow(qualityTable, "Удовл. стоимость:",
                    quality.getSatisfyingCost().toString(), normalFont);
        }

        if (quality.getActualCost() != null) {
            addDetailRow(qualityTable, "Факт. стоимость:",
                    quality.getActualCost().toString(), normalFont);
        }

        document.add(qualityTable);

        if (quality.getProperties() != null && !quality.getProperties().isEmpty()) {
            addQualityProperties(document, quality, normalFont, boldFont, indent);
        }
    }

    private void addQualityProperties(Document document, ElementQuality quality,
                                      PdfFont normalFont, PdfFont boldFont, int indent) throws IOException {

        // Заголовок раздела свойств
        Paragraph propsHeader = new Paragraph("Свойства качества")
                .setFont(boldFont)
                .setFontSize(11)
                .setMarginLeft(indent)
                .setMarginBottom(5);
        document.add(propsHeader);

        // Получаем PropertyValues для этого качества
        List<PropertyValue> propertyValues = quality.getProperties().stream()
                .map(propertyValueService::getPropertyValueOfProperty)
                .flatMap(List::stream)
                .toList();

        // Группируем по Property
        Map<Property, List<PropertyValue>> valuesByProperty = propertyValues.stream()
                .collect(Collectors.groupingBy(PropertyValue::getProperty));

        // Таблица: Property | ExternalInfluence | Уровни и значения
        Table propsTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 50}));
        propsTable.setWidth(UnitValue.createPercentValue(90));
        propsTable.setMarginLeft(indent);
        propsTable.setMarginBottom(15);

        // Заголовки
        propsTable.addHeaderCell(createCell("Свойство", boldFont, true, TextAlignment.CENTER));
        propsTable.addHeaderCell(createCell("Внешнее воздействие", boldFont, true, TextAlignment.CENTER));
        propsTable.addHeaderCell(createCell("Значения по уровням", boldFont, true, TextAlignment.CENTER));

        // Данные
        for (Property property : quality.getProperties()) {
            List<PropertyValue> values = valuesByProperty.getOrDefault(property, Collections.emptyList());

            // Группируем значения по ExternalInfluence
            Map<ExternalInfluence, List<PropertyValue>> byInfluence = values.stream()
                    .collect(Collectors.groupingBy(
                            pv -> pv.getExternalInfluence() != null ? pv.getExternalInfluence() :
                                    new ExternalInfluence() {{
                                        setName("Без воздействия");
                                    }}
                    ));

            if (byInfluence.isEmpty()) {
                propsTable.addCell(createCell(getPropertyDisplay(property), normalFont, false, TextAlignment.LEFT));
                propsTable.addCell(createCell("-", normalFont, false, TextAlignment.CENTER));
                propsTable.addCell(createCell("-", normalFont, false, TextAlignment.CENTER));
            } else {
                boolean firstRow = true;
                for (Map.Entry<ExternalInfluence, List<PropertyValue>> entry : byInfluence.entrySet()) {
                    ExternalInfluence influence = entry.getKey();
                    List<PropertyValue> influenceValues = entry.getValue();

                    // Формируем строку значений по уровням
                    String valuesStr = influenceValues.stream()
                            .sorted(Comparator.comparing(pv ->
                                    pv.getExternalInfluenceLevel() != null ?
                                            pv.getExternalInfluenceLevel().getName() : ""))
                            .map(pv -> {
                                String levelName = pv.getExternalInfluenceLevel() != null ?
                                        pv.getExternalInfluenceLevel().getName() : "Без уровня";
                                String val = pv.getValue() != null ? pv.getValue() : "-";
                                return levelName + ": " + val;
                            })
                            .collect(Collectors.joining("\n"));

                    if (firstRow) {
                        // Первая строка — с названием свойства
                        propsTable.addCell(createCell(getPropertyDisplay(property), normalFont, false, TextAlignment.LEFT));
                        firstRow = false;
                    } else {
                        // Продолжение — пустая ячейка (объединение по вертикали будет ниже)
                        propsTable.addCell(createCell("", normalFont, false, TextAlignment.LEFT));
                    }

                    propsTable.addCell(createCell(
                            influence.getName() != null ? influence.getName() : "-",
                            normalFont, false, TextAlignment.LEFT));
                    propsTable.addCell(createCell(valuesStr, normalFont, false, TextAlignment.LEFT));
                }
            }
        }

        document.add(propsTable);
    }

    // Вспомогательный метод для отображения Property
    private String getPropertyDisplay(Property property) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(property.getId());

        if (property.getUnit() != null) {
            sb.append("\nЕд.изм.: ").append(property.getUnit().getName());
        }
        if (property.getQualityCriterionValue() != null) {
            sb.append("\nКритерий: ").append(property.getQualityCriterionValue());
        }

        return sb.toString();
    }

    private void addDetailRow(Table table, String label, String value, PdfFont font) {
        table.addCell(createCell(label, font, true, TextAlignment.LEFT));
        table.addCell(createCell(value != null ? value : "-", font, false, TextAlignment.LEFT));
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

    private String getUnitName(Property property) {
        return property.getUnit() != null ? property.getUnit().getName() : "-";
    }

    private PdfFont loadFont(String fontPath) throws IOException {
        try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
            if (fontStream == null) {
                throw new IOException("Шрифт не найден: " + fontPath);
            }
            byte[] fontData = fontStream.readAllBytes();
            return PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
        }
    }
}
