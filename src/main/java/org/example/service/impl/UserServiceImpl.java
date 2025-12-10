package org.example.service.impl;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.UserService;
import java.util.List;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = new UserRepositoryImpl();
    }

    /**
     * Lấy danh sách User
     *
     * @return Trả về tất cả user
     */
    @Override
    public List<User> findAll() {
        System.out.println("Get all users");
        return userRepository.findAll();
    }
}
