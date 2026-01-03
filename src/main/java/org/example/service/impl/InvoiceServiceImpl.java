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

public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository = new InvoiceRepositoryImpl();

    @Override
    public InvoiceResponse create(CreateInvoiceRequest request) {

        // 1. Check trùng invoice theo order
        if (invoiceRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalStateException("Order đã tồn tại hóa đơn");
        }

        // 2. Tạo entity – SET ĐẦY ĐỦ DEFAULT
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.getOrderId());

        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(7));

        // ⚠️ CỰC KỲ QUAN TRỌNG
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setPaidAmount(BigDecimal.ZERO);

        invoice.setStatus(InvoiceStatus.NEW);
        // hoặc "" nếu DB cho phép

        // 3. Insert
        long id = invoiceRepository.insert(invoice);

        // 4. Response
        return invoiceRepository.findById((int) id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Create invoice failed"));
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
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public void updatePayment(Integer invoiceId, BigDecimal paidAmount) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.CANCELED) {
            throw new IllegalStateException("Invoice đã bị hủy");
        }

        BigDecimal newPaid = invoice.getPaidAmount().add(paidAmount);

        InvoiceStatus status =
                newPaid.compareTo(invoice.getTotalAmount()) >= 0
                        ? InvoiceStatus.PAID
                        : InvoiceStatus.PARTIAL;

        invoiceRepository.updatePayment(invoiceId, newPaid, status.name());
    }


    @Override
    public void delete(Integer id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Không thể hủy invoice đã thanh toán");
        }

        // ❗ HỦY = CANCEL
        invoiceRepository.updatePayment(
                id,
                invoice.getPaidAmount(),
                InvoiceStatus.CANCELED.name()
        );
    }



    @Override
    public void updateStatus(Integer invoiceId, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoiceRepository.updatePayment(
                invoiceId,
                invoice.getPaidAmount(),
                status.name()
        );
    }



    // ================= PRIVATE =================

    private String generateInvoiceNumber() {
        return "INV-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

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

        return r;
    }
}
