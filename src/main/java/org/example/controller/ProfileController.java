package org.example.controller;

import org.example.entity.enums.Role;
import org.example.dto.response.ProfileResponse;
import org.example.service.ProfileService;

public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
        ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    public ProfileResponse loadProfile(String username, Role role) {
        return profileService.loadProfile(username, role);
    }
}

