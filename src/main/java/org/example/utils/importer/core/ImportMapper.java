package org.example.utils.importer.core;

import org.apache.poi.ss.usermodel.Row;

/**
 * Interface để map dữ liệu từ Excel row sang DTO
 * @param <T> Kiểu DTO
 */
public interface ImportMapper<T> {
    /**
     * Map một dòng Excel thành đối tượng DTO
     * @param row dòng Excel
     * @param rowIndex chỉ số dòng (1-based, không tính header)
     * @return đối tượng DTO
     * @throws Exception nếu có lỗi khi mapping
     */
    T mapRow(Row row, int rowIndex) throws Exception;

    /**
     * Lấy danh sách các cột bắt buộc trong file Excel
     * @return mảng tên cột theo thứ tự
     */
    String[] getRequiredHeaders();
}

