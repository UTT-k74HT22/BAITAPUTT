package org.example.service.impl;

import org.example.entity.Account;
import org.example.entity.dto.RegisterRequest;
import org.example.service.AccountService;
import org.example.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;

    public AuthServiceImpl() {
        this.accountService = new AccountServiceImpl();
    }

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    @Override
    public boolean login(String username, String password) {
        System.out.println("Login running with username: "  + username);

        if (username == null || password == null) {
            System.out.println("Login failed: username or password is null");
            return false;
        }

        Account account = accountService.findByUsername(username);
        if (account != null) {
            if (!account.isActive()) {
                System.out.println("Login failed: account is inactive for username: " + username);
                return false;
            } else {
                if (account.getPassword().equals(password)) {
                    System.out.println("Login successful for username: " + username);
                    return true;
                } else {
                    System.out.println("Login failed: incorrect password for username: " + username);
                    return false;
                }
            }
        } else {
            System.out.println("Login failed: invalid credentials for username: " + username);
            return false;
        }
    }

    /**
     * Đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     */
    @Override
    public void register(RegisterRequest request) {
        System.out.println("Registering new account with username: " + request.getUsername());
        validateInfo(request);

        Account account = new Account(
                request.getUsername(),
                request.getPassword(),
                "USER",
                true
        );

        accountService.create(account);
        System.out.println("Registration successful for username: " + request.getUsername());
    }

    // ===================== PRIVATE METHODS =====================
    private void validateInfo(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new RuntimeException("Username không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password không được để trống");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            throw new RuntimeException("Confirm Password không được để trống");
        }

        // Check duplicate username
        if (accountService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        if (!request.isPasswordConfirmed()) {
            throw new RuntimeException("Password và Confirm Password không khớp!");
        }
    }
}
