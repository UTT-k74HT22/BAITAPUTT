package org.example.repository;

import org.example.entity.Payment;

import java.util.List;

public interface PaymentRepository {
    List<Payment> findAll();

    Payment findById(Long id);

    long insert(Payment p);

    void update(Payment p);

    void delete(Long id);

    boolean existsByPaymentNumber(String paymentNumber);
}
