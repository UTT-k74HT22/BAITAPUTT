package org.example.controller;

import org.example.entity.Account;
import org.example.service.AccountService;
import java.util.List;

/**
 * AccountController class to handle account-related operations.
 */
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Lấy tất cả tài khoản.
     * @return danh sách tài khoản
     */
    public List<Account> getAllAccounts() {
        return accountService.getAll();
    }

    /**
     * Tìm tài khoản theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    public Account findByUsername(String username) {
        return accountService.findByUsername(username);
    }

    /**
     * Tạo tài khoản mới.
     * @param account tài khoản cần tạo
     */
    public void createAccount(Account account) {
        System.out.println("[Controller] Tạo tài khoản mới: " + account.getUsername());
        accountService.create(account);
    }

    /**
     * Cập nhật tài khoản.
     * @param account tài khoản cần cập nhật
     */
    public void updateAccount(Account account) {
        accountService.update(account);
    }

    /**
     * Xóa tài khoản theo ID.
     * @param id ID của tài khoản cần xóa
     */
    public void deleteAccountById(int id) {
        accountService.deleteById(id);
    }

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */
    public boolean existsByUsername(String username) {
        return accountService.existsByUsername(username);
    }
}
