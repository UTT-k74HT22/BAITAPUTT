package org.example.repository;

import org.example.entity.Account;
import org.example.entity.enums.Role;

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
     * Tìm tài khoản theo ID.
     * @param id ID của tài khoản
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    Account findById(Long id);

    /**
     * Chèn tài khoản mới và trả về ID đã chèn.
     * @param conn kết nối cơ sở dữ liệu
     * @param a tài khoản cần chèn
     * @return ID của tài khoản đã chèn
     */
    long insert(Connection conn, Account a);


    /**
     * Cập nhật vai trò và trạng thái kích hoạt của tài khoản.
     * @param id ID của tài khoản cần cập nhật
     * @param role vai trò mới
     * @param active trạng thái kích hoạt mới
     */
    void updateRoleAndStatus(long id, Role role, boolean active);

    /**
     * Xóa tài khoản theo ID.
     * @param id ID của tài khoản cần xóa
     */
    void deleteById(Long id);

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

    /**
     *
     * @param accountId
     * @param hashedPassword
     */
    void updatePassword(long accountId, String hashedPassword);

    Long findUserIdByAccountId(Long accountId);
}
