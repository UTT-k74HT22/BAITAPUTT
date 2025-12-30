package org.example.repository;

import org.example.dto.request.Employee.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.Employee.UpdateProfileEmployeeRequest;
import org.example.dto.response.Employee.EmployeeProfileDetailResponse;
import org.example.dto.response.Employee.EmployeeProfileResponse;
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
     * Lấy tất cả chi tiết hồ sơ nhân viên.
     * @return danh sách chi tiết hồ sơ nhân viên
     */
    List<EmployeeProfileDetailResponse> getAllProfileDetails();

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     * @param code mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    void updateProfile(String code, UpdateProfileEmployeeRequest request);

    /**
     * Cập nhật chi tiết hồ sơ nhân viên theo ID hồ sơ.
     *
     * @param profileId ID hồ sơ nhân viên
     * @param req yêu cầu cập nhật chi tiết hồ sơ nhân viên
     */
    void updateDetailByProfileId(Long profileId, UpdateEmployeeProfileDetailRequest req);

    /**
     * Cập nhật trạng thái hồ sơ nhân viên.
     * @param code ID hồ sơ nhân viên
     * @param status trạng thái mới
     */
    void updateStatus(String code, ProfileStatus status);
}

