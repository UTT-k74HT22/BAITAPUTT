package org.example.service.impl;

import org.example.entity.Account;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;

    public AuthServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
    }

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    @Override
    public Account login(LoginRequest request) {
        System.out.println("Login running with username: "  + request.getUsername());

        if (request.getUsername() == null || request.getPassword() == null) {
            System.out.println("Login failed: username or password is null");
            return null;
        }

        Account account = accountRepository.findByUsername(request.getUsername());
        if (account == null) {
            System.out.println("Login failed: account not found");
            return null;
        }

        if (!account.isActive()) {
            System.out.println("Login failed: account is inactive");
            return null;
        }

        if (!account.getPassword().equals(request.getPassword())) {
            System.out.println("Login failed: incorrect password");
            return null;
        }

        System.out.println("Login successful: " + account.getUsername());
        return account;
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

        accountRepository.save(account);
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
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        if (!request.isPasswordConfirmed()) {
            throw new RuntimeException("Password và Confirm Password không khớp!");
        }
    }
}
