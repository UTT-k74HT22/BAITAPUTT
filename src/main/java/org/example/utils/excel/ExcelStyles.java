package org.example.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import java.util.EnumMap;
import java.util.Map;

/**
 * Quản lý các kiểu định dạng Excel
 * @author Hoang Dinh Dung
 */
public class ExcelStyles {

    private final Workbook workbook;
    private final Map<StyleKey, CellStyle> cache = new EnumMap<>(StyleKey.class);

    public ExcelStyles(Workbook workbook) {
        this.workbook = workbook;
    }

    public CellStyle get(StyleKey key) {
        return cache.computeIfAbsent(key, this::build);
    }

    /**
     * Dựng kiểu theo key
     * @param key kiểu cần dựng
     * @return CellStyle tương ứng
     */
    private CellStyle build(StyleKey key) {
        return switch (key) {

            case TITLE -> buildTitle();
            case HEADER -> buildHeader();
            case DATA -> buildData(false);
            case DATA_CENTER -> buildData(true);
            case BADGE_ACTIVE -> buildBadge(true);
            case BADGE_INACTIVE -> buildBadge(false);

        };
    }

    /**
     * Dựng kiểu tiêu đề
     * @return CellStyle tiêu đề
     */
    private CellStyle buildTitle() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        setThinBorder(style);
        return style;
    }

    /**
     * Dựng kiểu header
     * @return CellStyle header
     */
    private CellStyle buildHeader() {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        setThinBorder(style);
        return style;
    }

    /**
     * Dựng kiểu dữ liệu
     * @param center căn giữa hay không
     * @return CellStyle dữ liệu
     */
    private CellStyle buildData(boolean center) {
        CellStyle style = workbook.createCellStyle();
        setThinBorder(style);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(center ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        style.setWrapText(true);
        return style;
    }

    /**
     * Dựng kiểu badge
     * @param active trạng thái active hay không
     * @return CellStyle badge
     */
    private CellStyle buildBadge(boolean active) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(get(StyleKey.DATA_CENTER));

        if (active) {
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            Font f = workbook.createFont();
            f.setBold(true);
            f.setColor(IndexedColors.DARK_GREEN.getIndex());
            style.setFont(f);
        } else {
            style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            Font f = workbook.createFont();
            f.setBold(true);
            f.setColor(IndexedColors.DARK_RED.getIndex());
            style.setFont(f);
        }

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Đặt viền mỏng cho ô
     * @param style CellStyle cần đặt viền
     */
    private void setThinBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
