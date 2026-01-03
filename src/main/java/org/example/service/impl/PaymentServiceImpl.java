package org.example.service.impl;

import org.example.entity.Payment;
import org.example.entity.enums.PaymentStatus;
import org.example.repository.PaymentRepository;
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
    public long createByInvoice(long invoiceId, String method) {

        String paymentNumber = generatePaymentNumber();

        if (paymentRepository.existsByPaymentNumber(paymentNumber)) {
            throw new RuntimeException("Payment number bị trùng");
        }

        long rows = paymentRepository.insertByInvoiceId(
                invoiceId,
                paymentNumber,
                method
        );

        if (rows == 0) {
            throw new RuntimeException("Invoice không tồn tại hoặc đã được thanh toán");
        }

        return rows;
    }

    @Override
    public void update(Payment payment) {
        paymentRepository.update(payment);
    }

    @Override
    public void cancel(Long id) {
        Payment payment = getById(id);

        if  (payment == null) {
            throw new RuntimeException("Payment không tồn tại");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Chỉ được hủy payment thành công");
        }

        paymentRepository.delete(id);
    }

    private String generatePaymentNumber() {
        return "PAY-" + System.currentTimeMillis();
    }
}
