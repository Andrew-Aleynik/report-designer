package com.andrewaleynik.reportdesigner.reportdesigner.services.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;
import java.io.InputStream;

public final class PdfFontLoader {

    private PdfFontLoader() {
    }

    public static PdfFont loadFromClasspath(Class<?> context, String fontPath) throws IOException {
        try (InputStream fontStream = context.getResourceAsStream(fontPath)) {
            if (fontStream == null) {
                throw new IOException("Шрифт не найден: " + fontPath);
            }
            byte[] fontData = fontStream.readAllBytes();
            return PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
        }
    }
}
