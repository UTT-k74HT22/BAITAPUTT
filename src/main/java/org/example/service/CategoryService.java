package org.example.service;

import org.example.dto.response.CategoryResponse;
import org.example.entity.Category;
import java.util.List;

public interface CategoryService {

    /**
     * Lấy tất cả category dưới dạng DTO (có parentName).
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Tìm category theo ID.
     */
    Category getCategoryById(Long id);

    /**
     * Tìm category theo code.
     */
    Category getCategoryByCode(String code);

    /**
     * Tạo category mới.
     */
    void createCategory(Category category);

    /**
     * Cập nhật category.
     */
    void updateCategory(Category category);

    /**
     * Xóa category theo ID.
     */
    void deleteCategory(Long id);

    /**
     * Tìm kiếm category theo keyword (code hoặc name).
     */
    List<CategoryResponse> searchCategories(String keyword);

    /**
     * Kiểm tra tồn tại theo code.
     */
    boolean existsByCode(String code);
}

