package org.example.service;

import org.example.entity.User;

import java.util.List;

public interface UserService {
    /**
     * Lấy danh sách User
     * @return Trả về tất cả user
     */
    List<User> findAll();
}
