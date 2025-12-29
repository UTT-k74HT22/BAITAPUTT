package org.example.service.impl;

import org.example.dto.response.ProfileResponse;
import org.example.entity.enums.Role;
import org.example.repository.CustomerProfileRepository;
import org.example.repository.EmployeeProfileRepository;
import org.example.repository.UserProfileRepository;
import org.example.service.ProfileService;

public class ProfileServiceImpl implements ProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserProfileRepository userProfileRepository;

    public ProfileServiceImpl(
            CustomerProfileRepository customerProfileRepository,
            EmployeeProfileRepository employeeProfileRepository,
            UserProfileRepository userProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.userProfileRepository = userProfileRepository;
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
}
