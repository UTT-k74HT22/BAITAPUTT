package org.example.utils.importer.core;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dto.response.importer.ImportPreviewResponse;
import org.example.dto.response.importer.ImportRowData;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class cung cấp logic chung cho việc import
 */
public abstract class AbstractImportService<T> {

    /**
     * Lấy mapper để chuyển đổi dòng Excel thành DTO
     */
    protected abstract ImportMapper<T> getMapper();

    /**
     * Lấy validator để validate dữ liệu
     */
    protected abstract ImportValidator<T> getValidator();

    /**
     * Lưu dữ liệu hợp lệ vào database
     */
    protected abstract void saveData(List<T> validData) throws Exception;

    /**
     * Preview dữ liệu từ file Excel
     * Đọc file, map và validate từng dòng
     */
    public ImportPreviewResponse<T> previewFromFile(File file) throws Exception {
        List<ImportRowData<T>> validRows = new ArrayList<>();
        List<ImportRowData<T>> invalidRows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            ImportMapper<T> mapper = getMapper();
            ImportValidator<T> validator = getValidator();

            // Validate headers
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("File rỗng hoặc không có header row.");
            }

            String[] requiredHeaders = mapper.getRequiredHeaders();
            if (!validateHeaders(headerRow, requiredHeaders)) {
                StringBuilder error = new StringBuilder("File không đúng định dạng. Header không khớp với template.");
                error.append("Expected headers: ");
                for (int i = 0; i < requiredHeaders.length; i++) {
                    if (i > 0) error.append(", ");
                    error.append("\"").append(requiredHeaders[i]).append("\"");
                }
                error.append("\nActual headers: ");
                int actualColCount = Math.min(headerRow.getLastCellNum(), requiredHeaders.length + 5);
                for (int i = 0; i < actualColCount; i++) {
                    if (i > 0) error.append(", ");
                    error.append("\"").append(getCellValue(headerRow, i)).append("\"");
                }
                throw new IllegalArgumentException(error.toString());
            }

            // Process data rows (skip header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) continue;

                try {
                    // Map dữ liệu
                    T data = mapper.mapRow(row, i + 1);

                    // Validate dữ liệu
                    List<ImportError> errors = validator.validate(data, i + 1);

                    ImportRowData<T> rowData = new ImportRowData<>();
                    rowData.setRowIndex(i + 1);
                    rowData.setData(data);
                    rowData.setErrors(errors);
                    rowData.setValid(errors.isEmpty());

                    if (errors.isEmpty()) {
                        validRows.add(rowData);
                    } else {
                        invalidRows.add(rowData);
                    }
                } catch (Exception e) {
                    // Nếu có exception khi map, coi như dòng lỗi
                    ImportRowData<T> rowData = new ImportRowData<>();
                    rowData.setRowIndex(i + 1);
                    rowData.setValid(false);
                    rowData.setData(null); // Explicitly set to null

                    List<ImportError> errors = new ArrayList<>();
                    errors.add(new ImportError(i + 1, "Parse Error",
                        "Không thể đọc dữ liệu: " + e.getMessage()));
                    rowData.setErrors(errors);
                    invalidRows.add(rowData);
                }
            }
        }

        ImportPreviewResponse<T> response = new ImportPreviewResponse<>();
        response.setValidRows(validRows);
        response.setInvalidRows(invalidRows);
        response.setTotalRows(validRows.size() + invalidRows.size());
        response.setHasErrors(!invalidRows.isEmpty());

        return response;
    }

    /**
     * Apply import - lưu dữ liệu hợp lệ vào database
     */
    public int applyImport(List<T> validData) throws Exception {
        saveData(validData);
        return validData.size();
    }

    /**
     * Lấy giá trị cell dưới dạng String
     * Xử lý các kiểu dữ liệu khác nhau
     */
    protected String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                //Case kí tự trắng
                return cell.getStringCellValue().trim();
            case NUMERIC:
                //Case ngày tháng
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // Xử lý số nguyên và số thập phân
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                } else {
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                //Case boolean
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                //Case công thức
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * Validate headers của file Excel
     */
    private boolean validateHeaders(Row headerRow, String[] requiredHeaders) {
        if (headerRow == null) {
            System.err.println("Header row is null");
            return false;
        }

        // Check if we have enough columns
        int actualColumnCount = headerRow.getLastCellNum();
        if (actualColumnCount < requiredHeaders.length) {
            System.err.println("Not enough columns. Expected: " + requiredHeaders.length + ", Found: " + actualColumnCount);
            return false;
        }

        // Validate each header
        for (int i = 0; i < requiredHeaders.length; i++) {
            String actualHeader = getCellValue(headerRow, i).trim();
            String expectedHeader = requiredHeaders[i].trim();

            if (!actualHeader.equalsIgnoreCase(expectedHeader)) {
                System.err.println("Header mismatch at column " + (i + 1) + ":");
                System.err.println("  Expected: '" + expectedHeader + "'");
                System.err.println("  Found: '" + actualHeader + "'");
                return false;
            }
        }
        return true;
    }

    /**
     * Kiểm tra dòng có rỗng không
     */
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (!getCellValue(row, i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

