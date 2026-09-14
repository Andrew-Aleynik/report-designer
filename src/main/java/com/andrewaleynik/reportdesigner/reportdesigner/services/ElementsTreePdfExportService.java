package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.PdfFontLoader;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.QualityPropertiesTableBuilder;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

import static com.andrewaleynik.reportdesigner.reportdesigner.App.FontPaths.ARIAL;
import static com.andrewaleynik.reportdesigner.reportdesigner.App.FontPaths.ARIAL_BOLD_ITALIC;

public class ElementsTreePdfExportService implements ExportService<TreeSet<Element>> {

    private final QualityPropertiesTableBuilder propertiesTableBuilder;

    public ElementsTreePdfExportService(PropertyValueService propertyValueService) {
        this.propertiesTableBuilder = new QualityPropertiesTableBuilder(propertyValueService);
    }

    @Override
    public File export(TreeSet<Element> elementsTree) {
        if (elementsTree == null || elementsTree.isEmpty()) {
            throw new IllegalArgumentException("Дерево элементов не может быть пустым");
        }

        try {
            File tempFile = File.createTempFile("system_", ".pdf");
            tempFile.deleteOnExit();

            PdfWriter writer = new PdfWriter(tempFile.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont headerFont = loadFont(ARIAL_BOLD_ITALIC);
            PdfFont normalFont = loadFont(ARIAL);
            PdfFont boldFont = loadFont(ARIAL_BOLD_ITALIC);

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
        document.add(new Paragraph("Отчет по системе")
                .setFont(headerFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        document.add(new Paragraph("Создан: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(30));
    }

    private void addTreeStatistics(Document document, TreeSet<Element> elementsTree,
                                   PdfFont boldFont, PdfFont normalFont) {
        int totalElements = elementsTree.size();
        int elementsWithQuality = countElementsWithQuality(elementsTree);

        Table statsTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}));
        statsTable.setWidth(UnitValue.createPercentValue(90));

        addStatRow(statsTable, "Общее количество элементов:", String.valueOf(totalElements), boldFont, normalFont);
        addStatRow(statsTable, "Элементов с качеством:", String.valueOf(elementsWithQuality), boldFont, normalFont);
        addStatRow(statsTable, "Элементов без качества:",
                String.valueOf(totalElements - elementsWithQuality), boldFont, normalFont);

        document.add(statsTable);
    }

    private int countElementsWithQuality(TreeSet<Element> elements) {
        return (int) elements.stream()
                .filter(element -> element.getQuality() != null)
                .count();
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
                                         int level) {
        int indent = level * 15;

        document.add(new Paragraph(element.getName())
                .setFont(level == 0 ? headerFont : boldFont)
                .setFontSize(14)
                .setMarginLeft(indent)
                .setMarginTop(level == 0 ? 10 : 5)
                .setMarginBottom(3));

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
                                   PdfFont normalFont, PdfFont boldFont, int indent) {
        document.add(new Paragraph("Качество: " + quality.getCode())
                .setFont(boldFont)
                .setFontSize(10)
                .setMarginLeft(indent)
                .setMarginBottom(3));

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
            document.add(propertiesTableBuilder.createSectionHeader(boldFont, indent));
            document.add(propertiesTableBuilder.build(quality, normalFont, boldFont, indent));
        }
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

    private PdfFont loadFont(String fontPath) throws IOException {
        return PdfFontLoader.loadFromClasspath(getClass(), fontPath);
    }
}
