package org.example.repository;

import org.example.entity.User;

import java.util.List;

public interface UserRepository {

    /**
     * Lấy danh sách User
     * @return Trả về tất cả user
     */
    List<User> findAll();

}
