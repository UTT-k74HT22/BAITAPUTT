package org.example.repository.impl;

import org.example.repository.BaseRepository;
import org.example.repository.CustomerProfileRepository;

import java.sql.Connection;

public class CustomerProfileRepositoryImpl extends BaseRepository implements CustomerProfileRepository {
    /**
     * Chèn hồ sơ khách hàng vào cơ sở dữ liệu.
     *
     * @param conn         kết nối cơ sở dữ liệu
     * @param profileId    ID hồ sơ
     * @param customerType loại khách hàng
     * @param taxCode      mã số thuế
     */
    @Override
    public void insert(Connection conn, long profileId, String customerType, String taxCode) {
        String sql = """
                INSERT INTO customer_profiles(profile_id, customer_type, tax_code)
                VALUES (?, ?, ?)
                """;
        try {
            executeUpdate(conn, sql, ps -> {
                ps.setLong(1, profileId);
                ps.setString(2, (customerType == null || customerType.isBlank()) ? "RETAIL" : customerType);
                ps.setString(3, taxCode);
            });
        } catch (Exception e) {
            throw new RuntimeException("Insert customer_profile failed: " + e.getMessage(), e);
        }
    }
}
