package org.example.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

public interface EmployeeProfileRepository {
    void insert(Connection conn, long profileId, String position, String department,
                LocalDate hiredDate, BigDecimal baseSalary);
}
