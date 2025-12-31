package org.example.service.impl;

import org.example.dto.request.UpdateUserProfileRequest;
import org.example.dto.response.ProfileResponse;
import org.example.entity.enums.Role;
import org.example.repository.CustomerProfileRepository;
import org.example.repository.EmployeeProfileRepository;
import org.example.repository.UserProfileRepository;
import org.example.repository.AccountRepository;
import org.example.service.ProfileService;

import org.mindrot.jbcrypt.BCrypt;

public class ProfileServiceImpl implements ProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserProfileRepository userProfileRepository;
    private final AccountRepository accountRepository;

    public ProfileServiceImpl(
            CustomerProfileRepository customerProfileRepository,
            EmployeeProfileRepository employeeProfileRepository,
            UserProfileRepository userProfileRepository,
            AccountRepository accountRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.userProfileRepository = userProfileRepository;
        this.accountRepository = accountRepository;;
    }

    @Override
    public ProfileResponse loadProfile(String username, Role role) {
        var userProfile = userProfileRepository.findProfileByUsername(username)
                .orElseThrow(() -> new RuntimeException("UserProfile Not Found"));

        ProfileResponse p = new ProfileResponse();
        p.setCode(userProfile.getCode());
        p.setUsername(userProfile.getUsername());
        p.setFullName(userProfile.getFullName());
        p.setPhone(userProfile.getPhone());
        p.setEmail(userProfile.getEmail());
        p.setAddress(userProfile.getAddress());
        p.setRole(role);

        if (role == Role.ADMIN || role == Role.EMPLOYEE) {
            employeeProfileRepository.getProfileDetailByCode(userProfile.getCode())
                    .ifPresent(ep -> {
                        p.setPosition(ep.getPosition());
                        p.setDepartment(ep.getDepartment());
                    });
        }

        if (role == Role.CUSTOMER) {
            customerProfileRepository.findByCode(userProfile.getCode())
                    .ifPresent(cp ->
                        p.setCustomerType(cp.getCustomerType().name())
                    );
        }
        return p;
    }

    @Override
    public void updateMyProfile(long accountId, UpdateUserProfileRequest req) {
        userProfileRepository.updateMyProfileByAccountId(
                accountId,
                req.getFullName(),
                req.getPhone(),
                req.getEmail(),
                req.getAddress()
        );
    }

    @Override
    public void changePassword(long accountId, String oldPassword, String newPassword) {
        var account = accountRepository.findById(accountId);
        if (account == null) {
            throw new RuntimeException("User not found");
        }

        if (!BCrypt.checkpw(oldPassword, account.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        accountRepository.updatePassword(accountId, hashed);
    }
}
