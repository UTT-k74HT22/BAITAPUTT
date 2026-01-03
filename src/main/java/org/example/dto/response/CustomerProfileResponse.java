package org.example.dto.response;

import org.example.entity.enums.CustomerType;
import org.example.entity.enums.ProfileStatus;

public class CustomerProfileResponse {
    private Long profileId;
    private Long id;
    private String code;
    private String fullName;
    private String phone;
    private String email;
    private String Address;
    private CustomerType customerType;
    private ProfileStatus profileStatus;
    private String taxCode;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getAddress() {return Address;}

    public void setAddress(String address) {this.Address = address;}

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public ProfileStatus getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(ProfileStatus profileStatus) {
        this.profileStatus = profileStatus;
    }

    public String getTaxCode() {return taxCode;}

    public void setTaxCode(String taxCode) {this.taxCode = taxCode;}
}


