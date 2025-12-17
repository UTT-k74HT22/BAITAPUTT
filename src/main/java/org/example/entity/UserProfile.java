package org.example.entity;

import org.example.entity.enums.ProfileStatus;

public class UserProfile extends BaseEntity {
    private Long accountId;
    private String code;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private ProfileStatus status;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ProfileStatus getStatus() {
        return status;
    }

    public void setStatus(ProfileStatus status) {
        this.status = status;
    }
}
