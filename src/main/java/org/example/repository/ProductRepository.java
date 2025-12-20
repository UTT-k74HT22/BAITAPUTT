package org.example.repository;


import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    /**
    * Insert a new product into the database.
    * @param request the product to insert
    * @return the ID of the inserted product
    * */
    long insert(CreateProductRequest request);

    /**
    * Get all products from the database.
    * @return a list of all products
    * */
    List<ProductResponse> getAll();

    /**
    * Find a product by its code.
    * @param code the code of the product to find
    * @return an Optional containing the product if found, or an empty Optional if not found
    * */
    Optional<ProductResponse> findByCode(String code);
    Optional<ProductResponse> findById(long id);

    /**
    * Find a product by its code.
    * @param code the code of the product to find
    * @return an Optional containing the product if found, or an empty Optional if not found
    * */
    Optional<ProductDetailResponse> findDetailByCode(String code);
    /**
    * Check if a product with the given code exists in the database.
    * @param code the code of the product to check
    * @return true if the product exists, false otherwise
    * */
    boolean existsByCode(String code);
    /**
    * Update a product by its code.
    * @param code the code of the product to update
    * @param request the product to update
    * */
    void updateByCode(String code, UpdateProductRequest request);
    /**
    * Soft delete a product by its code.
    * @param code the code of the product to delete
    * */
    void softDeleteByCode(String code);

}
