package org.example.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Kết quả preview dữ liệu import
 */
public class ImportPreviewResponse<T> {
    private List<ImportRowData<T>> validRows;
    private List<ImportRowData<T>> invalidRows;
    private int totalRows;
    private boolean hasErrors;

    public ImportPreviewResponse() {
        this.validRows = new ArrayList<>();
        this.invalidRows = new ArrayList<>();
    }

    public List<ImportRowData<T>> getValidRows() {
        return validRows;
    }

    public void setValidRows(List<ImportRowData<T>> validRows) {
        this.validRows = validRows;
    }

    public List<ImportRowData<T>> getInvalidRows() {
        return invalidRows;
    }

    public void setInvalidRows(List<ImportRowData<T>> invalidRows) {
        this.invalidRows = invalidRows;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public int getValidCount() {
        return validRows.size();
    }

    public int getInvalidCount() {
        return invalidRows.size();
    }
}

