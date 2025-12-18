package org.example.service;

import org.example.dto.response.AccountResponse;
import org.example.entity.Account;
import org.example.entity.enums.Role;

import java.util.List;

public interface AccountService {

    /**
     * Lấy tất cả tài khoản.
     * @return danh sách tài khoản
     */
    List<Account> getAll();

    List<AccountResponse> getAllAccount();

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
     * Cập nhật tài khoản.
     * @param accountId ID của tài khoản cần cập nhật
     * @param role vai trò mới
     * @param active trạng thái kích hoạt mới
     */
    void update(Long accountId, Role role, Boolean active);

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
}

