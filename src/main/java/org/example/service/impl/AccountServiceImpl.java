package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.entity.Account;
import org.example.service.AccountService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private Connection connection = DatabaseConfig.getConnection();
    private Statement statement;
    private ResultSet resultSet;

    /**
     * Lấy tất cả tài khoản.
     *
     * @return danh sách tài khoản
     */
    @Override
    public List<Account> getAll() {
        System.out.println("Lấy danh sách tài khoản");

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try {

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Account account = new Account();
                account = mapRowToAccount(resultSet);
                accounts.add(account);
            }
            System.out.println("Lấy danh sách tài khoản thành công");
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return accounts;
    }

    /**
     * Tìm tài khoản theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    @Override
    public Account findByUsername(String username) {
        System.out.println("Tìm tài khoản với username: " + username);
        String sql = "SELECT * FROM accounts WHERE username LIKE '%" + username + "%'";
        Account account = null;
        try {

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                account = mapRowToAccount(resultSet);
            }
            System.out.println("Tìm tài khoản thành công");
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm tài khoản: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return account;
    }

    /**
     * Tạo tài khoản mới.
     *
     * @param account tài khoản cần tạo
     */
    @Override
    public void create(Account account) {
        System.out.println("Tạo tài khoản mới: " + account.getUsername());
//        String sql = "INSERT INTO accounts (username, password, full_name, role, is_active) " +
//                "VALUES ('" + account.getUsername() + "', '" + account.getPassword() + "', '" +
//                account.getFullName() + "', '" + account.getRole() + "', " + account.isActive() + ")";

        String sql = "INSERT INTO accounts (username, password, full_name, role, is_active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getFullName());
            ps.setString(4, account.getRole());
            ps.setBoolean(5, account.isActive());
            ps.executeUpdate();
            System.out.println("Tạo tài khoản thành công");
        } catch (Exception e) {
            System.out.println("Lỗi khi tạo tài khoản: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Cập nhật tài khoản.
     *
     * @param account tài khoản cần cập nhật
     */
    @Override
    public void update(Account account) {
        System.out.println("Cập nhật tài khoản: " + account.getUsername());
        String sql = "UPDATE accounts SET " +
                "password = '" + account.getPassword() + "', " +
                "full_name = '" + account.getFullName() + "', " +
                "role = '" + account.getRole() + "', " +
                "is_active = " + account.isActive() + " " +
                "WHERE id = " + account.getId();
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Cập nhật tài khoản thành công");
        } catch (Exception e) {
            System.out.println("Lỗi khi cập nhật tài khoản: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Xóa tài khoản theo ID.
     *
     * @param id ID của tài khoản cần xóa
     */
    @Override
    public void deleteById(int id) {
        System.out.println("Xóa tài khoản với ID: " + id);
        String sql = "DELETE FROM accounts WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa account", e);
        }
    }

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM accounts WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại username", e);
        }
    }

    // ========== PRIVATE METHODS ========== //
    /**
     * Chuyển ResultSet thành đối tượng Account.
     *
     * @param rs ResultSet từ truy vấn
     * @return đối tượng Account
     * @throws SQLException nếu có lỗi khi truy xuất dữ liệu
     */
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        Account acc = new Account();
        acc.setId(rs.getInt("id"));
        acc.setUsername(rs.getString("username"));
        acc.setPassword(rs.getString("password"));
        acc.setFullName(rs.getString("full_name"));
        acc.setRole(rs.getString("role"));
        acc.setActive(rs.getBoolean("is_active"));
        return acc;
    }
}
