package org.example.repository.impl;

import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;
import org.example.entity.enums.ProfileStatus;
import org.example.repository.BaseRepository;
import org.example.repository.CustomerProfileRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<CustomerProfileResponse> findAll() {
        String sql = """
                SELECT
                    up.id              AS id,
                    cp.profile_id      AS profileId,
                    up.code            AS code,
                    up.full_name       AS fullName,
                    up.phone           AS phone,
                    cp.customer_type   AS customerType,
                    up.status          AS status
                FROM customer_profiles cp
                JOIN user_profiles up ON cp.profile_id = up.id
                ORDER BY up.id DESC
                """;
        return queryForList(sql, this::mapRow);
    }

    @Override
    public Optional<CustomerProfileResponse> findByCode(String code) {
        String sql = """
                SELECT
                    up.id            AS id,
                    cp.profile_id    AS profileId,
                    up.code          AS code,
                    up.full_name     AS fullName,
                    up.phone         AS phone,
                    cp.customer_type AS customerType,
                    up.status        AS status
                FROM customer_profiles cp
                JOIN user_profiles up ON cp.profile_id = up.id
                WHERE up.code = ?
                """;
        return queryForObject(sql, params(code), this::mapRow);
    }

    @Override
    public List<CustomerProfileResponse> search(String keyword, CustomerType type) {

        String sql = """
            SELECT
                up.id            AS id,
                cp.profile_id    AS profileId,
                up.code          AS code,
                up.full_name     AS fullName,
                up.phone         AS phone,
                cp.customer_type AS customerType,
                up.status        AS status
            FROM customer_profiles cp
            JOIN user_profiles up ON cp.profile_id = up.id
            WHERE
                ( ? IS NULL
                  OR up.code LIKE ?
                  OR up.full_name LIKE ?
                  OR up.phone LIKE ? )
            AND ( ? IS NULL OR cp.customer_type = ? )
            ORDER BY up.id DESC
            """;

        String key = (keyword == null || keyword.isBlank())
                ? null
                : "%" + keyword + "%";

        return queryForList(sql, ps -> {
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);

            ps.setString(5, type == null ? null : type.name());
            ps.setString(6, type == null ? null : type.name());
        }, this::mapRow);
    }

    @Override
    public void update(String code,
                       String full_name,
                       String phone,
                       CustomerType customerType,
                       String taxCode) {
        String sql = """
                UPDATE customer_profiles cp
                JOIN user_profiles up ON cp.profile_id = up.id
                SET up.full_name = ?,
                    up.phone = ?,
                    cp.customer_type = ?,
                    cp.tax_code = ?
                WHERE up.code = ?
                """;

        executeUpdate(sql, params(
                full_name,
                phone,
                customerType.name(),
                taxCode,
                code
        ));
    }

    @Override
    public void delete(String code) {
        String sql = """
                DELETE up
                FROM user_profiles up
                WHERE up.code = ?
                """;

        executeUpdate(sql, params(code));
    }

    private CustomerProfileResponse mapRow(ResultSet rs) throws SQLException {
        CustomerProfileResponse response = new CustomerProfileResponse();
        response.setId(rs.getLong("id"));
        response.setProfileId(rs.getLong("profileId"));
        response.setCode(rs.getString("code"));
        response.setFullName(rs.getString("fullName"));
        response.setPhone(rs.getString("phone"));
        response.setCustomerType(
                CustomerType.valueOf(rs.getString("customerType"))
        );
        response.setProfileStatus(
                ProfileStatus.valueOf(rs.getString("status"))
        );
        return response;
    }
}