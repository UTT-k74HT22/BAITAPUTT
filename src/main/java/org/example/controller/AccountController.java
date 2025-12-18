package org.example.controller;

import org.example.dto.request.CreateAccountRequest;
import org.example.dto.response.AccountResponse;
import org.example.entity.Account;
import org.example.entity.enums.Role;
import org.example.service.AccountService;
import org.example.service.UserCreationService;
import org.example.utils.excel.AccountExcelWriter;
import org.example.utils.excel.EmployeeExcelWriter;
import org.example.utils.excel.ExcelExporter;

import java.awt.*;
import java.util.List;

/**
 * AccountController class to handle account-related operations.
 */
public class AccountController {

    private final AccountService accountService;
    private final UserCreationService userCreationService;

    public AccountController(AccountService accountService, UserCreationService userCreationService) {
        this.accountService = accountService;
        this.userCreationService = userCreationService;
    }

    /**
     * Lấy tất cả tài khoản.
     * @return danh sách tài khoản
     */
    public List<Account> getAllAccounts() {
        return accountService.getAll();
    }

    /**
     * Lấy tất cả tài khoản dưới dạng AccountResponse.
     * @return danh sách AccountResponse
     */
    public void getAllAccountResponses(Component component) {
        System.out.println("[Controller] Lấy tất cả tài khoản dưới dạng AccountResponse");
        ExcelExporter.exportWithDialog(
                accountService.getAllAccount(),
                new AccountExcelWriter(),
                component
        );
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
     * Tìm tài khoản theo ID.
     * @param id ID của tài khoản
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    public Account findById(Long id) {
        System.out.println("[Controller] Tìm tài khoản với ID: " + id);
        return accountService.findById(id);
    }

    /**
     * Tạo tài khoản mới.
     * @param request tài khoản cần tạo
     */
    public long createAccount(CreateAccountRequest request) {
        System.out.println("[Controller] Tạo tài khoản mới: " + request.getUsername());
        return userCreationService.createAccount(request);
    }

    /**
     * Cập nhật vai trò và trạng thái kích hoạt của tài khoản.
     * @param accountId ID của tài khoản cần cập nhật
     * @param role vai trò mới
     * @param active trạng thái kích hoạt mới
     */
    public void updateRoleAndStatus(long accountId, Role role, boolean active) {
        accountService.update(accountId, role, active);
    }

    /**
     * Xóa tài khoản theo ID.
     * @param id ID của tài khoản cần xóa
     */
    public void deleteAccountById(Long id) {
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
