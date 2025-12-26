package org.example.repository;

import org.example.dto.response.CategoryResponse;
import org.example.entity.Category;
import org.example.entity.Manufacturer;

import java.util.List;

public interface CategoryRepository {

    /**
     * Lấy tất cả category.
     * @return danh sách category
     */
    List<CategoryResponse> findAll();

    /**
     * Tìm category theo ID.
     * @param id ID category
     * @return category hoặc null nếu không tồn tại
     */
    Category findById(Long id);

    /**
     * Tìm category theo mã.
     * @param code mã category
     * @return category hoặc null
     */
    Category findByCode(String code);

    /**
     * Insert category trong transaction và trả về ID.
     * @param category category cần insert
     * @return ID đã tạo
     */
    long insert(Category category);

    /**
     * Cập nhật category.
     * @param category category cần cập nhật
     */
    void update(Category category);

    /**
     * Xóa category theo ID.
     * @param id ID category
     */
    void deleteById(Long id);

    /**
     * Tìm kiếm category theo từ khóa (code / name).
     * @param keyword từ khóa
     * @return danh sách category
     */
    List<CategoryResponse> search(String keyword);

    /**
     * Kiểm tra tồn tại theo mã.
     * @param code mã category
     * @return true nếu tồn tại
     */
    boolean existsByCode(String code);
    /**
     * Lấy tất cả category có status = ACTIVE
     * @return danh sách category
     */
    List<CategoryResponse> findAllActive();
}
