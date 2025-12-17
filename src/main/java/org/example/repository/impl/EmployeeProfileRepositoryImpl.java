package org.example.repository.impl;

import org.example.dto.response.EmployeeResponse;
import org.example.repository.BaseRepository;
import org.example.repository.EmployeeProfileRepository;
import org.example.repository.EmployeeRepository;

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
                INSERT INTO employee_profile (profile_id, position, department, hired_date, base_salary)
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
}
