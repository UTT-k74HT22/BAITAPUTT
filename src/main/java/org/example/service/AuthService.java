package org.example.service;

import org.example.entity.dto.RegisterRequest;

public interface AuthService {

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    boolean login(String username, String password);

    /**
     * Đăng ký tài khoản mới.
     * @param request thông tin đăng ký
     */
    void register(RegisterRequest request);
}
