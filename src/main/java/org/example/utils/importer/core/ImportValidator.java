package org.example.utils.importer.core;

import java.util.List;

/**
 * Interface để validate dữ liệu import
 * @param <T> Kiểu DTO cần validate
 */
public interface ImportValidator<T> {
    /**
     * Validate dữ liệu import
     * @param data đối tượng cần validate
     * @param rowIndex chỉ số dòng (1-based)
     * @return danh sách lỗi, rỗng nếu hợp lệ
     */
    List<ImportError> validate(T data, int rowIndex);
}

