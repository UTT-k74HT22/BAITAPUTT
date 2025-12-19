package org.example.service;

import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    void create(CreateProductRequest request);

    void update(String code, UpdateProductRequest request);

    void delete(String code);

    List<ProductResponse> getAll();

    ProductResponse getByCode(String code);
}
