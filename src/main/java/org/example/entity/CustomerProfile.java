package org.example.entity;

import org.example.entity.enums.CustomerType;

public class CustomerProfile extends BaseEntity {
    private Long profileId;
    private CustomerType customerType;
    private String taxCode;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }
}
