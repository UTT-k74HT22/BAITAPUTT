package org.example.service;

import org.example.dto.request.CreateInvoiceRequest;
import org.example.dto.response.InvoiceResponse;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {

    /**
     * Tạo hóa đơn cho 1 đơn hàng
     */
    InvoiceResponse create(CreateInvoiceRequest request);

    /**
     * Lấy hóa đơn theo ID
     */
    InvoiceResponse getById(Integer id);

    /**
     * Lấy hóa đơn theo Order ID
     */
    InvoiceResponse getByOrderId(Integer orderId);

    /**
     * Cập nhật thanh toán cho hóa đơn
     */
    void updatePayment(Integer invoiceId, BigDecimal paidAmount);

    List<InvoiceResponse> getAll();
}
