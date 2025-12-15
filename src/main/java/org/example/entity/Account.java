package org.example.entity;

import org.example.entity.enums.Role;

import java.time.LocalDateTime;

public class Account extends BaseEntity {

    private String username;
    private String password;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime lastLoginAt;

    public Account() {}

    public Account(String username, String password, String email, String phone, Role role, boolean active) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.active = active;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
