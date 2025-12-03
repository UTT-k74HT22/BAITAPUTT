package org.example.service.impl;

import org.example.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public boolean login(String username, String password) {
        if (username == null || password == null) return false;
        return DEFAULT_USERNAME.equals(username.trim())
                && DEFAULT_PASSWORD.equals(password.trim());
    }
}
