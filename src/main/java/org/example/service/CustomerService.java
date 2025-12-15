package org.example.service;

import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;

import java.util.List;

public interface CustomerService {

    /**
     * Lấy danh sách khách hàng đang hoạt động (ACTIVE)
     * kèm thông tin tài khoản (account_id, username)
     */
    List<CustomerResponse> getAllActiveWithAccount();

    /**
     * Tạo mới một khách hàng
     * @param customer thông tin khách hàng cần tạo
     */
    void create(Customer customer);

    /**
     * Cập nhật thông tin khách hàng
     * @param customer thông tin khách hàng cần cập nhật
     */
    void update(Customer customer);

    /**
     * Xóa mềm khách hàng (cập nhật trạng thái INACTIVE)
     * @param id id khách hàng
     */
    void softDelete(int id);
}
