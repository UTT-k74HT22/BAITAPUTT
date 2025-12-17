package org.example.service.impl;

import org.example.dto.response.EmployeeResponse;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.EmployeeProfileRepositoryImpl;
import org.example.service.EmployeeService;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return List.of();
    }
}
