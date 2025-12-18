package org.example.repository.impl;

import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.entity.EmployeeProfile;
import org.example.entity.enums.ProfileStatus;
import org.example.repository.BaseRepository;
import org.example.repository.EmployeeProfileRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmployeeProfileRepositoryImpl extends BaseRepository implements EmployeeProfileRepository {
    @Override
    public void insert(Connection conn, long profileId, String position, String department, LocalDate hiredDate, BigDecimal baseSalary) {
        String sql = """
                INSERT INTO employee_profiles (profile_id, position, department, hired_date, base_salary)
                VALUES (?, ?, ?, ?, ?)
                """;
        try {
            executeUpdate(conn, sql, ps -> {
                ps.setLong(1, profileId);
                ps.setString(2, position);
                ps.setString(3, department);
                ps.setDate(4, hiredDate == null ? null : java.sql.Date.valueOf(hiredDate));
                ps.setBigDecimal(5, baseSalary == null ? BigDecimal.ZERO : baseSalary);
            });
        } catch (Exception e) {
            throw new RuntimeException("Insert employee_profile failed: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tất cả hồ sơ nhân viên.
     *
     * @return danh sách hồ sơ nhân viên
     */
    @Override
    public List<EmployeeProfileResponse> getAllProfiles() {
        System.out.println("[Repository] Lấy tất cả hồ sơ nhân viên");
        String sql = """
                SELECT
                    a.id AS accountId,
                    a.username AS accountUsername,
                    up.id AS profileId,
                    up.code AS code,
                    up.full_name AS fullName,
                    up.phone AS phone,
                    ep.position AS position,
                    up.status AS status
                FROM employee_profiles ep
                LEFT JOIN user_profiles up ON ep.profile_id = up.id
                LEFT JOIN accounts a ON up.account_id = a.id
                """;
        return queryForList(sql, this::mapToRow);
    }

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     *
     * @param code mã nhân viên
     * @param request    yêu cầu cập nhật hồ sơ nhân viên
     */
    @Override
    public void updateProfile(String code, UpdateProfileEmployeeRequest request) {
        String sql = """
                UPDATE employee_profiles
                SET full_name = ?,
                    phone = ?,
                    position = ?,
                    status = ?
                WHERE code = ?
                """;
        executeUpdate(sql, params(code, request.getFullName(),
                request.getPhone(), request.getPosition(), request.getStatus(), code));
    }

    private EmployeeProfileResponse mapToRow(ResultSet rs) throws SQLException {
        EmployeeProfileResponse profile = new EmployeeProfileResponse();
        profile.setAccountId(rs.getLong("accountId"));
        profile.setAccountUsername(rs.getString("accountUsername"));
        profile.setProfileId(rs.getString("profileId"));
        profile.setCode(rs.getString("code"));
        profile.setFullName(rs.getString("fullName"));
        profile.setPhone(rs.getString("phone"));
        profile.setPosition(rs.getString("position"));
        profile.setStatus(ProfileStatus.valueOf(rs.getString("status")));
        return profile;
    }
}
