package org.example.service.impl;

import org.example.dto.response.CategoryResponse;
import org.example.entity.Category;
import org.example.repository.CategoryRepository;
import org.example.repository.impl.CategoryRepositoryImpl;
import org.example.service.CategoryService;

import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl() {
        this.categoryRepository = new CategoryRepositoryImpl();
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category getCategoryByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Override
    public void createCategory(Category category) {
        if (categoryRepository.existsByCode(category.getCode())) {
            throw new RuntimeException("Category code already exists: " + category.getCode());
        }
        categoryRepository.insert(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryRepository.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponse> searchCategories(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll();
        }
        return categoryRepository.search(keyword.trim());
    }


    @Override
    public boolean existsByCode(String code) {
        return categoryRepository.existsByCode(code);
    }
}
