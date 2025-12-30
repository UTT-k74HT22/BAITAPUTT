package org.example.service;

import org.example.dto.request.auth.LoginRequest;
import org.example.dto.request.auth.RegisterRequest;
import org.example.entity.Account;

public interface AuthService {

    /**
     * Kiểm tra đăng nhập.
     * @return true nếu username/password hợp lệ
     */
    Account login(LoginRequest request);

    /**
     * Đăng ký tài khoản mới.
     * @param request thông tin đăng ký
     */
    void register(RegisterRequest request);
}
