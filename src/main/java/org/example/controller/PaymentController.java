package org.example.controller;

import org.example.entity.Payment;
import org.example.service.PaymentService;

import java.util.List;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ===================== READ =====================
    public List<Payment> getAllPayments() {
        return paymentService.getAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentService.getById(id);
    }

    // ===================== CREATE =====================
    public long createPayment(Payment payment) {
        try {
            return paymentService.create(payment);
        } catch (RuntimeException e) {
            throw new RuntimeException("Tạo payment thất bại: " + e.getMessage());
        }
    }

    // ===================== UPDATE =====================
    public void updatePayment(Payment payment) {
        try {
            paymentService.update(payment);
        } catch (RuntimeException e) {
            throw new RuntimeException("Cập nhật payment thất bại: " + e.getMessage());
        }
    }

    // ===================== SOFT DELETE =====================
    public void cancelPayment(Long id) {
        try {
            paymentService.cancel(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Hủy payment thất bại: " + e.getMessage());
        }
    }
}
