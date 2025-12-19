package org.example.repository;

import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileDetailResponse;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.entity.enums.ProfileStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeProfileRepository {
    void insert(Connection conn, long profileId, String position, String department,
                LocalDate hiredDate, BigDecimal baseSalary);

    /**
     * Lấy tất cả hồ sơ nhân viên.
     * @return danh sách hồ sơ nhân viên
     */
    List<EmployeeProfileResponse> getAllProfiles();

    /**
     * Lấy chi tiết hồ sơ nhân viên theo mã nhân viên.
     *
     * @param code mã nhân viên
     * @return chi tiết hồ sơ nhân viên
     */
    Optional<EmployeeProfileDetailResponse> getProfileDetailByCode(String code);

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     * @param code mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    void updateProfile(String code, UpdateProfileEmployeeRequest request);

    /**
     * Cập nhật trạng thái hồ sơ nhân viên.
     * @param code ID hồ sơ nhân viên
     * @param status trạng thái mới
     */
    void updateStatus(String code, ProfileStatus status);
}

