package org.example.repository.impl;

import org.example.config.DatabaseConfig;
import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;
import org.example.repository.CustomerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public List<CustomerResponse> findAllWithAccount() {
        String sql = """
        SELECT 
            c.id            AS customer_id,
            c.code          AS customer_code,
            c.name          AS customer_name,
            c.phone,
            c.email,
            c.status,
            a.id            AS account_id,
            a.username
        FROM customers c
        LEFT JOIN accounts a ON c.account_id = a.id
    """;

        List<CustomerResponse> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CustomerResponse dto = new CustomerResponse();
                dto.setCustomerId(rs.getInt("customer_id"));
                dto.setCustomerCode(rs.getString("customer_code"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setPhone(rs.getString("phone"));
                dto.setEmail(rs.getString("email"));

                dto.setAccountId(rs.getInt("account_id"));
                dto.setUsername(rs.getString("username"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
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
            ps.setLong(9,c.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(int id, String status) {
        String sql = "UPDATE customers SET status=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer findById(int id) {
        return null; // optional
    }
}
