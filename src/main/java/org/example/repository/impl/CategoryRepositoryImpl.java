package org.example.repository.impl;

import org.example.dto.response.CategoryResponse;
import org.example.entity.Category;
import org.example.entity.Manufacturer;
import org.example.repository.BaseRepository;
import org.example.repository.CategoryRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryRepositoryImpl extends BaseRepository implements CategoryRepository {

    // ==================== FIND ALL DTO ====================
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

    // ==================== FIND BY ID ENTITY ====================
    @Override
    public Category findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        return queryForEntity(sql, params(id), this::mapRowToEntity);
    }

    // ==================== FIND BY CODE ENTITY ====================
    @Override
    public Category findByCode(String code) {
        String sql = "SELECT * FROM categories WHERE code = ?";
        return queryForEntity(sql, params(code), this::mapRowToEntity);
    }

    // ==================== SEARCH DTO ====================
    @Override
    public List<CategoryResponse> search(String keyword) {
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
            WHERE c.code LIKE ? OR c.name LIKE ?
            ORDER BY c.display_order, c.name
        """;
        String kw = "%" + keyword + "%";
        return queryForList(sql, params(kw, kw), this::mapRowToDto);
    }

    // ==================== SAVE / INSERT ====================
    public long insert(Category category) {
        String sql = """
        INSERT INTO categories
        (code, name, description, parent_id, is_active, display_order)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        return executeInsertAndReturnId(sql, ps -> {
            ps.setString(1, category.getCode());
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());
            if (category.getParentId() != null) {
                ps.setLong(4, category.getParentId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            ps.setBoolean(5, category.isActive());
            ps.setInt(6, category.getDisplayOrder());
        });
    }

    // ==================== UPDATE ====================
    @Override
    public void update(Category category) {
        String sql = """
            UPDATE categories
            SET code = ?, name = ?, description = ?, parent_id = ?, is_active = ?, display_order = ?
            WHERE id = ?
        """;
        executeUpdate(sql, params(
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.getParentId(),
                category.isActive(),
                category.getDisplayOrder(),
                category.getId()
        ));
    }

    // ==================== DELETE ====================
    @Override
    public void deleteById(Long id) {
        String sql = """
                UPDATE categories
                SET is_active = 0
                WHERE id = ?
                """;
        executeUpdate(sql, params(id));
    }

    // ==================== CHECK EXISTS ====================
    @Override
    public boolean existsByCode(String code) {
        String sql = "SELECT 1 FROM categories WHERE code = ?";
        return exists(sql, params(code));
    }

    @Override
    public List<CategoryResponse> findAllActive() {
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
        WHERE c.is_active = 1
        ORDER BY c.display_order, c.name
        """;

        return queryForList(sql, this::mapRowToDto);
    }

    // ==================== MAPPERS ====================
    private Category mapRowToEntity(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setCode(rs.getString("code"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setParentId(rs.getObject("parent_id", Long.class));
        c.setActive(rs.getBoolean("is_active"));
        c.setDisplayOrder(rs.getInt("display_order"));
        return c;
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
