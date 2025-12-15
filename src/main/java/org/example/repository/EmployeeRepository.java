package org.example.repository;

import org.example.dto.response.EmployeeResponse;
import java.util.List;

public interface EmployeeRepository {

    /**
     * Lấy tất cả nhân viên.
     * @return danh sách nhân viên
     */
    List<EmployeeResponse> findAll();

}
