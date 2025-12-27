package org.example.service.impl;

import org.example.service.CustomerService;
import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;
import org.example.repository.CustomerProfileRepository;
import org.example.repository.impl.CustomerProfileRepositoryImpl;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerProfileRepository customerProfileRepository;

    public CustomerServiceImpl() {
        this.customerProfileRepository = new CustomerProfileRepositoryImpl();
    }

    @Override
    public List<CustomerProfileResponse> getAll() {
        System.out.println("[Service] Lấy danh sách khách hàng");
        return customerProfileRepository.findAll();
    }

    @Override
    public CustomerProfileResponse getByCode(String code) {
        System.out.println("[Service] Lấy khách hàng theo code: " + code);
        return customerProfileRepository.findByCode(code)
                .orElseThrow(() ->
        new IllegalArgumentException("Không tìm thấy khách hàng với mã: " + code)
                );
    }

    @Override
    public List<CustomerProfileResponse> search(String keyword, CustomerType type) {
        System.out.println("[Service] Tìm kiếm khách hàng");
        return customerProfileRepository.search(keyword, type);
    }

    @Override
    public void update(String code,
                       String fullName,
                       String phone,
                       CustomerType customerType,
                       String taxCode) {
        System.out.println("[Service] Cập nhật khách hàng: " + code);

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code không được để trống");
        }

        customerProfileRepository.update(code, fullName, phone, customerType, taxCode);
    }

    @Override
    public void delete(String code) {
        System.out.println("[Service] Xoá khách hàng: " + code);

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code không được để trống");
        }
        customerProfileRepository.delete(code);
    }
}
