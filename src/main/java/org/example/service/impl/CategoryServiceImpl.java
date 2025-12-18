
package org.example.service.impl;
import org.example.dto.response.CategoryResponse;
import org.example.repository.CategoryRepository;
import org.example.repository.impl.CategoryRepositoryImpl;
import org.example.service.CategoryService;

import java.util.List;
import java.util.logging.Logger;

public class CategoryServiceImpl implements CategoryService {

    private static final Logger log =
            Logger.getLogger(CategoryServiceImpl.class.getName());

    private final CategoryRepository categoryRepository =
            new CategoryRepositoryImpl();

    @Override
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching category list");return categoryRepository.findAll();
    }
}
