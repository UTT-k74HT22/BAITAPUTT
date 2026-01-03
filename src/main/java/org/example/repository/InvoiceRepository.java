package org.example.repository;

import org.example.entity.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    long insert(Invoice invoice);

    Optional<Invoice> findById(Integer id);

    Optional<Invoice> findByOrderId(Integer orderId);

    boolean existsByOrderId(Integer orderId);

    List<Invoice> findAllByOrderId(Integer orderId);

    List<Invoice> findAll();

    void updatePayment(Integer invoiceId, BigDecimal paidAmount, String status);
}
