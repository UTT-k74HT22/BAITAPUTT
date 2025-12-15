package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;
import org.example.repository.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public List<CustomerResponse> findAllWithAccount() {
        String sql = """
        SELECT
            c.id,
            c.account_id,
            c.code,
            c.name,
            c.type,
            c.phone,
            c.email,
            c.address,
            c.tax_code,
            c.status,
            a.username
        FROM customers c
        LEFT JOIN accounts a ON c.account_id = a.id
    """;

        List<CustomerResponse> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CustomerResponse r = new CustomerResponse();

                r.setId(rs.getInt("id"));
                r.setAccountId(rs.getObject("account_id", Integer.class));
                r.setCode(rs.getString("code"));
                r.setName(rs.getString("name"));
                r.setType(rs.getString("type"));
                r.setPhone(rs.getString("phone"));
                r.setEmail(rs.getString("email"));
                r.setAddress(rs.getString("address"));
                r.setTaxCode(rs.getString("tax_code"));
                r.setStatus(rs.getString("status"));
                r.setUsername(rs.getString("username"));

                list.add(r);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    @Override
    public void save(Customer c) {
        String sql = """
            INSERT INTO customers
            (code, name, type, phone, email, address, tax_code, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getCode());
            ps.setString(2, c.getName());
            ps.setString(3, c.getType());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getAddress());
            ps.setString(7, c.getTaxCode());
            ps.setString(8, c.getStatus().name());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Customer c) {
        String sql = """
            UPDATE customers
            SET code=?, name=?, type=?, phone=?, email=?, address=?, tax_code=?, status=?
            WHERE id=?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getCode());
            ps.setString(2, c.getName());
            ps.setString(3, c.getType());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getAddress());
            ps.setString(7, c.getTaxCode());
            ps.setString(8, c.getStatus().name());
            ps.setLong(9, c.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(int id, String status) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE customers SET status=? WHERE id=?")) {

            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer findById(int id) {
        return null;
    }
}
