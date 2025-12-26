package org.example.view.layout;

import java.awt.*;

public final class UiTheme {

    private UiTheme() {}

    // ===== Fonts =====
    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // ===== Base colors =====
    public static final Color PRIMARY = new Color(52, 152, 219);
    public static final Color TEXT    = new Color(45, 45, 45);
    public static final Color BG       = new Color(245, 246, 248);

    // ===== Semantic colors (ERP chuẩn) =====
    public static final Color SUCCESS = new Color(39, 174, 96);   // xanh lá
    public static final Color WARNING = new Color(241, 196, 15);  // vàng
    public static final Color DANGER  = new Color(231, 76, 60);   // đỏ
    public static final Color INFO    = new Color(52, 152, 219);  // xanh dương
    public static final Color ORANGE  = new Color(230, 126, 34);  // cam
    public static final Color PURPLE  = new Color(155, 89, 182);  // tím
    public static final Color DARK    = new Color(52, 73, 94);    // xám đậm
}
