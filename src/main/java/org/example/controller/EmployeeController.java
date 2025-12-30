package org.example.controller;

import org.example.dto.request.Employee.UpdateEmployeeProfileDetailRequest;
import org.example.dto.request.Employee.UpdateProfileEmployeeRequest;
import org.example.dto.response.Employee.EmployeeProfileDetailResponse;
import org.example.dto.response.Employee.EmployeeProfileResponse;
import org.example.entity.enums.ProfileStatus;
import org.example.service.EmployeeService;
import org.example.utils.excel.employees.EmployeeDetailExcelWriter;
import org.example.utils.excel.employees.EmployeeExcelWriter;
import org.example.utils.excel.core.ExcelExporter;
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
     * Lấy chi tiết nhân viên theo mã nhân viên.
     *
     * @param code mã nhân viên
     * @return chi tiết nhân viên
     */
    public EmployeeProfileDetailResponse getEmployeeDetailByCode(String code) {
        System.out.println("[Controller] Lấy chi tiết nhân viên theo mã nhân viên");
        return employeeService.getEmployeeDetailByCode(code);
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
     * Cập nhật chi tiết hồ sơ nhân viên theo mã nhân viên.
     *
     * @param code    mã nhân viên
     * @param request yêu cầu cập nhật chi tiết hồ sơ nhân viên
     */
    public void updateProfileDetail(String code, UpdateEmployeeProfileDetailRequest request) {
        System.out.println("[Controller] Cập nhật chi tiết hồ sơ nhân viên theo mã nhân viên");
        employeeService.updateProfileDetail(code, request);
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

    /**
     * Xoá hồ sơ nhân viên (cập nhật trạng thái).
     *
     * @param code     ID hồ sơ nhân viên
     * @param status trạng thái mới
     */
    public void delete(String code, ProfileStatus status) {
        System.out.println("[Controller] Xoá hồ sơ nhân viên (cập nhật trạng thái)");
        employeeService.delete(code, status);
    }

    /**
     * Xuất danh sách nhân viên ra file Excel.
     *
     * @param parent thành phần cha để hiển thị hộp thoại
     */
    public void exportExcel(Component parent) {
        System.out.println("[Controller] Xuất danh sách nhân viên ra file Excel");
        ExcelExporter.exportWithDialog(
                employeeService.getAllEmployees(),
                new EmployeeExcelWriter(),
                parent
        );
    }

    /**
     * Xuất chi tiết nhân viên ra file Excel.
     *
     * @param code   mã nhân viên
     * @param parent thành phần cha để hiển thị hộp thoại
     */
    public void exportEmployeeDetailExcel(String code, Component parent) {
        System.out.println("[Controller] Xuất Excel chi tiết nhân viên: " + code);

        ExcelExporter.exportWithDialog(
                List.of(employeeService.getEmployeeDetailByCode(code)),
                new EmployeeDetailExcelWriter(),
                parent
        );
    }

    public void exportExcelDetailList(Component parent) {
        System.out.println("[Controller] Xuất danh sách nhân viên (chi tiết) ra Excel");
        ExcelExporter.exportWithDialog(
                employeeService.getAllProfileDetails(),
                new EmployeeDetailExcelWriter(),
                parent
        );
    }

}
