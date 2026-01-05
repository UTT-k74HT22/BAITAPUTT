package org.example.utils.importer.template;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;

/**
 * Tạo file Excel template cho Stock Movement import (Xuất/Nhập/Điều chỉnh kho)
 */
public class StockMovementTemplateGenerator {

    public byte[] generate() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Dữ liệu");

            // ===== Styles =====
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle textStyle = createTextStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // ===== Header (Row 0) =====
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);

            String[] headers = {"Loại", "Kho hàng", "Mã sản phẩm", "Mã lô", "Số lượng", "Ghi chú"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===== Column widths =====
            sheet.setColumnWidth(0, 4500);  // Loại
            sheet.setColumnWidth(1, 4500);  // Kho hàng
            sheet.setColumnWidth(2, 5200);  // Mã sản phẩm
            sheet.setColumnWidth(3, 4500);  // Mã lô
            sheet.setColumnWidth(4, 4200);  // Số lượng
            sheet.setColumnWidth(5, 9000);  // Ghi chú

            // ===== Example rows (Row 1, 2, 3) =====
            Object[][] examples = {
                    {"IMPORT", 1, 1, 1, 100, "Nhập hàng từ NCC ABC"},
                    {"EXPORT", 1, 2, 2, 50, "Xuất bán"},
                    {"ADJUST", 2, 3, 3, 95, "Kiểm kê"}
            };

            for (int i = 0; i < examples.length; i++) {
                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(18);

                Object[] data = examples[i];
                for (int j = 0; j < data.length; j++) {
                    Cell cell = row.createCell(j);

                    if (j == 4 && data[j] instanceof Number) { // Số lượng
                        cell.setCellValue(((Number) data[j]).doubleValue());
                        cell.setCellStyle(numberStyle);
                    } else if (data[j] instanceof Number) {
                        cell.setCellValue(((Number) data[j]).doubleValue());
                        cell.setCellStyle(textStyle);
                    } else {
                        cell.setCellValue(String.valueOf(data[j]));
                        cell.setCellStyle(textStyle);
                    }
                }
            }

            // ===== Freeze header =====
            // Freeze rows above data: keep title + note + header visible
            sheet.createFreezePane(0, 3);

            // ===== Data validation dropdown for "Loại" column =====
            addLoaiDropdownValidation(sheet, 3, 1000, 0, 0);

            // ===== Write to bytes =====
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private static void addLoaiDropdownValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(
                new String[]{"IMPORT", "EXPORT", "ADJUST"}
        );

        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = helper.createValidation(constraint, addressList);

        // Some Excel versions need this to avoid warning
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Giá trị không hợp lệ", "Loại chỉ được chọn: IMPORT, EXPORT, ADJUST.");

        sheet.addValidationData(validation);
    }

    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static CellStyle createNoteStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        return style;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static CellStyle createTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createTextStyle(workbook);
        style.setAlignment(HorizontalAlignment.RIGHT);

        DataFormat fmt = workbook.createDataFormat();
        style.setDataFormat(fmt.getFormat("#,##0")); // format quantity
        return style;
    }
}
