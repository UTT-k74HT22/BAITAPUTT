package org.example.repository.impl;

import org.example.dto.request.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileDetailResponse;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.entity.enums.ProfileStatus;
import org.example.repository.BaseRepository;
import org.example.repository.EmployeeProfileRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeProfileRepositoryImpl extends BaseRepository implements EmployeeProfileRepository {

    @Override
    public void insert(Connection conn, long profileId, String position, String department,
                       LocalDate hiredDate, BigDecimal baseSalary) {

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
            throw new RuntimeException("Insert employee_profile failed", e);
        }
    }

    @Override
    public List<EmployeeProfileResponse> getAllProfiles() {
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
                JOIN user_profiles up ON ep.profile_id = up.id
                JOIN accounts a ON up.account_id = a.id
                """;

        return queryForList(sql, this::mapToRow);
    }

    @Override
    public Optional<EmployeeProfileDetailResponse> getProfileDetailByCode(String code) {
        String sql = """
                SELECT
                    a.id AS accountId,
                    a.username AS accountUsername,
                    up.id AS profileId,
                    up.code AS code,
                    up.full_name AS fullName,
                    up.phone AS phone,
                    up.email AS email,
                    up.address AS address,
                    ep.position AS position,
                    ep.department AS department,
                    ep.hired_date AS hiredDate,
                    ep.base_salary AS salary,
                    up.status AS status
                FROM employee_profiles ep
                JOIN user_profiles up ON ep.profile_id = up.id
                JOIN accounts a ON up.account_id = a.id
                WHERE up.code = ?
                """;
        return queryForObject(sql, params(code), this::mapDetailResponse);
    }

    /**
     * Lấy tất cả chi tiết hồ sơ nhân viên.
     *
     * @return danh sách chi tiết hồ sơ nhân viên
     */
    @Override
    public List<EmployeeProfileDetailResponse> getAllProfileDetails() {
        String sql = """
            SELECT
                a.id AS accountId,
                a.username AS accountUsername,
                up.id AS profileId,
                up.code AS code,
                up.full_name AS fullName,
                up.phone AS phone,
                up.email AS email,
                up.address AS address,
                ep.position AS position,
                ep.department AS department,
                ep.hired_date AS hiredDate,
                ep.base_salary AS salary,
                up.status AS status
            FROM employee_profiles ep
            JOIN user_profiles up ON ep.profile_id = up.id
            JOIN accounts a ON up.account_id = a.id
            ORDER BY up.id DESC
            """;
        return queryForList(sql, this::mapDetailResponse);
    }

    @Override
    public void updateProfile(String code, UpdateProfileEmployeeRequest request) {
        String sql = """
                UPDATE employee_profiles ep
                JOIN user_profiles up ON ep.profile_id = up.id
                SET up.full_name = ?,
                    up.phone = ?,
                    ep.position = ?,
                    up.status = ?
                WHERE up.code = ?
                """;
        executeUpdate(sql, params(
                request.getFullName(),
                request.getPhone(),
                request.getPosition(),
                request.getStatus().name(),
                code
        ));
    }

    /**
     * Cập nhật chi tiết hồ sơ nhân viên theo ID hồ sơ.
     *
     * @param profileId ID hồ sơ nhân viên
     * @param req       yêu cầu cập nhật chi tiết hồ sơ nhân viên
     */
    @Override
    public void updateDetailByProfileId(Long profileId, UpdateEmployeeProfileDetailRequest req) {
        String sql = """
                    UPDATE employee_profiles
                    SET position = ?, department = ?, hired_date = ?, base_salary = ?
                    WHERE profile_id = ?
                    """;
        executeUpdate(sql, ps -> {
            ps.setString(1, req.getPosition());
            ps.setString(2, req.getDepartment());
            ps.setDate(3, req.getHiredDate() == null ? null : java.sql.Date.valueOf(req.getHiredDate()));
            ps.setBigDecimal(4, req.getSalary());
            ps.setLong(5, profileId);
        });
    }

    @Override
    public void updateStatus(String code, ProfileStatus status) {
        String sql = """
                UPDATE user_profiles
                SET status = ?
                WHERE code = ?
                """;

        executeUpdate(sql, params(
                status.name(), // UPPERCASE
                code
        ));
    }

    /* ===================== MAP RESULT ===================== */

    private EmployeeProfileResponse mapToRow(ResultSet rs) throws SQLException {
        EmployeeProfileResponse profile = new EmployeeProfileResponse();
        profile.setAccountId(rs.getLong("accountId"));
        profile.setAccountUsername(rs.getString("accountUsername"));
        profile.setProfileId(rs.getLong("profileId"));
        profile.setCode(rs.getString("code"));
        profile.setFullName(rs.getString("fullName"));
        profile.setPhone(rs.getString("phone"));
        profile.setPosition(rs.getString("position"));
        profile.setStatus(ProfileStatus.valueOf(rs.getString("status"))); // OK
        return profile;
    }

    private EmployeeProfileDetailResponse mapDetailResponse(ResultSet rs) throws SQLException {
        EmployeeProfileDetailResponse detail = new EmployeeProfileDetailResponse();
        detail.setAccountId(rs.getLong("accountId"));
        detail.setAccountUsername(rs.getString("accountUsername"));
        detail.setProfileId(rs.getLong("profileId"));
        detail.setCode(rs.getString("code"));
        detail.setFullName(rs.getString("fullName"));
        detail.setPhone(rs.getString("phone"));
        detail.setEmail(rs.getString("email"));
        detail.setAddress(rs.getString("address"));
        detail.setPosition(rs.getString("position"));
        detail.setDepartment(rs.getString("department"));
        detail.setHiredDate(
                rs.getDate("hiredDate") != null
                        ? rs.getDate("hiredDate").toLocalDate()
                        : null
        );
        detail.setSalary(rs.getBigDecimal("salary"));
        detail.setStatus(ProfileStatus.valueOf(rs.getString("status")));
        return detail;
    }
}
