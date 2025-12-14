package org.example.service.impl;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.entity.enums.Role;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class AuthServiceImpl implements AuthService {

    private static final Logger log = Logger.getLogger(AuthServiceImpl.class.getName());
    private final AccountRepository accountRepository;

    public AuthServiceImpl() {
        this(new AccountRepositoryImpl());
    }

    // Dùng constructor này để test / inject repo
    public AuthServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Kiểm tra đăng nhập.
     * @return Account nếu username/password hợp lệ, ngược lại trả null
     */
    @Override
    public Account login(LoginRequest request) {
        if (request == null) return null;

        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        Account account = accountRepository.findByUsername(username);
        if (account == null) return null;

        if (!account.isActive()) return null;

        if (!BCrypt.checkpw(password, account.getPassword())) {
            return null;
        }

        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.update(account);
        log.info("Login successful: " + username);
        return account;
    }

    /**
     * Đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     */
    @Override
    public void register(RegisterRequest request) {
        if (request == null) throw new RuntimeException("Request không được null");

        validateInfo(request);

        String hashedPassword = BCrypt.hashpw(
                request.getPassword(),
                BCrypt.gensalt(12) // cost factor = 12 (khuyến nghị)
        );

        Account account = new Account();
        account.setUsername(request.getUsername().trim());
        account.setPassword(hashedPassword);
        account.setRole(Role.CUSTOMER);            // hoặc Role.EMPLOYEE tùy hệ thống
        account.setActive(true);
        account.setLastLoginAt(null);

        accountRepository.save(account);
        log.info("Registration successful for username: " + account.getUsername());
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

        if (!request.isPasswordConfirmed()) {
            throw new RuntimeException("Password và Confirm Password không khớp!");
        }

        if (accountRepository.existsByUsername(request.getUsername().trim())) {
            throw new RuntimeException("Username đã tồn tại!");
        }
    }
}
