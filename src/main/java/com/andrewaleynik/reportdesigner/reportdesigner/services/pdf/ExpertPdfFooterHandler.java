package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Footer in the style of "Эксперт": timestamp on the left, system name on the right.
 */
public class ExpertPdfFooterHandler implements IEventHandler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final PdfFont font;
    private final String systemLabel;
    private final String timestamp;

    public ExpertPdfFooterHandler(PdfFont font, String systemLabel) {
        this.font = font;
        this.systemLabel = systemLabel;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = documentEvent.getDocument();
        PdfPage page = documentEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        float bottom = 28;
        float left = 36;
        float right = pageSize.getWidth() - 36;

        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
        try (Canvas canvas = new Canvas(pdfCanvas, pageSize)) {
            canvas.showTextAligned(
                    new Paragraph(timestamp).setFont(font).setFontSize(8),
                    left, bottom, TextAlignment.LEFT);
            canvas.showTextAligned(
                    new Paragraph(systemLabel).setFont(font).setFontSize(8).setItalic(),
                    right, bottom, TextAlignment.RIGHT);
        }
    }
}
