package org.example.service.impl;

import org.example.dto.RegisterRequest;
import org.example.dto.request.CreateCustomerRequest;
import org.example.dto.request.CreateEmployeeRequest;
import org.example.entity.Account;
import org.example.entity.enums.Role;
import org.example.repository.*;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.repository.impl.CustomerProfileRepositoryImpl;
import org.example.repository.impl.EmployeeProfileRepositoryImpl;
import org.example.repository.impl.UserProfileRepositoryImpl;
import org.example.service.UserCreationService;
import org.mindrot.jbcrypt.BCrypt;

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
        System.out.println("[UserCreationService] registerCustomer called");
        validateRegister(request);
        String username = request.getUsername().trim();

        if (accountRepo.existsByUsername(username)) {
            System.out.println("[UserCreationService] Username already exists: " + username);
            throw new  RuntimeException("Username already exists");
        }

        return inTransaction(conn -> {
            //1. Tạo account
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(request.getPassword().trim());
            account.setRole(Role.CUSTOMER);
            account.setActive(true);

            long accountId = accountRepo.insert(conn, account);
            System.out.println("[UserCreationService] Created account with ID: " + accountId);

            //2. Tạo profile người dùng
            String code = profileRepo.generateNextCode(conn, "CUS");
            long profileId = profileRepo.insert(conn, accountId, code,
                    "Customer", null, null, null);

            // 3) customer_profiles (default)
            cusRepo.insert(conn, profileId, "RETAIL", null);
            System.out.println("[UserCreationService] Created customer profile with ID: " + profileId);
            return profileId;
        });
    }

    @Override
    public long registerEmployee(RegisterRequest request) {
        return 0;
    }

    @Override
    public long createCustomer(CreateCustomerRequest req) {
        System.out.println("[UserCreationService] createCustomer called");
        if (req == null) throw new IllegalArgumentException("Request null");

        String username = req.getUsername().trim();
        if (accountRepo.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        return inTransaction(conn -> {
            Account acc = new Account();
            acc.setUsername(username);
            acc.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt(12)));
            acc.setRole(Role.CUSTOMER);
            acc.setActive(true);
            acc.setEmail(emptyToNull(req.getEmail()));
            acc.setPhone(emptyToNull(req.getPhone()));

            long accountId = accountRepo.insert(conn, acc);

            System.out.println("[UserCreationService] Created account with ID: " + accountId);

            String code = profileRepo.generateNextCode(conn, "CUS");
            long profileId = profileRepo.insert(conn, accountId, code,
                    req.getFullName(), emptyToNull(req.getPhone()),
                    emptyToNull(req.getEmail()), emptyToNull(req.getAddress()));

            cusRepo.insert(conn, profileId, String.valueOf(req.getCustomerType()), emptyToNull(req.getTaxCode()));

            System.out.println("[UserCreationService] Created customer profile with ID: " + profileId);
            return profileId;
        });
    }

    @Override
    public long createEmployee(CreateEmployeeRequest req) {
        System.out.println("[UserCreationService] createEmployee called");
        if (req == null) throw new IllegalArgumentException("Request null");

        String username = req.getUsername().trim();
        if (accountRepo.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        return inTransaction(conn -> {
            //1. Tạo account
            Account acc = new Account();
            acc.setUsername(username);
            acc.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt(12)));
            acc.setRole(Role.EMPLOYEE);
            acc.setActive(true);
            acc.setEmail(emptyToNull(req.getEmail()));
            acc.setPhone(emptyToNull(req.getPhone()));

            long accountId = accountRepo.insert(conn, acc);

            System.out.println("[UserCreationService] Created account with ID: " + accountId);

            //2. Tạo profile người dùng
            String code = profileRepo.generateNextCode(conn, "EMP");
            long profileId = profileRepo.insert(conn, accountId, code,
                    req.getFullName(), emptyToNull(req.getPhone()),
                    emptyToNull(req.getEmail()), emptyToNull(req.getAddress()));

            empRepo.insert(conn, profileId,
                    req.getPosition(), req.getDepartment(),
                    req.getHiredDate(), req.getSalary());

            System.out.println("[UserCreationService] Created employee profile with ID: " + profileId);

            return profileId;
        });
    }

    //Validation
    private void validateRegister(RegisterRequest req) {
        if (req == null) throw new IllegalArgumentException("Request null");
        if (req.getUsername() == null || req.getUsername().isBlank()) throw new IllegalArgumentException("Username trống");
        if (req.getPassword() == null || req.getPassword().isBlank()) throw new IllegalArgumentException("Password trống");
        if (req.getConfirmPassword() == null || req.getConfirmPassword().isBlank()) throw new IllegalArgumentException("Confirm trống");
        if (!req.isPasswordConfirmed()) throw new IllegalArgumentException("Password không khớp");
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
