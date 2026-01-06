package org.example.service.impl;

import org.example.dto.request.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileDetailResponse;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.entity.enums.ProfileStatus;
import org.example.repository.EmployeeProfileRepository;
import org.example.repository.UserProfileRepository;
import org.example.repository.impl.EmployeeProfileRepositoryImpl;
import org.example.repository.impl.UserProfileRepositoryImpl;
import org.example.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserProfileRepository userProfileRepository;

    public EmployeeServiceImpl() {
        this.employeeProfileRepository = new EmployeeProfileRepositoryImpl();
        this.userProfileRepository = new UserProfileRepositoryImpl();
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
     * Lấy tất cả chi tiết hồ sơ nhân viên.
     *
     * @return danh sách chi tiết hồ sơ nhân viên
     */
    @Override
    public List<EmployeeProfileDetailResponse> getAllProfileDetails() {
        System.out.println("[Service] Lấy tất cả chi tiết hồ sơ nhân viên");
        return employeeProfileRepository.getAllProfileDetails();
    }


    /**
     * Lấy chi tiết nhân viên theo mã nhân viên.
     *
     * @param code mã nhân viên
     * @return chi tiết nhân viên
     */
    @Override
    public EmployeeProfileDetailResponse getEmployeeDetailByCode(String code) {
        System.out.println("[Service] Lấy chi tiết nhân viên theo mã nhân viên");
        return employeeProfileRepository.getProfileDetailByCode(code)
                .orElseThrow(() ->
                        new IllegalArgumentException("Không tìm thấy nhân viên với mã: " + code)
                );
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

    /**
     * Cập nhật chi tiết hồ sơ nhân viên theo mã nhân viên.
     *
     * @param code    mã nhân viên
     * @param request yêu cầu cập nhật chi tiết hồ sơ nhân viên
     */
    @Override
    public void updateProfileDetail(String code, UpdateEmployeeProfileDetailRequest request) {
        System.out.println("[Service] Cập nhật chi tiết hồ sơ nhân viên theo mã nhân viên");
        Long profileId = userProfileRepository.findIdByCode(code)
                .orElseThrow(() ->
                        new IllegalArgumentException("Không tìm thấy hồ sơ với mã: " + code)
                );
        userProfileRepository.updateDetailByCode(code, request);
        employeeProfileRepository.updateDetailByProfileId(profileId, request);
    }

    /**
     * Xoá hồ sơ nhân viên (cập nhật trạng thái).
     *
     * @param code     ID hồ sơ nhân viên
     * @param status trạng thái mới
     */
    @Override
    public void delete(String code, ProfileStatus status) {
        System.out.println("[Service] Xoá hồ sơ nhân viên (cập nhật trạng thái)");
        ProfileStatus newStatus = ProfileStatus.INACTIVE;
        employeeProfileRepository.updateStatus(code, newStatus);
    }
}
