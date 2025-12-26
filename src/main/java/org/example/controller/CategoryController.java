package org.example.controller;

import org.example.dto.response.CategoryResponse;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.example.utils.excel.CategoryExcelWriter;
import org.example.utils.excel.ExcelExporter;
import java.awt.Component;
import java.util.List;

/**
 * Controller cho các thao tác liên quan đến category.
 * Chịu trách nhiệm giao tiếp giữa giao diện (UI) và service.
 */
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Khởi tạo CategoryController với service.
     *
     * @param categoryService service quản lý category
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Lấy tất cả category dưới dạng DTO.
     *
     * @return danh sách CategoryResponse
     */
    public List<CategoryResponse> getAllCategories() {
        System.out.println("[Controller] Lấy tất cả category");
        return categoryService.getAllCategories();
    }

    /**
     * Lấy category theo ID.
     *
     * @param id ID của category
     * @return category hoặc null nếu không tồn tại
     */
    public Category getCategoryById(Long id) {
        System.out.println("[Controller] Lấy category theo ID: " + id);
        return categoryService.getCategoryById(id);
    }

    /**
     * Lấy category theo code.
     *
     * @param code mã code của category
     * @return category hoặc null nếu không tồn tại
     */
    public Category getCategoryByCode(String code) {
        System.out.println("[Controller] Lấy category theo code: " + code);
        return categoryService.getCategoryByCode(code);
    }

    /**
     * Tìm kiếm category theo từ khóa (code hoặc name).
     *
     * @param keyword từ khóa tìm kiếm
     * @return danh sách category phù hợp
     */
    public List<CategoryResponse> searchCategories(String keyword) {
        System.out.println("[Controller] Tìm kiếm category theo keyword: " + keyword);
        return categoryService.searchCategories(keyword);
    }


    /**
     * Tạo category mới.
     *
     * @param category category cần tạo
     */
    public void createCategory(Category category) {
        System.out.println("[Controller] Tạo category mới: " + category.getName());
        categoryService.createCategory(category);
        System.out.println("[Controller] Tạo thành công: ID=" + category.getId());
    }


    /**
     * Cập nhật category.
     *
     * @param category category cần cập nhật
     */
    public void updateCategory(Category category) {
        System.out.println("[Controller] Cập nhật category: ID=" + category.getId());
        categoryService.updateCategory(category);
        System.out.println("[Controller] Cập nhật thành công");
    }


    /**
     * Xóa category theo ID.
     *
     * @param id ID của category cần xóa
     */
    /**
     * Soft delete category (set is_active = 0)
     *
     * @param code mã category
     */
    public void delete(String code) {
        System.out.println("[Controller] Soft delete category");
        categoryService.delete(code);
    }




    /**
     * Xuất danh sách category ra file Excel.
     *
     * @param parent thành phần cha để hiển thị hộp thoại chọn file
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
