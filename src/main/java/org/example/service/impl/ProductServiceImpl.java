package org.example.service.impl;

import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.entity.enums.ProductStatus;
import org.example.repository.ProductRepository;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.ProductService;

import java.util.List;

/**
 * Service xử lý nghiệp vụ sản phẩm.
 */
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository = new ProductRepositoryImpl();

    /**
     * Tạo mới sản phẩm.
     */
    @Override
    public void create(CreateProductRequest request) {

        if (productRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Product code already exists: " + request.getCode());
        }

        productRepository.insert(request);
    }

    /**
     * Cập nhật sản phẩm theo code.
     */
    @Override
    public void update(String code, UpdateProductRequest request) {

        ProductResponse existing = productRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with code: " + code
                ));

        if (existing.getStatus() == ProductStatus.DISCONTINUED) {
            throw new IllegalStateException("Cannot update discontinued product");
        }

        productRepository.updateByCode(code, request);
    }

    /**
     * Xóa mềm sản phẩm.
     */
    @Override
    public void delete(String code) {
        productRepository.softDeleteByCode(code);
    }

    /**
     * Lấy danh sách tất cả sản phẩm.
     */
    @Override
    public List<ProductResponse> getAll() {
        return productRepository.getAll();
    }

    /**
     * Lấy sản phẩm theo code.
     */
    @Override
    public ProductResponse getByCode(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with code: " + code
                ));
    }
    public ProductDetailResponse getDetailByCode(String code){
        return productRepository.findDetailByCode(code)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with code: " + code
                ));
    }
//    @Override
//    public List<CategoryResponse> getAllCategories() {
//        return categoryRepository.findAllActive();
//    }
//
//    @Override
//    public List<ManufacturerResponse> getAllManufacturers() {
//        return manufacturerRepository.findAllActive();
//    }

}
