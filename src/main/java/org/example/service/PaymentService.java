package org.example.service;

import org.example.entity.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAll();

    Payment getById(Long id);

    long createByInvoice(long invoiceId, String method);

    void update(Payment payment);

    void cancel(Long id);
}
