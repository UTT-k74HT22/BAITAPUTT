package org.example.repository.impl;

import org.example.dto.request.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.UpdateProfileEmployeeRequest;
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
     * @param code    mã hồ sơ
     * @param request đối tượng yêu cầu chứa thông tin cập nhật
     */
    @Override
    public void updateBasicByCode(String code, UpdateProfileEmployeeRequest request) {
        String sql = """
                UPDATE user_profiles
                SET full_name = ?,
                    phone = ?,
                    status = ?
                WHERE code = ?
                """;
        executeUpdate(sql, ps -> {
            ps.setString(1, request.getFullName());
            ps.setString(2, request.getPhone());
            ps.setString(3, request.getStatus().name());
            ps.setString(4, code);
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
}
