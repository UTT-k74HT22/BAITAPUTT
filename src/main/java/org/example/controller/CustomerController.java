package org.example.controller;

import org.example.service.CustomerService;
import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;
import org.example.service.impl.CustomerServiceImpl;

import java.util.List;

public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    public List<CustomerProfileResponse> getCustomers() {
        System.out.println("[Controller] Lấy tất cả khách hàng");
        return customerService.getAll();
    }

    public CustomerProfileResponse getCustomerByCode(String code) {
        System.out.println("[Controller] Lấy khách hàng theo code: " + code);
        return customerService.getByCode(code);
    }

    public List<CustomerProfileResponse> searchCustomers(String keyword, CustomerType type) {
        System.out.println("[Controller] Tìm kiếm khách hàng");
        return customerService.search(keyword, type);
    }

    public void updateCustomer(String code,
                               String fullName,
                               String phone,
                               CustomerType customerType,
                               String taxCode) {

        System.out.println("[Controller] Cập nhật khách hàng: " + code);
        customerService.update(
                code,
                fullName,
                phone,
                customerType,
                taxCode
        );
    }

    public void deleteCustomer(String code) {
        System.out.println("[Controller] Xoá khách hàng: " + code);
        customerService.delete(code);
    }
}
