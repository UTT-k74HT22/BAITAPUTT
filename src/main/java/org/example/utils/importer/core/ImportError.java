package org.example.utils.importer.core;

/**
 * Đại diện cho một lỗi xảy ra khi import
 */
public class ImportError {
    private int rowIndex;
    private String columnName;
    private String message;

    public ImportError(int rowIndex, String columnName, String message) {
        this.rowIndex = rowIndex;
        this.columnName = columnName;
        this.message = message;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return columnName + ": " + message;
    }
}
