package org.example.repository.impl;

import org.example.dto.request.CreateWarehouseRequest;
import org.example.dto.response.WarehouseResponse;
import org.example.repository.BaseRepository;
import org.example.repository.WarehouseRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class WarehouseRepositoryImpl extends BaseRepository implements WarehouseRepository {

    @Override
    public void insert(CreateWarehouseRequest request) {
        String sql = """
                INSERT INTO warehouses (code, name, address, phone, manager_name, is_active)
                VALUES (?, ?, ?, ?, ?)
                """;
        executeUpdate(sql, ps -> {
            ps.setString(1, request.getCode());
            ps.setString(2, request.getName());
            ps.setString(3, request.getAddress());
            ps.setString(4, request.getPhone());
            ps.setString(5, request.getManagerName());
            ps.setBoolean(6, request.isActive());
        });
    }

    @Override
    public List<WarehouseResponse> getAll() {
        String sql = """
        SELECT id, code, name, address, phone, manager_name, is_active
        FROM warehouses
        ORDER BY id DESC
    """;
        return queryForList(sql, this::mapToResponse);
    }


    @Override
    public Optional<WarehouseResponse> getByCode(String code) {
        String sql = """
                SELECT id, code, name, address, phone, manager_name, is_active
                FROM warehouses
                WHERE code = ? AND is_active = 1
                """;
        return queryForObject(sql, params(code), this::mapToResponse);
    }

    @Override
    public void updateByCode(String code, CreateWarehouseRequest request) {
        String sql = """
            UPDATE warehouses
            SET name = ?,
                address = ?,
                phone = ?,
                manager_name = ?,
                is_active = ?
            WHERE code = ?
            """;

        executeUpdate(sql, params(
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getManagerName(),
                request.isActive(),
                code
        ));
    }


    @Override
    public void softDelete(String code) {
        String sql = "UPDATE warehouses SET is_active = 0 WHERE code = ?";
        executeUpdate(sql, params(code));
    }

    /* ================= MAP RESULT ================= */
    private WarehouseResponse mapToResponse(ResultSet rs) throws SQLException {
        WarehouseResponse w = new WarehouseResponse();
        w.setId(rs.getLong("id"));
        w.setCode(rs.getString("code"));
        w.setName(rs.getString("name"));
        w.setAddress(rs.getString("address"));
        w.setPhone(rs.getString("phone"));
        w.setManagerName(rs.getString("manager_name"));
        w.setActive(rs.getBoolean("is_active"));
        return w;
    }
}
