package org.example.repository;

import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileResponse;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeProfileRepository {
    void insert(Connection conn, long profileId, String position, String department,
                LocalDate hiredDate, BigDecimal baseSalary);

    /**
     * Lấy tất cả hồ sơ nhân viên.
     * @return danh sách hồ sơ nhân viên
     */
    List<EmployeeProfileResponse> getAllProfiles();

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     * @param code mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    void updateProfile(String code, UpdateProfileEmployeeRequest request);
}

