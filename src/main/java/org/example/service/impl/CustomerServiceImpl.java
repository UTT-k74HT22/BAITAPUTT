package org.example.service.impl;

import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;
import org.example.entity.enums.CustomerStatus;
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo = new CustomerRepositoryImpl();

    /**
     * @return list of active customers
     */
    public List<CustomerResponse> getAllActiveWithAccount() {
        return repo.findAllWithAccount()
                .stream()
                .filter(c -> CustomerStatus.ACTIVE.name().equals(c.getStatus()))
                .toList();
    }

    /**
     * @param customer customer to create
     */
    @Override
    public void create(Customer customer) {
        customer.setStatus(CustomerStatus.ACTIVE);
        try {
            repo.save(customer);
        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();

            if (cause instanceof java.sql.SQLIntegrityConstraintViolationException sqlEx) {
                String msg = sqlEx.getMessage();

                if (msg.contains("customers.phone")) {
                    throw new RuntimeException("Số điện thoại đã tồn tại");
                }
                if (msg.contains("customers.code")) {
                    throw new RuntimeException("Mã khách hàng đã tồn tại");
                }
            }

            throw new RuntimeException("Không thể tạo khách hàng");
        }
    }

    @Override
    public void update(Customer customer) {
        try {
            repo.update(customer);
        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();

            if (cause instanceof java.sql.SQLIntegrityConstraintViolationException sqlEx) {
                String msg = sqlEx.getMessage();

                if (msg.contains("customers.code")) {
                    throw new RuntimeException("Mã khách hàng đã tồn tại");
                }
                if (msg.contains("customers.phone")) {
                    throw new RuntimeException("Số điện thoại đã tồn tại");
                }
            }

            throw new RuntimeException("Không thể cập nhật khách hàng");
        }
    }

    @Override
    public void softDelete(int id) {
        try {
            repo.updateStatus(id, CustomerStatus.INACTIVE.name());
        } catch (RuntimeException ex) {
            throw new RuntimeException("Không thể xóa khách hàng");
        }
    }
}
