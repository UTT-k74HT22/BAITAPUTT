package org.example.service;

import org.example.dto.response.EmployeeResponse;
import java.util.List;

public interface EmployeeService {

    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    List<EmployeeResponse> getAllEmployees();
}
