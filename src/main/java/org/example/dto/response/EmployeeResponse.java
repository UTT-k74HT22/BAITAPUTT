package org.example.dto.response;

import org.example.entity.enums.ProfileStatus;
import java.time.LocalDate;

public class EmployeeResponse {
    private Long id;
    private String code;
    private String fullName;
    private String position;
    private String department;
    private String phone;
    private String email;
    private ProfileStatus active;
    private LocalDate hiredDate;

    private String accountUsername;

    public EmployeeResponse(Long id, String code, String fullName, String position, String department, String phone, String email, ProfileStatus active, LocalDate hiredDate, String accountUsername) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.position = position;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.active = active;
        this.hiredDate = hiredDate;
        this.accountUsername = accountUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProfileStatus isActive() {
        return active;
    }

    public void setActive(ProfileStatus active) {
        this.active = active;
    }

    public LocalDate getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }
}
