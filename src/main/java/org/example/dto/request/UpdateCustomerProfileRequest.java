package org.example.dto.request;

import org.example.entity.enums.CustomerType;
import org.example.entity.enums.ProfileStatus;

public class UpdateCustomerProfileRequest {

    // ===== user_profiles =====
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private ProfileStatus status;

    // ===== customer_profiles =====
    private CustomerType customerType;
    private String taxCode;

    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public ProfileStatus getStatus() {return status;}
    public void setStatus(ProfileStatus status) {this.status = status;}

    public CustomerType getCustomerType() {return customerType;}
    public void setCustomerType(CustomerType customerType) {this.customerType = customerType;}

    public String getTaxCode() {return taxCode;}
    public void setTaxCode(String taxCode) {this.taxCode = taxCode;}
}
