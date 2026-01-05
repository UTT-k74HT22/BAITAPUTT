package org.example.repository;


import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    /**
    * Thêm mới một sản phẩm vào cơ sở dữ liệu.
    * @param request đối tượng chứa thông tin sản phẩm cần thêm
    * @return ID của sản phẩm vừa được thêm
    */
    long insert(CreateProductRequest request);

    /**
    * Lấy tất cả sản phẩm từ cơ sở dữ liệu.
    * @return danh sách tất cả sản phẩm
    */
    List<ProductResponse> getAll();

    /**
    * Tìm kiếm sản phẩm theo mã.
    * @param code mã sản phẩm cần tìm
    * @return Optional chứa thông tin sản phẩm nếu tìm thấy, hoặc rỗng nếu không tìm thấy
    */
    Optional<ProductResponse> findByCode(String code);
    /**
     * Lấy thông tin sản phẩm để chỉnh sửa
     * @param id ID của sản phẩm cần lấy thông tin
     * @return Optional chứa thông tin sản phẩm nếu tìm thấy
     */
    Optional<ProductResponse> findById(long id);

    /**
    * Tìm kiếm chi tiết sản phẩm theo mã.
    * @param code mã sản phẩm cần tìm
    * @return Optional chứa chi tiết sản phẩm nếu tìm thấy, hoặc rỗng nếu không tìm thấy
    */
    Optional<ProductDetailResponse> findDetailByCode(String code);
    /**
    * Kiểm tra sự tồn tại của sản phẩm trong cơ sở dữ liệu.
    * @param code mã sản phẩm cần kiểm tra
    * @return true nếu sản phẩm tồn tại, false nếu không tồn tại
    */
    boolean existsByCode(String code);
    /**
    * Cập nhật thông tin sản phẩm theo mã.
    * @param code mã sản phẩm cần cập nhật
    * @param request đối tượng chứa thông tin cập nhật
    */
    void updateByCode(String code, UpdateProductRequest request);
    /**
    * Xóa mềm sản phẩm theo mã (đánh dấu đã xóa).
    * @param code mã sản phẩm cần xóa
    */
    void softDeleteByCode(String code);
    /**
     * Tìm kiếm các lô hàng theo ID sản phẩm.
     * @param productId ID của sản phẩm cần tìm lô hàng
     * @return danh sách các lô hàng của sản phẩm
     */
    List<BatchResponse> findBatchesByProduct(Long productId);
    /**
     * Tìm kiếm sản phẩm theo ID danh mục.
     * @param categoryId ID của danh mục cần tìm sản phẩm
     * @return danh sách sản phẩm thuộc danh mục
     */
    List<ProductResponse> findByCategory(Long categoryId);
}
