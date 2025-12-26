package org.example.dto.response;

import org.example.utils.importer.core.ImportError;
import java.util.ArrayList;
import java.util.List;

/**
 * Chứa thông tin của 1 dòng dữ liệu import
 */
public class ImportRowData<T> {
    private int rowIndex;
    private T data;
    private List<ImportError> errors;
    private boolean valid;

    public ImportRowData() {
        this.errors = new ArrayList<>();
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ImportError> getErrors() {
        return errors;
    }

    public void setErrors(List<ImportError> errors) {
        this.errors = errors;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Lấy tóm tắt lỗi dưới dạng string
     */
    public String getErrorSummary() {
        if (errors.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            sb.append(errors.get(i).toString());
            if (i < errors.size() - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }
}

