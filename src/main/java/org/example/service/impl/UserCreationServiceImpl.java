package org.example.service.impl;

import org.example.dto.request.auth.RegisterRequest;
import org.example.dto.request.CreateAccountRequest;
import org.example.entity.Account;
import org.example.entity.enums.CustomerType;
import org.example.entity.enums.Role;
import org.example.repository.*;
import org.example.repository.impl.*;
import org.example.service.UserCreationService;
import org.mindrot.jbcrypt.BCrypt;
import java.math.BigDecimal;

public class UserCreationServiceImpl extends BaseRepository implements UserCreationService {

    private final AccountRepository accountRepo;
    private final UserProfileRepository profileRepo;
    private final EmployeeProfileRepository empRepo;
    private final CustomerProfileRepository cusRepo;

    public UserCreationServiceImpl() {
        this.accountRepo = new AccountRepositoryImpl();
        this.profileRepo = new UserProfileRepositoryImpl();
        this.empRepo = new EmployeeProfileRepositoryImpl();
        this.cusRepo = new CustomerProfileRepositoryImpl();
    }

    @Override
    public long registerCustomer(RegisterRequest request) {
        validateRegister(request);

        String username = request.getUsername().trim();
        if (accountRepo.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        return inTransaction(conn -> {
            // 1) account
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(BCrypt.hashpw(request.getPassword().trim(), BCrypt.gensalt(12)));
            account.setRole(Role.CUSTOMER);
            account.setActive(true);

            long accountId = accountRepo.insert(conn, account);

            // 2) user_profiles
            String code = profileRepo.generateNextCode(conn, "CUS");
            long profileId = profileRepo.insert(
                    conn,
                    accountId,
                    code,
                    "Customer",
                    null,
                    null,
                    null
            );

            // 3) customer_profiles
            cusRepo.insert(conn, profileId, "RETAIL", null);

            return accountId;
        });
    }

    @Override
    public long createAccount(CreateAccountRequest request) {
        validateCreateAccount(request);

        String username = request.getUsername().trim();
        if (accountRepo.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại");
        }

        return inTransaction(conn -> {
            // 1) accounts
            Account acc = new Account();
            acc.setUsername(username);
            acc.setPassword(BCrypt.hashpw(request.getPassword().trim(), BCrypt.gensalt(12)));
            acc.setRole(request.getRole());
            acc.setActive(request.isActive());

            long accountId = accountRepo.insert(conn, acc);

            // 2) user_profiles
            String prefix = request.getRole() == Role.EMPLOYEE ? "EMP" : "CUS";
            String code = profileRepo.generateNextCode(conn, prefix);

            String fullName = safeFullName(request.getFullName(), request.getRole());
            long profileId = profileRepo.insert(
                    conn,
                    accountId,
                    code,
                    fullName,
                    emptyToNull(request.getPhone()),
                    emptyToNull(request.getEmail()),
                    emptyToNull(request.getAddress())
            );

            // 3) role profile
            switch (request.getRole()) {
                case EMPLOYEE -> empRepo.insert(conn, profileId,
                        null, null, null,
                        BigDecimal.ZERO
                );
                case CUSTOMER -> cusRepo.insert(conn, profileId, String.valueOf(CustomerType.RETAIL), null);
                default -> throw new IllegalStateException("Role not supported: " + request.getRole());
            }

            return accountId;
        });
    }

    // ================= VALIDATION =================
    private void validateCreateAccount(CreateAccountRequest req) {
        if (req == null) throw new IllegalArgumentException("Request null");

        if (req.getUsername() == null || req.getUsername().isBlank())
            throw new IllegalArgumentException("Username không được để trống");

        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new IllegalArgumentException("Password không được để trống");

        if (req.getConfirmPassword() == null || req.getConfirmPassword().isBlank())
            throw new IllegalArgumentException("Confirm Password không được để trống");

        if (!req.getPassword().equals(req.getConfirmPassword()))
            throw new IllegalArgumentException("Password và Confirm Password không khớp");

        if (req.getRole() == null)
            throw new IllegalArgumentException("Role không được để trống");
    }

    private void validateRegister(RegisterRequest req) {
        if (req == null) throw new IllegalArgumentException("Request null");
        if (req.getUsername() == null || req.getUsername().isBlank()) throw new IllegalArgumentException("Username trống");
        if (req.getPassword() == null || req.getPassword().isBlank()) throw new IllegalArgumentException("Password trống");
        if (req.getConfirmPassword() == null || req.getConfirmPassword().isBlank()) throw new IllegalArgumentException("Confirm trống");
        if (!req.isPasswordConfirmed()) throw new IllegalArgumentException("Password không khớp");
    }

    private String safeFullName(String fullName, Role role) {
        String v = emptyToNull(fullName);
        if (v != null) return v;
        return role == Role.EMPLOYEE ? "Employee" : "Customer";
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
