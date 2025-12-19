package org.example.controller;

import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.ProductResponse;
import org.example.service.ProductService;

import java.util.List;

/**
 * Controller xử lý nghiệp vụ sản phẩm.
 */
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lấy danh sách tất cả sản phẩm.
     */
    public List<ProductResponse> getAll() {
        return productService.getAll();
    }

    /**
     * Lấy sản phẩm theo mã.
     */
    public ProductResponse getByCode(String code) {
        return productService.getByCode(code);
    }

    /**
     * Thêm mới sản phẩm.
     */
    public void create(CreateProductRequest request) {
        productService.create(request);
    }

    /**
     * Cập nhật sản phẩm theo mã.
     */
    public void update(String code, UpdateProductRequest request) {
        productService.update(code, request);
    }

    /**
     * Xóa mềm sản phẩm.
     */
    public void delete(String code) {
        productService.delete(code);
    }
}
