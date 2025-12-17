package org.example.service;

import org.example.dto.RegisterRequest;
import org.example.dto.request.CreateCustomerRequest;
import org.example.dto.request.CreateEmployeeRequest;

public interface UserCreationService {
    long registerCustomer(RegisterRequest request);

    long registerEmployee(RegisterRequest request);

    long createCustomer(CreateCustomerRequest request);

    long createEmployee(CreateEmployeeRequest request);
}
