package org.example.repository;

import org.example.entity.Account;

import java.sql.Connection;
import java.util.List;

public interface AccountRepository {

    /**
     * Lấy tất cả tài khoản.
     * @return danh sách tài khoản
     */
    List<Account> findAll();

    /**
     * Tìm tài khoản theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    Account findByUsername(String username);

    /**
     * Lưu tài khoản mới.
     * @param account tài khoản cần lưu
     */
    void save(Account account);

    /**
     * Chèn tài khoản mới và trả về ID đã chèn.
     * @param conn kết nối cơ sở dữ liệu
     * @param a tài khoản cần chèn
     * @return ID của tài khoản đã chèn
     */
    long insert(Connection conn, Account a);

    /**
     * Cập nhật tài khoản.
     * @param account tài khoản cần cập nhật
     */
    void update(Account account);

    /**
     * Xóa tài khoản theo ID.
     * @param id ID của tài khoản cần xóa
     */
    void deleteById(int id);

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByUsername(String username);

    /**
     * Kiểm tra tài khoản tồn tại theo email.
     * @param email email của tài khoản
     * @return true nếu tồn tại, false nếu không
     */
    boolean existsByEmail(String email);
}
