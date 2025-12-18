package org.example.service.impl;

import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.repository.EmployeeProfileRepository;
import org.example.repository.impl.EmployeeProfileRepositoryImpl;
import org.example.service.EmployeeService;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeProfileRepository employeeProfileRepository;

    public EmployeeServiceImpl() {
        this.employeeProfileRepository = new EmployeeProfileRepositoryImpl();
    }

    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    @Override
    public List<EmployeeProfileResponse> getAllEmployees() {
        System.out.println("[Service] Lấy tất cả nhân viên");
        return employeeProfileRepository.getAllProfiles()
                .stream()
                .map(profile -> new EmployeeProfileResponse(
                    profile.getAccountId(),
                    profile.getAccountUsername(),
                    profile.getProfileId(),
                    profile.getCode(),
                    profile.getFullName(),
                    profile.getPhone(),
                    profile.getPosition(),
                    profile.getStatus()
                ))
                .toList();
    }

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     *
     * @param id      mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    @Override
    public void updateProfile(String id, UpdateProfileEmployeeRequest request) {
        System.out.println("[Service] Cập nhật thông tin hồ sơ nhân viên");

        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu cập nhật không được null");
        }
        employeeProfileRepository.updateProfile(id, request);
    }
}
