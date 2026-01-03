package org.example.service;

import org.example.dto.request.UpdateCustomerProfileRequest;
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
     * **/
    void update(String code, UpdateCustomerProfileRequest request);

    /**
     *
     * @param code
     */
    void delete(String code);
}
