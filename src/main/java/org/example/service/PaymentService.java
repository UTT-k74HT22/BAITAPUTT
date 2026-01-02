package org.example.service;

import org.example.entity.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAll();

    Payment getById(Long id);

    long create(Payment payment);

    void update(Payment payment);

    void cancel(Long id);
}
