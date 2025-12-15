package org.example.controller;

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
    public List<EmployeeResponse> getAllEmployees() {
        System.out.println("[Controller] Lấy tất cả nhân viên");
        return employeeService.getAllEmployees();
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
}
