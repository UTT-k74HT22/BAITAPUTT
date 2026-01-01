package org.example.repository.impl;

import org.example.entity.Account;
import org.example.entity.enums.Role;
import org.example.repository.AccountRepository;
import org.example.repository.BaseRepository;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl extends BaseRepository implements AccountRepository {

    /**
     * Lấy tất cả tài khoản.
     *
     * @return danh sách tài khoản
     */
    @Override
    public List<Account> findAll() {
        String sql = """
            SELECT id, username, password, role, is_active, last_login_at
            FROM accounts
        """;

        return queryForList(sql, this::mapRow);
    }

    /**
     * Tìm tài khoản theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    @Override
    public Account findByUsername(String username) {
        String sql = """
            SELECT id, username, password, role, is_active, last_login_at
            FROM accounts
            WHERE username = ?
        """;
        Optional<Account> account = queryForObject(sql, params(username), this::mapRow);
        return account.orElse(null);
    }

    /**
     * Tìm tài khoản theo ID.
     *
     * @param id ID của tài khoản
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    @Override
    public Account findById(Long id) {
        String sql = """
            SELECT id, username, password, role, is_active, last_login_at
            FROM accounts
            WHERE id = ?
        """;
        Optional<Account> account = queryForObject(sql, params(id), this::mapRow);
        return account.orElse(null);
    }

    /**
     * Chèn tài khoản mới và trả về ID đã chèn.
     *
     * @param conn kết nối cơ sở dữ liệu
     * @param a    tài khoản cần chèn
     * @return ID của tài khoản đã chèn
     */
    @Override
    public long insert(Connection conn, Account a) {
        String sql = """
                INSERT INTO accounts(username, password, role, is_active, last_login_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try {
            return executeInsertAndReturnId(conn, sql, ps -> {
                ps.setString(1, a.getUsername());
                ps.setString(2, a.getPassword());
                ps.setString(3, a.getRole().name());
                ps.setInt(4, a.isActive() ? 1 : 0);
                ps.setObject(5, null); // last_login_at
            });
        } catch (Exception e) {
            throw new RuntimeException("Insert account failed: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật vai trò và trạng thái kích hoạt của tài khoản.
     *
     * @param id     ID của tài khoản cần cập nhật
     * @param role   vai trò mới
     * @param active trạng thái kích hoạt mới
     */
    @Override
    public void updateRoleAndStatus(long id, Role role, boolean active) {
        String sql = """
            UPDATE accounts
            SET role = ?, is_active = ?
            WHERE id = ?
        """;
        executeUpdate(sql, params(role.name(), active, id));
    }

    /**
     * Xóa tài khoản theo ID.
     *
     * @param id ID của tài khoản cần xóa
     */
    @Override
    public void deleteById(Long id) {
        String sql = """
            UPDATE accounts
            SET is_active = FALSE
            WHERE id = ?
        """;
        executeUpdate(sql, params(id));
    }

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */
    @Override
    public boolean existsByUsername(String username) {
        String sql = """
            SELECT 1
            FROM accounts
            WHERE username = ?
            LIMIT 1
        """;
        return exists(sql, params(username));
    }

    /**
     * Kiểm tra tài khoản tồn tại theo email.
     * @param email email của tài khoản
     * @return true nếu tồn tại, false nếu không
     */
    @Override
    public boolean existsByEmail(String email) {
        String sql  = """
            SELECT 1
            FROM accounts
            WHERE email = ?
            LIMIT 1
        """;
        return exists(sql, params(email));
    }

    @Override
    public void updatePassword(long accountId, String hashedPassword) {
        String sql = "UPDATE accounts SET password = ? WHERE id = ?";
        executeUpdate(sql, ps -> {
            ps.setString(1, hashedPassword);
            ps.setLong(2, accountId);
        });
    }


    /**
     * Map ResultSet to Entity.
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setId(rs.getLong("id"));
        acc.setUsername(rs.getString("username"));
        acc.setPassword(rs.getString("password"));
        String roleStr = rs.getString("role");
        if (roleStr != null && !roleStr.isBlank()) {
            acc.setRole(Role.valueOf(roleStr));
        }
        acc.setActive(rs.getBoolean("is_active"));
        Timestamp ts = rs.getTimestamp("last_login_at");
        if (ts != null) {
            acc.setLastLoginAt(ts.toLocalDateTime());
        }
        return acc;
    }

    /**
     * Convert LocalDateTime to Timestamp.
     */
    private Timestamp toTimestamp(java.time.LocalDateTime time) {
        return time == null ? null : Timestamp.valueOf(time);
    }
}
