package org.example.utils.importer.demo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Chuyển file CSV sang XLSX để dễ import
 * Run: java ConvertCsvToXlsx Product_Import_Example.csv
 */
public class ConvertCsvToXlsx {

    public static void main(String[] args) {
        String inputCsv = "Product_Import_Example.csv";
        String outputXlsx = "Product_Import_Example.xlsx";

        try {
            convertCsvToXlsx(inputCsv, outputXlsx);
            System.out.println("✅ Đã chuyển đổi thành công!");
            System.out.println("📁 File Excel: " + outputXlsx);
        } catch (Exception e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void convertCsvToXlsx(String csvFile, String xlsxFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(xlsxFile)) {

            Sheet sheet = workbook.createSheet("Products");

            // Styling
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            String line;
            int rowNum = 0;

            while ((line = br.readLine()) != null) {
                Row row = sheet.createRow(rowNum);
                String[] values = line.split(",");

                for (int i = 0; i < values.length; i++) {
                    Cell cell = row.createCell(i);
                    String value = values[i].trim();

                    // Try parse as number for price column
                    if (rowNum > 0 && i == 8) { // Standard Price column
                        try {
                            cell.setCellValue(Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            cell.setCellValue(value);
                        }
                    } else {
                        cell.setCellValue(value);
                    }

                    // Apply header style
                    if (rowNum == 0) {
                        cell.setCellStyle(headerStyle);
                        sheet.setColumnWidth(i, 4500);
                    }
                }

                rowNum++;
            }

            workbook.write(fos);
        }
    }
}

