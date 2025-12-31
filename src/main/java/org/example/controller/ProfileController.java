package org.example.controller;

import org.example.dto.request.UpdateUserProfileRequest;
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

    public void updateMyProfile(long accountId, UpdateUserProfileRequest req) {
        profileService.updateMyProfile(accountId, req);
    }

    public void changePassword(long accountId, String oldPassword, String newPassword) {
        profileService.changePassword(accountId, oldPassword, newPassword);
    }
}

