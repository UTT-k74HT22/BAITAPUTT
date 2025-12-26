package org.example.dto.request;

public class CreateManufacturerRequest {

    private String code;
    private String name;
    private String country;
    private String address;
    private String phone;
    private String email;
    private String contactPerson;

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getContactPerson() { return contactPerson; }

    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
}
