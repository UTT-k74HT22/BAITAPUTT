
package org.example.service.impl;

import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AuthService;
import org.example.service.UserCreationService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Logger;

public class AuthServiceImpl implements AuthService {

    private static final Logger log = Logger.getLogger(AuthServiceImpl.class.getName());
    private final AccountRepository accountRepository;
    private final UserCreationService userCreationService;

    private static Account currentAccount;
    private static Long currentUserProfileId; // separate field for user_profile id

    public AuthServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
        this.userCreationService = new UserCreationServiceImpl();
    }

    /**
     * Find user profile id by account id
     * @param accountId
     * @return user profile id
     */
    public Long findUserIdByAccountId(Long accountId) {
        return ((AccountRepositoryImpl) accountRepository).findUserIdByAccountId(accountId);
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
        if (account == null) {
            log.info("Login failed: username not found - " + username);
            return null;
        }

        if (!account.isActive()) {
            log.info("Login failed: account inactive - " + username);
            return null;
        }

        if (!BCrypt.checkpw(password, account.getPassword())) {
            log.info("Login failed: incorrect password - " + username);
            return null;
        }

        currentAccount = account;

        Long userProfileId = accountRepository.findUserIdByAccountId(account.getId());

        // DO NOT overwrite account id. Store the user_profile id separately.
        currentUserProfileId = userProfileId;

        log.info("Login successful: " + username + " (user_profile_id=" + userProfileId + ")");
        return currentAccount;
    }

    // return the user_profile id (static so callers can use AuthServiceImpl.getCurrentUserProfileId())
    public static Long getCurrentUserProfileId() {
        return currentUserProfileId;
    }

    /**
     * Đăng ký tài khoản mới.
     *
     * @param request thông tin đăng ký
     */
    @Override
    public void register(RegisterRequest request) {
        System.out.println("[AuthService] Đăng ký tài khoản mới: " + (request != null ? request.getUsername() : "null"));
        if (request == null) {
            throw new RuntimeException("Request không được để trống");
        }
        validateInfo(request);
        userCreationService.registerCustomer(request);
    }

    public static Account getCurrentAccount() {
        return currentAccount;
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
