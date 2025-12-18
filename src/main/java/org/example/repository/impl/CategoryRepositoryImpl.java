package org.example.repository.impl;
import org.example.dto.response.CategoryResponse;
import org.example.repository.BaseRepository;
import org.example.repository.CategoryRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryRepositoryImpl extends BaseRepository
        implements CategoryRepository {

    @Override
    public List<CategoryResponse> findAll() {

        String sql = """
            SELECT
                c.id,
                c.code,
                c.name,
                c.description,
                c.parent_id,
                c.is_active,
                c.display_order,
                p.name AS parent_name
            FROM categories c
            LEFT JOIN categories p ON c.parent_id = p.id
            ORDER BY c.display_order, c.name
        """;

        return queryForList(sql, this::mapRowToDto);
    }

    private CategoryResponse mapRowToDto(ResultSet rs) throws SQLException {
        CategoryResponse r = new CategoryResponse();
        r.setId(rs.getLong("id"));
        r.setCode(rs.getString("code"));
        r.setName(rs.getString("name"));
        r.setDescription(rs.getString("description"));
        r.setParentId(rs.getObject("parent_id", Long.class));
        r.setActive(rs.getBoolean("is_active"));
        r.setDisplayOrder(rs.getInt("display_order"));
        r.setParentName(rs.getString("parent_name"));
        return r;
    }
}
