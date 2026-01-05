package org.example.utils.importer.template;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Tạo file Excel template cho Product import
 */
public class ProductTemplateGenerator {

    public static void generateTemplate(String outputPath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Code", "Name", "Category Code", "Manufacturer Code",
                    "Barcode", "Dosage Form", "Unit", "Description",
                    "Standard Price", "Requires Prescription"
            };

            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4500);
            }

            // Add example rows
            Row exampleRow1 = sheet.createRow(1);
            exampleRow1.createCell(0).setCellValue("PRD001");
            exampleRow1.createCell(1).setCellValue("Paracetamol 500mg");
            exampleRow1.createCell(2).setCellValue("CAT001");
            exampleRow1.createCell(3).setCellValue("MFG001");
            exampleRow1.createCell(4).setCellValue("8934567890123");
            exampleRow1.createCell(5).setCellValue("Tablet");
            exampleRow1.createCell(6).setCellValue("Box");
            exampleRow1.createCell(7).setCellValue("Pain relief medication");
            exampleRow1.createCell(8).setCellValue(15000);
            exampleRow1.createCell(9).setCellValue("No");

            Row exampleRow2 = sheet.createRow(2);
            exampleRow2.createCell(0).setCellValue("PRD002");
            exampleRow2.createCell(1).setCellValue("Amoxicillin 250mg");
            exampleRow2.createCell(2).setCellValue("CAT002");
            exampleRow2.createCell(3).setCellValue("MFG002");
            exampleRow2.createCell(4).setCellValue("8934567890456");
            exampleRow2.createCell(5).setCellValue("Capsule");
            exampleRow2.createCell(6).setCellValue("Box");
            exampleRow2.createCell(7).setCellValue("Antibiotic medication");
            exampleRow2.createCell(8).setCellValue(45000);
            exampleRow2.createCell(9).setCellValue("Yes");

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
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
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}

