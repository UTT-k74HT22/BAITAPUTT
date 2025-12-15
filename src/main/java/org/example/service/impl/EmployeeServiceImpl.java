package org.example.service.impl;

import org.example.dto.response.EmployeeResponse;
import org.example.repository.EmployeeRepository;
import org.example.repository.impl.EmployeeRepositoryImpl;
import org.example.service.EmployeeService;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl() {
        this.employeeRepository = new EmployeeRepositoryImpl();
    }

    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        System.out.println("[Service] Lấy tất cả nhân viên");
        return employeeRepository.findAll() != null ? employeeRepository.findAll() : List.of();
    }
}
