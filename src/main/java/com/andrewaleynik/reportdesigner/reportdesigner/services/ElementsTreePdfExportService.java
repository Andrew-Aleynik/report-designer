package com.andrewaleynik.reportdesigner.reportdesigner.services;

import com.andrewaleynik.reportdesigner.reportdesigner.models.Element;
import com.andrewaleynik.reportdesigner.reportdesigner.models.ElementQuality;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.ExpertPdfFooterHandler;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.ExpertReportTableBuilder;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.ExpertTableStyle;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.PdfFontLoader;
import com.andrewaleynik.reportdesigner.reportdesigner.services.pdf.QualityCostsFormatter;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.AreaBreakType;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import static com.andrewaleynik.reportdesigner.reportdesigner.App.FontPaths.BOLD;
import static com.andrewaleynik.reportdesigner.reportdesigner.App.FontPaths.REGULAR;

/**
 * PDF export in the style of the "Эксперт" sample report
 * ({@code Na_nizheprivedennom_ris.docx}): horizontal-only tables,
 * influence indicators page and technical requirements page.
 */
public class ElementsTreePdfExportService implements ExportService<TreeSet<Element>> {

    private static final String SYSTEM_LABEL = "Экспертная система";

    private final ExpertReportTableBuilder reportTableBuilder;

    public ElementsTreePdfExportService(PropertyValueService propertyValueService) {
        this.reportTableBuilder = new ExpertReportTableBuilder(propertyValueService);
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
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 36, 50, 36);

            PdfFont normalFont = loadFont(REGULAR);
            PdfFont boldFont = loadFont(BOLD);

            pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
                    new ExpertPdfFooterHandler(normalFont, SYSTEM_LABEL));

            Element root = elementsTree.first();
            String objectName = root.getName() != null ? root.getName() : root.getCode();
            String partTypeName = resolvePartTypeName(root);

            addRealInfluencesPage(document, elementsTree, normalFont, boldFont);
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            addRequirementsPage(document, elementsTree, partTypeName, objectName, normalFont, boldFont);

            document.close();
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF файла: " + e.getMessage(), e);
        }
    }

    private void addRealInfluencesPage(Document document,
                                       TreeSet<Element> elementsTree,
                                       PdfFont normalFont,
                                       PdfFont boldFont) {
        document.add(ExpertTableStyle.title("Показатели реальных воздействий", boldFont));
        document.add(ExpertTableStyle.subtitleLine());
        document.add(reportTableBuilder.buildRealInfluencesTable(elementsTree, normalFont, boldFont));
    }

    private void addRequirementsPage(Document document,
                                     TreeSet<Element> elementsTree,
                                     String partTypeName,
                                     String objectName,
                                     PdfFont normalFont,
                                     PdfFont boldFont) {
        document.add(ExpertTableStyle.title(
                "Технические требования к типу детали «" + partTypeName + "» (" + objectName + ")",
                boldFont));
        document.add(ExpertTableStyle.subtitleLine());
        addQualityCosts(document, elementsTree.first().getQuality(), normalFont);
        document.add(reportTableBuilder.buildRequirementsTable(elementsTree, normalFont, boldFont));
    }

    private void addQualityCosts(Document document, ElementQuality quality, PdfFont normalFont) {
        for (String line : QualityCostsFormatter.formatLines(quality)) {
            document.add(new Paragraph(line)
                    .setFont(normalFont)
                    .setFontSize(9)
                    .setMarginBottom(2));
        }
        document.add(new Paragraph(" ")
                .setFont(normalFont)
                .setFontSize(6)
                .setMarginBottom(4));
    }

    private static String resolvePartTypeName(Element root) {
        if (root.getType() != null && root.getType().getName() != null && !root.getType().getName().isBlank()) {
            return root.getType().getName().trim();
        }
        return "не указан";
    }

    private PdfFont loadFont(String fontPath) throws IOException {
        return PdfFontLoader.loadFromClasspath(getClass(), fontPath);
    }
}
