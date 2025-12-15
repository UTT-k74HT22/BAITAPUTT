package org.example.repository.impl;

import org.example.dto.response.EmployeeResponse;
import org.example.repository.BaseRepository;
import org.example.repository.EmployeeRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeRepositoryImpl extends BaseRepository implements EmployeeRepository {
    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    @Override
    public List<EmployeeResponse> findAll() {
        String sql = """
        SELECT
            e.id,
            e.code,
            e.full_name,
            e.position,
            e.department,
            e.phone,
            e.email,
            e.is_active,
            e.hired_date,
            a.username AS account_username
        FROM employees e
        LEFT JOIN accounts a ON e.account_id = a.id
        """;
        return queryForList(sql, this::mapRowToDto);
    }


    private EmployeeResponse mapRowToDto(ResultSet rs) throws SQLException {
        EmployeeResponse r = new EmployeeResponse();
        r.setId(rs.getLong("id"));
        r.setCode(rs.getString("code"));
        r.setFullName(rs.getString("full_name"));
        r.setPosition(rs.getString("position"));
        r.setDepartment(rs.getString("department"));
        r.setPhone(rs.getString("phone"));
        r.setEmail(rs.getString("email"));
        r.setActive(rs.getBoolean("is_active"));
        if (rs.getDate("hired_date") != null) {
            r.setHiredDate(rs.getDate("hired_date").toLocalDate());
        }
        r.setAccountUsername(rs.getString("account_username"));
        return r;
    }
}
