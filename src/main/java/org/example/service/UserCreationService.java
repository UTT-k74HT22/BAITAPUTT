package org.example.service;

import org.example.dto.request.auth.RegisterRequest;
import org.example.dto.request.CreateAccountRequest;


public interface UserCreationService {
    /**
     * Registers a new customer based on the provided request details.
     *
     * @param request the details required to register a new customer
     * @return the ID of the newly registered customer
     */
    long registerCustomer(RegisterRequest request);

    /**
     * Creates a new account based on the provided request details.
     *
     * @param request the details required to create a new account
     * @return the ID of the newly created account
     */
    long createAccount(CreateAccountRequest request);
}
