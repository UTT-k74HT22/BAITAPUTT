package org.example.service.impl;

import org.example.entity.Account;
import org.example.repository.AccountRepository;
import org.example.repository.impl.AccountRepositoryImpl;
import org.example.service.AccountService;
import java.sql.*;
import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl() {
        this.accountRepository = new AccountRepositoryImpl();
    }

    /**
     * Lấy tất cả tài khoản.
     *
     * @return danh sách tài khoản
     */
    @Override
    public List<Account> getAll() {
        System.out.println("Lấy danh sách tài khoản");
        return accountRepository.findAll();
    }

    /**
     * Tìm tài khoản theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    @Override
    public Account findByUsername(String username) {
        System.out.println("Tìm tài khoản với username: " + username);
        return accountRepository.findByUsername(username);
    }

    /**
     * Tạo tài khoản mới.
     *
     * @param account tài khoản cần tạo
     */
    @Override
    public void create(Account account) {
        System.out.println("[Service] Tạo tài khoản mới: " + (account != null ? account.getUsername() : "null"));

        if (account == null) throw new IllegalArgumentException("Account không được null");

        String username = account.getUsername() == null ? "" : account.getUsername().trim();
        String password = account.getPassword() == null ? "" : account.getPassword().trim();
        String email = account.getEmail() == null ? "" : account.getEmail().trim();
        String phone = account.getPhone() == null ? "" : account.getPhone().trim();

        if (username.isEmpty()) throw new IllegalArgumentException("Username không được để trống");
        if (password.isEmpty()) throw new IllegalArgumentException("Password không được để trống");

        if (accountRepository.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        if (!email.isEmpty() && accountRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email.isEmpty() ? null : email);
        account.setPhone(phone.isEmpty() ? null : phone);

        if (account.getRole() == null) {
            account.setRole(org.example.entity.enums.Role.EMPLOYEE);
        }

        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            if (isDuplicateKey(ex)) {
                throw new RuntimeException("Username hoặc Email đã tồn tại!");
            }
            throw new RuntimeException("Lỗi khi tạo tài khoản: " + ex.getMessage(), ex);
        }
    }

    /**
     * Cập nhật tài khoản.
     *
     * @param account tài khoản cần cập nhật
     */
    @Override
    public void update(Account account) {
        System.out.println("Cập nhật tài khoản: " + account.getUsername());
        accountRepository.update(account);
    }

    /**
     * Xóa tài khoản theo ID.
     *
     * @param id ID của tài khoản cần xóa
     */
    @Override
    public void deleteById(int id) {
        System.out.println("Xóa tài khoản với ID: " + id);
        accountRepository.deleteById(id);
    }

    /**
     * Kiểm tra tài khoản tồn tại theo tên đăng nhập.
     *
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu không
     */
    @Override
    public boolean existsByUsername(String username) {
        System.out.println("Check exists username: " + username);
        return accountRepository.existsByUsername(username);
    }

    /**
     * Kiểm tra xem ngoại lệ có phải do vi phạm khóa trùng lặp không.
     *
     * @param ex ngoại lệ cần kiểm tra
     * @return true nếu là lỗi khóa trùng lặp, false nếu không
     */
    private boolean isDuplicateKey(Exception ex) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof java.sql.SQLIntegrityConstraintViolationException) return true;
            if (t instanceof java.sql.SQLException sqlEx) {
                if ("23000".equals(sqlEx.getSQLState())) return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
