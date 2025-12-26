package org.example.repository.impl;

import org.example.dto.response.ManufacturerResponse;
import org.example.repository.ManufacturerReponsitory;
import org.example.repository.BaseRepository;
import org.example.entity.Manufacturer;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ManufacturerReponsitoryImpl extends BaseRepository implements ManufacturerReponsitory {
    /**
     *
     * @return
     */
    @Override
    public List<Manufacturer> findAll() {
        String sql = """
            SELECT id, code, name, country, address, phone, email, contact_person,
                   created_at, updated_at
            FROM manufacturers
            ORDER BY id DESC
        """;

        return queryForList(sql, this::mapRow);
    }


    /**
     *
     * @param id
     * @return
     */
    @Override
    public Manufacturer findById(Long id) {
        String sql = """
            SELECT id, code, name, country, address, phone, email, contact_person,
                   created_at, updated_at
            FROM manufacturers
            WHERE id = ?
        """;

        Optional<Manufacturer> result =
                queryForObject(sql, params(id), this::mapRow);

        return result.orElse(null);
    }

    /**
     *
     * @param
     * @param m
     * @return
     */
    @Override
    public long insert(Manufacturer m){
        String sql = """
            INSERT INTO manufacturers
            (code, name, country, address, phone, email, contact_person) VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        return executeInsertAndReturnId(sql, ps -> {
            ps.setString(1, m.getCode());
            ps.setString(2, m.getName());
            ps.setString(3, m.getCountry());
            ps.setString(4, m.getAddress());
            ps.setString(5, m.getPhone());
            ps.setString(6, m.getEmail());
            ps.setString(7, m.getContactPerson());
        });
    }
    /**
     *
     * @param m
     */
    @Override
    public void update(Manufacturer m) {
        String sql = """
            UPDATE manufacturers
            SET code = ?, name = ?, country = ?, address = ?, phone = ?, email = ?, contact_person = ?
            WHERE id = ?
        """;

        executeUpdate(sql, params(
                m.getCode(),
                m.getName(),
                m.getCountry(),
                m.getAddress(),
                m.getPhone(),
                m.getEmail(),
                m.getContactPerson(),
                m.getId()
        ));
    }

    /**
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        String sql = """
            DELETE FROM manufacturers
            WHERE id = ?
        """;
        executeUpdate(sql, params(id));
    }

    /**
     *
     * @param code
     * @return
     */
    @Override
    public boolean ExistsByCode(String code) {
        String sql = """
            SELECT 1 FROM manufacturers
            WHERE code = ?
            LIMIT 1;
        """;
        return exists(sql, params(code));
    }

    @Override
    public List<ManufacturerResponse> findAllActive() {
        String sql = """
        SELECT id, code, name, country, address, phone, email, contact_person
        FROM manufacturers
        ORDER BY name
        """;
        return queryForList(sql, this::mapRowToDto);
    }
    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    private Manufacturer mapRow(ResultSet rs) throws SQLException {
        Manufacturer m = new Manufacturer();
        m.setId(rs.getLong("id"));
        m.setCode(rs.getString("code"));
        m.setName(rs.getString("name"));
        m.setCountry(rs.getString("country"));
        m.setAddress(rs.getString("address"));
        m.setPhone(rs.getString("phone"));
        m.setEmail(rs.getString("email"));
        m.setContactPerson(rs.getString("contact_person"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            m.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            m.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return m;
    }
    private ManufacturerResponse mapRowToDto(ResultSet rs) throws SQLException {
        ManufacturerResponse dto = new ManufacturerResponse();
        dto.setId(rs.getLong("id"));
        dto.setCode(rs.getString("code"));
        dto.setName(rs.getString("name"));
        dto.setCountry(rs.getString("country"));
//        dto.setAddress(rs.getString("address"));
        dto.setPhone(rs.getString("phone"));
        dto.setEmail(rs.getString("email"));
//        dto.setContactPerson(rs.getString("contact_person"));
        return dto;
    }

}
