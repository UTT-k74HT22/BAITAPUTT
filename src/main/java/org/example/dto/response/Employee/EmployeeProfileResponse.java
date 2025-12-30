package org.example.dto.response.Employee;

import org.example.entity.enums.ProfileStatus;

public class EmployeeProfileResponse {
    private Long id;
    private Long accountId;
    private String accountUsername;
    private Long profileId;
    private String code;
    private String fullName;
    private String phone;
    private String position;

    private ProfileStatus status;

    public EmployeeProfileResponse() {
    }

    public EmployeeProfileResponse(Long accountId, String accountUsername, Long profileId, String code, String fullName, String phone, String position, ProfileStatus status) {
        this.accountId = accountId;
        this.accountUsername = accountUsername;
        this.profileId = profileId;
        this.code = code;
        this.fullName = fullName;
        this.phone = phone;
        this.position = position;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ProfileStatus getStatus() {
        return status;
    }

    public void setStatus(ProfileStatus status) {
        this.status = status;
    }
}
