package org.example.repository.impl;

import org.example.dto.request.UpdateEmployeeProfileDetailRequest;
import org.example.dto.response.ProfileResponse;
import org.example.entity.UserProfile;
import org.example.entity.enums.Role;
import org.example.repository.BaseRepository;
import org.example.repository.UserProfileRepository;
import java.sql.Connection;
import java.util.Optional;

public class UserProfileRepositoryImpl extends BaseRepository implements UserProfileRepository {
    /**
     * Chèn hồ sơ người dùng mới và trả về ID đã chèn.
     *
     * @param conn      kết nối cơ sở dữ liệu
     * @param accountId ID tài khoản liên kết
     * @param code      mã hồ sơ
     * @param fullName  họ và tên đầy đủ
     * @param phone     số điện thoại
     * @param email     email
     * @param address   địa chỉ
     * @return ID của hồ sơ người dùng đã chèn
     */
    @Override
    public long insert(Connection conn, long accountId, String code, String fullName, String phone, String email, String address) {
        String sql = """
                INSERT INTO user_profiles(account_id, code, full_name, phone, email, address)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try {
            return executeInsertAndReturnId(conn, sql, ps -> {
                ps.setLong(1, accountId);
                ps.setString(2, code);
                ps.setString(3, fullName);
                ps.setString(4, phone);
                ps.setString(5, email);
                ps.setString(6, address);
            });
        } catch (Exception e) {
            throw new RuntimeException("Insert user_profile failed: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo mã hồ sơ người dùng tiếp theo dựa trên tiền tố cho trước.
     *
     * @param conn   kết nối cơ sở dữ liệu
     * @param prefix tiền tố mã hồ sơ
     * @return mã hồ sơ người dùng tiếp theo
     */
    @Override
    public String generateNextCode(Connection conn, String prefix) {
        String sql = """
                SELECT code
                FROM user_profiles
                WHERE code LIKE ?
                ORDER BY code DESC
                LIMIT 1
                FOR UPDATE
                """;
        try {
            String last = queryForObject(conn, sql, params(prefix + "%"),
                    rs -> rs.getString("code")).orElse(null);

            int next = 1;
            if (last != null && last.startsWith(prefix)) {
                String num = last.substring(prefix.length()).replaceAll("[^0-9]", "");
                if (!num.isBlank()) next = Integer.parseInt(num) + 1;
            }
            return prefix + String.format("%03d", next);
        } catch (Exception e) {
            throw new RuntimeException("Generate code failed: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm ID hồ sơ người dùng dựa trên mã hồ sơ.
     *
     * @param code mã hồ sơ
     * @return Optional chứa ID hồ sơ nếu tìm thấy, ngược lại là rỗng
     */
    @Override
    public Optional<Long> findIdByCode(String code) {
        String sql = """
                SELECT id
                FROM user_profiles
                WHERE code = ?
                """;
        return queryForObject(sql, params(code), rs -> rs.getLong("id"));
    }

    /**
     * Cập nhật thông tin cơ bản của hồ sơ người dùng dựa trên mã hồ sơ.
     *
     * @param
     * @param
     */
    @Override
    public void updateMyProfileByAccountId(
            long accountId,
            String fullName,
            String phone,
            String email,
            String address
    ) {
        String sql = """
        UPDATE user_profiles
        SET full_name = ?,
            phone = ?,
            email = ?,
            address = ?
        WHERE account_id = ?
    """;

        executeUpdate(sql, ps -> {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setString(4, address);
            ps.setLong(5, accountId);
        });
    }


    /**
     * Cập nhật chi tiết hồ sơ người dùng dựa trên mã hồ sơ.
     *
     * @param code    mã hồ sơ
     * @param request đối tượng yêu cầu chứa thông tin cập nhật chi tiết
     */
    @Override
    public void updateDetailByCode(String code, UpdateEmployeeProfileDetailRequest request) {
        String sql = """
        UPDATE user_profiles
        SET full_name = ?, phone = ?, email = ?, address = ?, status = ?
        WHERE code = ?
        """;
        executeUpdate(sql, ps -> {
            ps.setString(1, request.getFullName());
            ps.setString(2, request.getPhone());
            ps.setString(3, request.getEmail());
            ps.setString(4, request.getAddress());
            ps.setString(5, request.getStatus() == null ? null : request.getStatus().name());
            ps.setString(6, code);
        });
    }

    @Override
    public Optional<UserProfile> findByAccountId(long accountId) {
        String sql = """
                SELECT *
                FROM user_profiles
                WHERE account_id = ?
        """;
        return queryForObject(sql, params(accountId), rs -> {
            UserProfile p = new UserProfile();
            p.setId(rs.getLong("id"));
            p.setAccountId(rs.getLong("account_id"));
            p.setCode(rs.getString("code"));
            p.setFullName(rs.getString("full_name"));
            p.setPhone(rs.getString("phone"));
            p.setEmail(rs.getString("email"));
            p.setAddress(rs.getString("address"));
            return p;
        });
    }

    @Override
    public Optional<ProfileResponse> findProfileByUsername(String username) {
        String sql = """
        SELECT a.username,
               a.role,
               up.code,
               up.full_name,
               up.phone,
               up.email,
               up.address,
               ep.position,
               ep.department,
               cp.customer_type,
               cp.tax_code
        FROM accounts a
        JOIN user_profiles up ON a.id = up.account_id
        LEFT JOIN employee_profiles ep ON ep.profile_id = up.id
        LEFT JOIN customer_profiles cp ON cp.profile_id = up.id
        WHERE a.username = ?
    """;

        return queryForObject(sql, params(username), rs -> {
            ProfileResponse p = new ProfileResponse();
            p.setUsername(rs.getString("username"));
            p.setRole(Role.valueOf(rs.getString("role")));
            p.setCode(rs.getString("code"));
            p.setFullName(rs.getString("full_name"));
            p.setPhone(rs.getString("phone"));
            p.setEmail(rs.getString("email"));
            p.setAddress(rs.getString("address"));
            p.setPosition(rs.getString("position"));
            p.setDepartment(rs.getString("department"));
            p.setCustomerType(rs.getString("customer_type"));
            p.setTaxCode(rs.getString("tax_code"));
            return p;
        });
    }
}
