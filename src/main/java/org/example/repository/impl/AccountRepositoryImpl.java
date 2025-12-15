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
            SELECT id, username, password, email, phone, role, is_active, last_login_at
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
            SELECT id, username, password, email, phone, role, is_active, last_login_at
            FROM accounts
            WHERE username = ?
        """;
        Optional<Account> account = queryForObject(sql, params(username), this::mapRow);
        return account.orElse(null);
    }

    /**
     * Lưu tài khoản mới.
     *
     * @param account tài khoản cần lưu
     */
    @Override
    public void save(Account account) {
        String sql = """
            INSERT INTO accounts(username, password, email, phone, role, is_active, last_login_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        executeUpdate(sql, ps -> {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getPhone());
            ps.setString(5, account.getRole() != null ? account.getRole().name() : null);
            ps.setBoolean(6, account.isActive());
            ps.setTimestamp(7, toTimestamp(account.getLastLoginAt()));
        });
    }

    /**
     * Cập nhật tài khoản.
     *
     * @param account tài khoản cần cập nhật
     */
    @Override
    public void update(Account account) {
        String sql = """
            UPDATE accounts
            SET password = ?,
                email = ?,
                phone = ?,
                role = ?,
                is_active = ?,
                last_login_at = ?
            WHERE id = ?
        """;
        executeUpdate(sql, ps -> {
            ps.setString(1, account.getPassword());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.setString(4, account.getRole() != null ? account.getRole().name() : null);
            ps.setBoolean(5, account.isActive());
            ps.setTimestamp(6, toTimestamp(account.getLastLoginAt()));
            ps.setLong(7, account.getId());
        });
    }

    /**
     * Xóa tài khoản theo ID.
     *
     * @param id ID của tài khoản cần xóa
     */
    @Override
    public void deleteById(int id) {
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

    /**
     * Map ResultSet to Entity.
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setId(rs.getLong("id"));
        acc.setUsername(rs.getString("username"));
        acc.setPassword(rs.getString("password"));
        acc.setEmail(rs.getString("email"));
        acc.setPhone(rs.getString("phone"));
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
