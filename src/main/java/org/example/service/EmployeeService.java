package org.example.service;

import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.dto.response.EmployeeResponse;
import java.util.List;

public interface EmployeeService {

    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    List<EmployeeProfileResponse> getAllEmployees();

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     *
     * @param code      mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    void updateProfile(String code, UpdateProfileEmployeeRequest request);
}
