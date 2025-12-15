package org.example.service.impl;

import org.example.dto.response.CustomerResponse;
import org.example.entity.Customer;
import org.example.entity.enums.CustomerStatus;
import org.example.repository.CustomerRepository;
import org.example.repository.impl.CustomerRepositoryImpl;
import org.example.service.CustomerService;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo = new CustomerRepositoryImpl();

    @Override
    public List<CustomerResponse> getAllActiveWithAccount() {
        return repo.findAllWithAccount()
                .stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .toList();
    }


    @Override
    public void create(Customer customer) {
        customer.setStatus(CustomerStatus.ACTIVE);
        try {
            repo.save(customer);
        } catch (RuntimeException ex) {
            handleDuplicate(ex);
        }
    }

    @Override
    public void update(Customer customer) {
        // Form hiện tại không cho chỉnh trạng thái, nên nếu status null thì mặc định giữ ACTIVE
        if (customer.getStatus() == null) {
            customer.setStatus(CustomerStatus.ACTIVE);
        }
        try {
            repo.update(customer);
        } catch (RuntimeException ex) {
            handleDuplicate(ex);
        }
    }

    @Override
    public void softDelete(int id) {
        repo.updateStatus(id, CustomerStatus.INACTIVE.name());
    }

    private void handleDuplicate(RuntimeException ex) {
        Throwable cause = ex.getCause();

        // Nếu là lỗi vi phạm ràng buộc duy nhất (duplicate key) thì map sang thông báo rõ ràng
        if (cause instanceof SQLIntegrityConstraintViolationException sqlEx) {
            String msg = sqlEx.getMessage() == null ? "" : sqlEx.getMessage();

            if (msg.contains("customers.code")) {
                throw new RuntimeException("Mã khách hàng đã tồn tại");
            }
            if (msg.contains("customers.phone")) {
                throw new RuntimeException("Số điện thoại đã tồn tại");
            }

            // Trường hợp khác nhưng vẫn là duplicate key
            throw new RuntimeException("Dữ liệu đã tồn tại, vui lòng kiểm tra lại");
        }

        // Không phải lỗi duplicate -> ném lại exception gốc để UI hiển thị đúng message
        throw ex;
    }
}
