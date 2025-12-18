package org.example.controller;
import org.example.dto.response.CategoryResponse;
import org.example.service.CategoryService;
import org.example.utils.excel.CategoryExcelWriter;
import org.example.utils.excel.ExcelExporter;

import java.awt.Component;
import java.util.List;

/**
 * Controller cho các thao tác liên quan đến category.
 */
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Lấy tất cả category.
     *
     * @return danh sách category
     */
    public List<CategoryResponse> getAllCategories() {
        System.out.println("[Controller] Lấy tất cả category");
        return categoryService.getAllCategories();
    }

    /**
     * Xuất danh sách category ra file Excel.
     *
     * @param parent thành phần cha để hiển thị hộp thoại
     */
    public void exportExcel(Component parent) {
        System.out.println("[Controller] Xuất danh sách category ra file Excel");
        ExcelExporter.exportWithDialog(
                categoryService.getAllCategories(),
                new CategoryExcelWriter(),
                parent
        );
    }
}
