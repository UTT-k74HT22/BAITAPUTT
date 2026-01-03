package org.example.service.impl;

import org.example.dto.request.UpdateCustomerProfileRequest;
import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;
import org.example.repository.CustomerProfileRepository;
import org.example.repository.UserProfileRepository;
import org.example.repository.impl.CustomerProfileRepositoryImpl;
import org.example.repository.impl.UserProfileRepositoryImpl;
import org.example.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserProfileRepository userProfileRepository;

    public CustomerServiceImpl() {
        this.customerProfileRepository = new CustomerProfileRepositoryImpl();
        this.userProfileRepository = new UserProfileRepositoryImpl();
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
    public void update(String code, UpdateCustomerProfileRequest request) {
        System.out.println("[Service] Cập nhật khách hàng: " + code);

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code không được để trống");
        }
        System.out.println("[Service] Cập nhật full profile cho: " + code);

        userProfileRepository.updateUserProfileForCustomer(code, request);

        customerProfileRepository.update(code, request.getCustomerType(), request.getTaxCode());

        System.out.println("[Service] Cập nhật khách hàng thành công: " + code);
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
