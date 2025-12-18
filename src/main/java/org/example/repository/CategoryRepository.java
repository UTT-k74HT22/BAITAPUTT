package org.example.repository;
import org.example.dto.response.CategoryResponse;
import java.util.List;

    public interface CategoryRepository {
        List<CategoryResponse> findAll();
    }


