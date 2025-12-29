package org.example.service;

import org.example.dto.response.ProfileResponse;
import org.example.entity.enums.Role;

public interface ProfileService {

    ProfileResponse loadProfile(String username, Role role);
}
