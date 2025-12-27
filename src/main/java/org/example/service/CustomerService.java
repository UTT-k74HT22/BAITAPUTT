package org.example.service;

import org.example.dto.response.CustomerProfileResponse;
import org.example.entity.enums.CustomerType;

import java.util.List;

public interface CustomerService {
    /**
     *
     * @return
     */
    List<CustomerProfileResponse> getAll();

    /**
     *
     * @param code
     * @return
     */
    CustomerProfileResponse getByCode(String code);

    /**
     *
     * @param keyword
     * @param type
     * @return
     */
    List<CustomerProfileResponse> search(String keyword, CustomerType type);

    /**
     *
     * @param code
     * @param fullName
     * @param phone
     * @param customerType
     * @param taxCode
     */
    void update(String code,
                String fullName,
                String phone,
                CustomerType customerType,
                String taxCode);

    /**
     *
     * @param code
     */
    void delete(String code);
}
