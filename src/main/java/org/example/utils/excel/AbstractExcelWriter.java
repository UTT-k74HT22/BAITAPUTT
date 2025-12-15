package org.example.utils.excel;

import org.apache.poi.ss.usermodel.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lớp trừu tượng hỗ trợ ghi dữ liệu vào file Excel.
 * @author Hoang Dinh Dung
 */
public abstract class AbstractExcelWriter<T> implements ExcelSheetWriter<T> {

    protected static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    protected static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected String safe(String s) {
        return s == null ? "" : s;
    }

    protected void create(Row row, int col, Object value, CellStyle style) {
        cell(row, col, value, style);
    }

    protected void cell(Row row, int col, Object value, CellStyle style) {
        Cell c = row.createCell(col);

        if (value == null) {
            c.setBlank();
        } else if (value instanceof String s) {
            c.setCellValue(s);
        } else if (value instanceof Boolean b) {
            c.setCellValue(b);
        } else if (value instanceof LocalDate d) {
            c.setCellValue(d.format(DF));
        } else if (value instanceof LocalDateTime dt) {
            c.setCellValue(dt.format(DTF));
        } else if (value instanceof java.util.Date dt) {
            c.setCellValue(dt);
        } else if (value instanceof BigDecimal bd) {
            c.setCellValue(bd.doubleValue());
        } else if (value instanceof Number n) {
            c.setCellValue(n.doubleValue());
        } else {
            c.setCellValue(String.valueOf(value));
        }
        c.setCellStyle(style);
    }
}