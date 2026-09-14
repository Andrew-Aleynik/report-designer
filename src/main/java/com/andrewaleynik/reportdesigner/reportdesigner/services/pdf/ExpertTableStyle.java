package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

/**
 * Visual style matching the "Эксперт" report screenshots:
 * horizontal row separators only, no vertical grid.
 */
public final class ExpertTableStyle {

    public static final DeviceRgb HEADER_LINE = new DeviceRgb(0, 0, 0);
    public static final DeviceRgb ROW_LINE = new DeviceRgb(180, 180, 180);

    private ExpertTableStyle() {
    }

    public static Cell headerCell(String text, PdfFont font, TextAlignment alignment) {
        return baseCell(text, font, 9, alignment)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(HEADER_LINE, 1.2f))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER);
    }

    public static Cell bodyCell(String text, PdfFont font, TextAlignment alignment) {
        return baseCell(text, font, 8, alignment)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ROW_LINE, 0.5f))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER);
    }

    public static Paragraph title(String text, PdfFont font) {
        return new Paragraph(text)
                .setFont(font)
                .setFontSize(14)
                .setMarginBottom(4);
    }

    public static Paragraph subtitleLine() {
        return new Paragraph("")
                .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1.5f))
                .setMarginBottom(10)
                .setMarginTop(0);
    }

    private static Cell baseCell(String text, PdfFont font, float fontSize,
                                 TextAlignment alignment) {
        Paragraph paragraph = new Paragraph(text != null ? text : "")
                .setFont(font)
                .setFontSize(fontSize)
                .setMultipliedLeading(1.15f);
        return new Cell()
                .add(paragraph)
                .setPaddingTop(6)
                .setPaddingBottom(6)
                .setPaddingLeft(4)
                .setPaddingRight(4)
                .setTextAlignment(alignment)
                .setVerticalAlignment(VerticalAlignment.TOP);
    }
}
