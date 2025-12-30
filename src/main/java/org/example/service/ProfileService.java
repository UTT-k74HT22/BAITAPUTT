package org.example.service;

import org.example.dto.request.UpdateUserProfileRequest;
import org.example.dto.response.ProfileResponse;
import org.example.entity.enums.Role;

public interface ProfileService {

    /**
     *
     * @param username
     * @param role
     * @return
     */
    ProfileResponse loadProfile(String username, Role role);

    /**
     *
     * @param accountId
     * @param req
     */
    void updateMyProfile(long accountId, UpdateUserProfileRequest req);
}
