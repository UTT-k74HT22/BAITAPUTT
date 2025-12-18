package org.example.controller;

import org.example.dto.request.UpdateProfileEmployeeRequest;
import org.example.dto.response.EmployeeProfileResponse;
import org.example.dto.response.EmployeeResponse;
import org.example.service.EmployeeService;
import org.example.utils.excel.EmployeeExcelWriter;
import org.example.utils.excel.ExcelExporter;
import java.awt.Component;
import java.util.List;

/**
 * Controller cho các thao tác liên quan đến nhân viên.
 */
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Lấy tất cả nhân viên.
     *
     * @return danh sách nhân viên
     */
    public List<EmployeeProfileResponse> getAllEmployees() {
        System.out.println("[Controller] Lấy tất cả nhân viên");
        return employeeService.getAllEmployees();
    }

    /**
     * Cập nhật thông tin hồ sơ nhân viên.
     *
     * @param id      mã nhân viên
     * @param request yêu cầu cập nhật hồ sơ nhân viên
     */
    public void updateProfile(String id, UpdateProfileEmployeeRequest request) {
        System.out.println("[Controller] Cập nhật thông tin hồ sơ nhân viên");
        employeeService.updateProfile(id, request);
    }

    /**
     * Tìm kiếm nhân viên theo mã.
     *
     * @param code mã nhân viên
     * @return thông tin nhân viên hoặc null nếu không tìm thấy
     */
    public EmployeeProfileResponse findByCode(String code) {
        System.out.println("[Controller] Tìm kiếm nhân viên theo mã: " + code);
        return employeeService.getAllEmployees()
                .stream()
                .filter(emp -> emp.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

//    /**
//     * Xuất danh sách nhân viên ra file Excel.
//     *
//     * @param parent thành phần cha để hiển thị hộp thoại
//     */
//    public void exportExcel(Component parent) {
//        System.out.println("[Controller] Xuất danh sách nhân viên ra file Excel");
//        ExcelExporter.exportWithDialog(
//                employeeService.getAllEmployees(),
//                new EmployeeExcelWriter(),
//                parent
//        );
//    }
}
