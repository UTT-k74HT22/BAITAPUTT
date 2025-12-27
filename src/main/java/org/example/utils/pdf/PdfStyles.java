package org.example.utils.pdf;

import com.lowagie.text.Font;
import java.awt.Color;

public class PdfStyles {

    /**
     * Font styles
     * */
    public static final Font TITLE_FONT =
            new Font(Font.HELVETICA, 18, Font.BOLD);

    public static final Font HEADER_FONT =
            new Font(Font.HELVETICA, 12, Font.BOLD);

    public static final Font BODY_FONT =
            new Font(Font.HELVETICA, 11, Font.NORMAL);

    /**
     * Colors
     * */
    public static final Color HEADER_BG = Color.LIGHT_GRAY;
    public static final Color BORDER_COLOR = Color.GRAY;

    private PdfStyles() {
        // prevent new
    }
}
