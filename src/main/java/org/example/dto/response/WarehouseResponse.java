package org.example.dto.response;

public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private String managerName;
    private boolean active;

    public WarehouseResponse() {}

    public Long getId() {return id;}
    public String getCode() {return code;}
    public String getName() {return name;}
    public String getAddress() {return address;}
    public String getPhone() {return phone;}
    public String getManagerName() {return managerName;}
    public boolean isActive() {return active;}
    public void setActive(boolean active) { this.active = active; }
    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

}
