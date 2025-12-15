package org.example.controller;

import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;
import org.example.service.CustomerService;
import org.example.service.impl.CustomerServiceImpl;

import java.util.List;

public class CustomerController {

    private final CustomerService service;

    public CustomerController() {
        this.service = new CustomerServiceImpl();
    }

    /**
     * Lấy danh sách khách hàng ACTIVE
     * kèm account_id & username
     */
    public List<CustomerResponse> getAllActiveCustomers() {
        return service.getAllActiveWithAccount();
    }

    /**
     * Tạo mới khách hàng
     */
    public void create(Customer customer) {
        service.create(customer);
    }

    /**
     * Cập nhật khách hàng
     */
    public void update(Customer customer) {
        service.update(customer);
    }

    /**
     * Xóa mềm khách hàng
     */
    public void softDelete(int id) {
        service.softDelete(id);
    }
}
