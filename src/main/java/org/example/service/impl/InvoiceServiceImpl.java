package org.example.service.impl;

import org.example.dto.request.CreateInvoiceRequest;
import org.example.dto.response.InvoiceResponse;
import org.example.entity.Invoice;
import org.example.entity.enums.InvoiceStatus;
import org.example.repository.InvoiceRepository;
import org.example.repository.impl.InvoiceRepositoryImpl;
import org.example.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Xử lý nghiệp vụ Hóa đơn
 */
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    public InvoiceServiceImpl() {
        this.invoiceRepository = new InvoiceRepositoryImpl();
    }


    @Override
    public InvoiceResponse create(CreateInvoiceRequest request) {

        // 1. Kiểm tra order đã có invoice chưa
        if (invoiceRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalStateException("Order đã tồn tại hóa đơn");
        }

        // 2. Khởi tạo entity Invoice
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.getOrderId());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(
                request.getDueDate() != null
                        ? request.getDueDate().atStartOfDay()
                        : null
        );
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setStatus(InvoiceStatus.NEW);
        invoice.setTaxCode(request.getTaxCode());

        // 3. Insert DB
        long id = invoiceRepository.insert(invoice);

        // 4. Trả response
        return toResponse(invoiceRepository.findById((int) id)
                .orElseThrow(() -> new RuntimeException("Create invoice failed")));
    }

    @Override
    public InvoiceResponse getById(Integer id) {
        return invoiceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public InvoiceResponse getByOrderId(Integer orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public void updatePayment(Integer invoiceId, BigDecimal paidAmount) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BigDecimal newPaid = invoice.getPaidAmount().add(paidAmount);

        // Xác định trạng thái hóa đơn
        InvoiceStatus status;
        if (newPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            status = InvoiceStatus.PAID;
        } else {
            status = InvoiceStatus.PARTIAL;
        }

        invoiceRepository.updatePayment(
                invoiceId,
                newPaid,
                status.name()
        );
    }

    @Override
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }




    /**
     * Sinh mã hóa đơn
     */
    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Map Entity → Response DTO
     */
    private InvoiceResponse toResponse(Invoice i) {
        InvoiceResponse r = new InvoiceResponse();
        r.setId(i.getId().intValue());
        r.setInvoiceNumber(i.getInvoiceNumber());
        r.setOrderId(i.getOrderId());
        r.setIssueDate(i.getIssueDate());
        r.setDueDate(i.getDueDate());
        r.setTotalAmount(i.getTotalAmount());
        r.setPaidAmount(i.getPaidAmount());
        r.setStatus(i.getStatus());
        r.setTaxCode(i.getTaxCode());
        return r;
    }
}
