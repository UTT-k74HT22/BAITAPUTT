package org.example.view.base;

import java.awt.*;

public final class UiTheme {
    private UiTheme() {}

    // ===== Base colors =====
    public static final Color PRIMARY = new Color(113, 99, 248);
    public static final Color SECONDARY = new Color(108,117,125);
    public static final Color BORDER  = new Color(230, 232, 245);
    public static final Color ROW_ALT = new Color(248, 249, 255);
    public static final Color SELECT  = new Color(232, 236, 255);
    public static final Color TEXT    = new Color(45, 45, 70);
    public static final Color BG       = new Color(245, 246, 248);

    // ===== Semantic colors (ERP standard) =====
    public static final Color SUCCESS = new Color(39, 174, 96);   // green
    public static final Color WARNING = new Color(241, 196, 15);  // yellow
    public static final Color DANGER  = new Color(231, 76, 60);   // red
    public static final Color INFO    = new Color(52, 152, 219);  // blue
    public static final Color ORANGE  = new Color(230, 126, 34);  // orange
    public static final Color PURPLE  = new Color(155, 89, 182);  // purple
    public static final Color DARK    = new Color(52, 73, 94);    // dark gray

    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
}
