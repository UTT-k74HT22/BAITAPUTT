package org.example.service.impl;

import org.example.dto.response.AccountResponse;
import org.example.entity.Account;
import org.example.entity.enums.Role;
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

    @Override
    public List<AccountResponse> getAllAccount() {
        List<AccountResponse> responses = this.getAll().stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getUsername(),
                        account.getRole(),
                        account.isActive(),
                        account.getLastLoginAt()
                ))
                .toList();
        return  responses;
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
     * Tìm tài khoản theo ID.
     *
     * @param id ID của tài khoản
     * @return tài khoản hoặc null nếu không tìm thấy
     */
    @Override
    public Account findById(Long id) {
        System.out.println("Tìm tài khoản với ID: " + id);
        return accountRepository.findById(id);
    }

    /**
     * Cập nhật tài khoản.
     *
     * @param accountId ID của tài khoản cần cập nhật
     * @param role      vai trò mới
     * @param active    trạng thái kích hoạt mới
     */
    @Override
    public void update(Long accountId, Role role, Boolean active) {
        System.out.println("Cập nhật tài khoản với ID: " + accountId);
        accountRepository.updateRoleAndStatus(accountId, role, active);
    }


    /**
     * Xóa tài khoản theo ID.
     *
     * @param id ID của tài khoản cần xóa
     */
    @Override
    public void deleteById(Long id) {
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
