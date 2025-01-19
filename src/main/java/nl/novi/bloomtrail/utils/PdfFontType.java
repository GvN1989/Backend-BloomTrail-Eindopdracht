package nl.novi.bloomtrail.utils;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;

public enum PdfFontType {

    TIMES_ROMAN(StandardFonts.TIMES_ROMAN),
    TIMES_BOLD(StandardFonts.TIMES_BOLD),
    TIMES_ITALIC(StandardFonts.TIMES_ITALIC),
    TIMES_BOLD_ITALIC(StandardFonts.TIMES_BOLDITALIC);

    private final String fontName;
    private PdfFont font;

    PdfFontType(String fontName) {
        this.fontName = fontName;
    }

    public PdfFont getFont() {
        if (font == null)
            try {
                font = PdfFontFactory.createFont(fontName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create font: " + fontName, e);
        }
        return font;
    }

}
