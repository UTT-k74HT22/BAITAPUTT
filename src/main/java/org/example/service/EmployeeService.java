package org.example.service;

import org.example.dto.request.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileDetailResponse;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.dto.response.EmployeeResponse;
import org.example.entity.enums.ProfileStatus;

import java.awt.*;
import java.util.List;

public interface EmployeeService {

    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    List<EmployeeProfileResponse> getAllEmployees();

    /**
     * Lấy tất cả chi tiết hồ sơ nhân viên.
     *
     * @return danh sách chi tiết hồ sơ nhân viên
     */
    List<EmployeeProfileDetailResponse> getAllProfileDetails();

    /**
     * Lấy chi tiết nhân viên theo mã nhân viên.
     *
     * @param code mã nhân viên
     * @return chi tiết nhân viên
     */
    EmployeeProfileDetailResponse getEmployeeDetailByCode(String code);

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     *
     * @param code      mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    void updateProfile(String code, UpdateProfileEmployeeRequest request);

    /**
     * Cập nhật chi tiết hồ sơ nhân viên theo mã nhân viên.
     *
     * @param code    mã nhân viên
     * @param request yêu cầu cập nhật chi tiết hồ sơ nhân viên
     */
    void updateProfileDetail(String code, UpdateEmployeeProfileDetailRequest request);

    /**
     * Xoá hồ sơ nhân viên (cập nhật trạng thái).
     *
     * @param code     ID hồ sơ nhân viên
     * @param status trạng thái mới
     */
    void delete(String code, ProfileStatus status);
}
