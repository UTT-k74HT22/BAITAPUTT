package org.example.service.impl;

import org.example.controller.AccountController;
import org.example.entity.Payment;
import org.example.entity.enums.PaymentStatus;
import org.example.repository.PaymentRepository;
import org.example.repository.impl.ManufacturerReponsitoryImpl;
import org.example.repository.impl.PaymentRepositoryImpl;
import org.example.service.PaymentService;

import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl() {
        this.paymentRepository = new PaymentRepositoryImpl();
    }

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getById(Long id) {
        Payment payment = paymentRepository.findById(id);
        return payment;
    }

    @Override
    public long create(Payment payment) {

        if (paymentRepository.existsByPaymentNumber(payment.getPaymentNumber())) {
            throw new RuntimeException("Payment number đã tồn tại");
        }

        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }

        return paymentRepository.insert(payment);
    }

    @Override
    public void update(Payment payment) {
        Payment current = getById(payment.getId());

        if (current.getStatus() == PaymentStatus.CANCELED) {
            throw new RuntimeException("Không thể cập nhật payment đã bị hủy");
        }

        paymentRepository.update(payment);
    }

    @Override
    public void cancel(Long id) {
        Payment payment = getById(id);

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new RuntimeException("Payment đã bị hủy trước đó");
        }

        paymentRepository.delete(id);
    }
}
