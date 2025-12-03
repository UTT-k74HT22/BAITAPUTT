package org.example.service;

public interface AuthService {

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    boolean login(String username, String password);
}
