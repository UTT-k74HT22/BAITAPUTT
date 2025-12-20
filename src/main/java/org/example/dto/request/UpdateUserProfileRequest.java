package org.example.dto.request;

import org.example.entity.enums.ProfileStatus;

public class UpdateUserProfileRequest {
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private ProfileStatus status;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public ProfileStatus getStatus() { return status; }
    public void setStatus(ProfileStatus status) { this.status = status; }
}
