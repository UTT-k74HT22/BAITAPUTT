package org.example.controller;

import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.CategoryResponse;
import org.example.dto.response.ManufacturerResponse;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.ProductService;
import org.example.utils.pdf.ProductDetailPdfExporter;

import java.util.List;

/**
 * Controller xử lý nghiệp vụ sản phẩm.
 */
public class ProductController {

    private final ProductService productService;
    private final ProductRepositoryImpl repo = new ProductRepositoryImpl();


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
     * Lấy sản phẩm chi tiết theo mã.
     */
    public ProductDetailResponse getDetail(String code) {
        return productService.getDetailByCode(code);
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

    /**
     * Lấy danh sách tất cả danh mục.
     */
    public List<CategoryResponse> getAllCategories() {
        return productService.getAllCategories();
    }

    /**
     * Lấy danh sách tất cả nhà sản xuất.
     */
    public List<ManufacturerResponse> getAllManufacturers() {
        return productService.getAllManufacturers();
    }
    /**
     * Lấy danh sách sản phẩm theo category (phục vụ tạo đơn hàng)
     */
    public List<ProductResponse> getByCategory(Long categoryId) {
        return repo.findByCategory(categoryId);
    }
    /**
     * Thực hiện xuất PDF
     * */
    public void exportProductDetailPdf(String code, String path){
        System.out.println("[Controller] Export PDF product detail "+ code);
        ProductDetailResponse detail = productService.getDetailByCode(code);
        ProductDetailPdfExporter.export(detail,path);
   }
}
