package org.example.repository.impl;

import org.example.dto.request.Product.CreateProductRequest;
import org.example.dto.request.Product.UpdateProductRequest;
import org.example.dto.response.Product.ProductDetailResponse;
import org.example.dto.response.Product.ProductResponse;
import org.example.entity.enums.ProductStatus;
import org.example.repository.BaseRepository;
import org.example.repository.ProductRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của ProductRepository.
 */
public class ProductRepositoryImpl extends BaseRepository implements ProductRepository {

    /**
     * Thêm mới sản phẩm.
     */
    @Override
    public long insert(CreateProductRequest request) {
        String sql = """
        INSERT INTO products
        (category_id, manufacturer_id, code, barcode, name,
         dosage_form, unit, description,
         standard_price, requires_prescription, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        return executeInsertAndReturnId(sql, ps -> {
            ps.setLong(1, request.getCategoryId());

            if (request.getManufacturerId() != null) {
                ps.setLong(2, request.getManufacturerId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, request.getCode());
            ps.setString(4, request.getBarcode());
            ps.setString(5, request.getName());
            ps.setString(6, request.getDosageForm());
            ps.setString(7, request.getUnit());
            ps.setString(8, request.getDescription());
            ps.setBigDecimal(9, request.getStandardPrice());
            ps.setBoolean(10, request.isRequiresPrescription());
            ps.setString(11, request.getStatus().name());
        });
    }

    /**
     * Cập nhật sản phẩm theo code.
     */
    @Override
    public void updateByCode(String code, UpdateProductRequest request) {
        String sql = """
            UPDATE products
            SET category_id = ?,
                manufacturer_id = ?,
                barcode = ?,
                name = ?,
                dosage_form = ?,
                unit = ?,
                description = ?,
                standard_price = ?,
                requires_prescription = ?,
                status = ?
            WHERE code = ?
        """;

        executeUpdate(sql, ps -> {
            ps.setLong(1, request.getCategoryId());

            if (request.getManufacturerId() != null) {
                ps.setLong(2, request.getManufacturerId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, request.getBarcode());
            ps.setString(4, request.getName());
            ps.setString(5, request.getDosageForm());
            ps.setString(6, request.getUnit());
            ps.setString(7, request.getDescription());
            ps.setBigDecimal(8, request.getStandardPrice());
            ps.setBoolean(9, request.isRequiresPrescription());
            ps.setString(10, request.getStatus().name());
            ps.setString(11, code);
        });
    }

    /**
     * Xóa mềm sản phẩm (chuyển status sang DISCONTINUED).
     */
    @Override
    public void softDeleteByCode(String code) {
        String sql = """
            UPDATE products
            SET status = ?
            WHERE code = ?
        """;

        executeUpdateOrThrow(
                sql,
                params(ProductStatus.DISCONTINUED.name(), code),
                "Product not found with code: " + code
        );
    }
    /**
     * Lấy danh sách tất cả sản phẩm.
     */
    @Override
    public List<ProductResponse> getAll() {
        String sql = """
            SELECT
                p.id,
                p.code,
                p.name,
                c.name AS categoryName,
                m.name AS manufacturerName,
                p.standard_price,
                p.requires_prescription,
                p.status
            FROM products p
            JOIN categories c ON p.category_id = c.id
            LEFT JOIN manufacturers m ON p.manufacturer_id = m.id
            ORDER BY p.created_at DESC
        """;

        return queryForList(sql, this::mapToRow);
    }
    /**
     * Lấy sản phẩm theo code.
     * */
    @Override
    public Optional<ProductResponse> findByCode(String code) {
        String sql = """
        SELECT
            p.id,
            p.code,
            p.name,
            c.name AS categoryName,
            m.name AS manufacturerName,
            p.standard_price,
            p.requires_prescription,
            p.status
        FROM products p
        JOIN categories c ON p.category_id = c.id
        LEFT JOIN manufacturers m ON p.manufacturer_id = m.id
        WHERE p.code = ?
    """;

        return queryForObject(sql, params(code), this::mapToRow);
    }

    /**
     * Lấy sản phẩm theo id.
     */
    @Override
    public Optional<ProductResponse> findById(long id) {
        String sql = """
        SELECT
            p.id,
            p.code,
            p.name,
            c.name AS categoryName,
            m.name AS manufacturerName,
            p.standard_price,
            p.requires_prescription,
            p.status
        FROM products p
        JOIN categories c ON p.category_id = c.id
        LEFT JOIN manufacturers m ON p.manufacturer_id = m.id
        WHERE p.id = ?
    """;

        return queryForObject(sql, params(id), this::mapToRow);
    }

    @Override
    public Optional<ProductDetailResponse> findDetailByCode(String code) {
        String sql = """
        SELECT
            p.id,
            p.code,
            p.barcode,
            p.name,
            p.dosage_form,
            p.unit,
            p.description,
            p.image_url,
            p.standard_price,
            p.requires_prescription,
            p.status,

            c.id   AS categoryId,
            c.name AS categoryName,

            m.id   AS manufacturerId,
            m.code AS manufacturerCode,
            m.name AS manufacturerName,
            m.country AS manufacturerCountry
        FROM products p
        JOIN categories c ON p.category_id = c.id
        LEFT JOIN manufacturers m ON p.manufacturer_id = m.id
        WHERE p.code = ?
    """;

        return queryForObject(sql, params(code), rs -> {
            ProductDetailResponse d = new ProductDetailResponse();
            d.setId(rs.getLong("id"));
            d.setCode(rs.getString("code"));
            d.setBarcode(rs.getString("barcode"));
            d.setName(rs.getString("name"));

            d.setDosageForm(rs.getString("dosage_form"));
            d.setUnit(rs.getString("unit"));
            d.setDescription(rs.getString("description"));
            d.setImageUrl(rs.getString("image_url"));

            d.setStandardPrice(rs.getBigDecimal("standard_price"));
            d.setRequiresPrescription(rs.getBoolean("requires_prescription"));
            d.setStatus(ProductStatus.valueOf(rs.getString("status")));

            d.setCategoryId(rs.getLong("categoryId"));
            d.setCategoryName(rs.getString("categoryName"));

            d.setManufacturerId(rs.getObject("manufacturerId", Long.class));
            d.setManufacturerCode(rs.getString("manufacturerCode"));
            d.setManufacturerName(rs.getString("manufacturerName"));
            d.setManufacturerCountry(rs.getString("manufacturerCountry"));
            return d;
        });
    }

    /**
     * Kiểm tra mã sản phẩm đã tồn tại chưa.
     */
    @Override
    public boolean existsByCode(String code) {
        String sql = """
        SELECT 1
        FROM products
        WHERE code = ?
        LIMIT 1
    """;

        return exists(sql, params(code));
    }

    /**
     * Map ResultSet sang ProductResponse.
     */
    private ProductResponse mapToRow(ResultSet rs) throws SQLException {
        ProductResponse p = new ProductResponse();
        p.setId(rs.getLong("id"));
        p.setCode(rs.getString("code"));
        p.setName(rs.getString("name"));
        p.setCategoryName(rs.getString("categoryName"));
        p.setManufacturerName(rs.getString("manufacturerName"));
        p.setStandardPrice(rs.getBigDecimal("standard_price"));
        p.setRequiresPrescription(rs.getBoolean("requires_prescription"));
        p.setStatus(ProductStatus.valueOf(rs.getString("status")));
        return p;
    }
}
