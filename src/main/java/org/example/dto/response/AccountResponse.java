package org.example.dto.response;

import org.example.entity.enums.Role;
import java.time.LocalDateTime;

public class AccountResponse {
    private Long id;
    private String username;
    private Role role;
    private Boolean active;
    private LocalDateTime lastLoginAt;

    public AccountResponse(Long id, String username, Role role, boolean active, LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
