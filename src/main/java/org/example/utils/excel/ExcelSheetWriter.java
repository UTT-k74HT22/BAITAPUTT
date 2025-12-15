package org.example.utils.excel;

import org.apache.poi.ss.usermodel.Sheet;
import java.util.List;

/**
 * Chịu trách nhiệm ghi dữ liệu vào các sheet trong file Excel.
 * @author Hoang Dinh Dung
 */
public interface ExcelSheetWriter<T> {
    /**
     * Tên sheet trong file Excel.
     * @return tên sheet
     */
    String sheetName();

    /**
     * Tiêu đề của sheet.
     * @return tiêu đề
     */
    String title();

    /**
     * Các tiêu đề cột trong sheet.
     * @return mảng tiêu đề cột
     */
    String[] headers();

    /**
     * Ghi dữ liệu vào sheet.
     * @param sheet sheet cần ghi
     * @param styles kiểu định dạng Excel
     * @param data danh sách dữ liệu cần ghi
     */
    void create(Sheet sheet, ExcelStyles styles, List<T> data);
}
