package org.example.controller;

import org.example.dto.request.CreateInvoiceRequest;
import org.example.dto.response.InvoiceResponse;
import org.example.service.InvoiceService;
import org.example.service.impl.InvoiceServiceImpl;

import java.math.BigDecimal;
import java.util.List;

/**
 * Điều phối các thao tác liên quan đến Invoice
 */
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ====== CONSTRUCTOR INJECT ======
    public InvoiceController() {
        this.invoiceService = new InvoiceServiceImpl();
    }

    /**
     * Tạo hóa đơn
     */
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        return invoiceService.create(request);
    }

    /**
     * Xem hóa đơn theo ID
     */
    public InvoiceResponse getInvoiceById(Integer id) {
        return invoiceService.getById(id);
    }

    /**
     * Xem hóa đơn theo Order ID
     */
    public InvoiceResponse getInvoiceByOrder(Integer orderId) {
        return invoiceService.getByOrderId(orderId);
    }

    /**
     * Lấy toàn bộ hóa đơn
     */
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceService.getAll();
    }

    /**
     * Thanh toán hóa đơn
     */
    public void payInvoice(Integer invoiceId, BigDecimal amount) {
        invoiceService.updatePayment(invoiceId, amount);
    }
}
