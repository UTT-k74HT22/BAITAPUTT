package org.example.repository;

import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;

import java.util.List;

public interface CustomerRepository {

    /**
     * Lấy danh sách khách hàng kèm thông tin tài khoản (JOIN customers - accounts)
     */
    List<CustomerResponse> findAllWithAccount();

    /**
     * Tìm khách hàng theo ID
     */
    Customer findById(int id);

    /**
     * Thêm mới khách hàng
     */
    void save(Customer customer);

    /**
     * Cập nhật thông tin khách hàng
     */
    void update(Customer customer);

    /**
     * Cập nhật trạng thái khách hàng (dùng cho xóa mềm)
     */
    void updateStatus(int id, String status);
}
