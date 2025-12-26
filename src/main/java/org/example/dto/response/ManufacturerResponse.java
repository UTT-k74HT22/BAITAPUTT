package org.example.dto.response;

public class ManufacturerResponse {
    private Long id;
    private String code;
    private String name;
    private String country;
    private String phone;
    private String email;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getCode() {return code;}

    public void setCode(String code) {this.code = code;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getCountry() {return country;}

    public void setCountry(String country) {this.country = country;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}
}
