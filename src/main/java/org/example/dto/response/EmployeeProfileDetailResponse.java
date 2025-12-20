package org.example.dto.response;

import org.example.entity.enums.ProfileStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeProfileDetailResponse extends EmployeeProfileResponse {
    private String email;
    private String address;
    private String department;
    private LocalDate hiredDate;
    private BigDecimal salary;

    public EmployeeProfileDetailResponse(
            Long accountId,
            String accountUsername,
            Long profileId,
            String code,
            String fullName,
            String phone,
            String email,
            String address,
            String position,
            String department,
            LocalDate hiredDate,
            BigDecimal salary,
            ProfileStatus status
    ) {
        super(accountId, accountUsername, profileId, code, fullName, phone, position, status);
        this.email = email;
        this.address = address;
        this.department = department;
        this.hiredDate = hiredDate;
        this.salary = salary;
    }

    public EmployeeProfileDetailResponse() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
