package org.example.repository.impl;

import org.example.entity.Invoice;
import org.example.entity.enums.InvoiceStatus;
import org.example.repository.BaseRepository;
import org.example.repository.InvoiceRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InvoiceRepositoryImpl extends BaseRepository implements InvoiceRepository {

    private static final String INSERT_SQL = """
        INSERT INTO invoices
        (order_id, invoice_number, issue_date, due_date,
         total_amount, paid_amount, status)
        VALUES (?,?,?,?,?,?,?)
    """;

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM invoices WHERE id = ?";

    private static final String FIND_BY_ORDER_SQL =
            "SELECT * FROM invoices WHERE order_id = ?";

    private static final String EXISTS_BY_ORDER_SQL =
            "SELECT 1 FROM invoices WHERE order_id = ? LIMIT 1";

    private static final String UPDATE_PAYMENT_SQL = """
        UPDATE invoices
        SET paid_amount = ?, status = ?
        WHERE id = ?
    """;

    @Override
    public long insert(Invoice invoice) {
        return executeInsertAndReturnId(
                INSERT_SQL,
                params(
                        invoice.getOrderId(),
                        invoice.getInvoiceNumber(),
                        invoice.getIssueDate(),
                        invoice.getDueDate(),
                        invoice.getTotalAmount(),
                        invoice.getPaidAmount(),
                        invoice.getStatus().name()
                )
        );
    }

    @Override
    public Optional<Invoice> findById(Integer id) {
        return queryForObject(FIND_BY_ID_SQL, params(id), this::mapRow);
    }

    @Override
    public Optional<Invoice> findByOrderId(Integer orderId) {
        return queryForObject(FIND_BY_ORDER_SQL, params(orderId), this::mapRow);
    }

    @Override
    public boolean existsByOrderId(Integer orderId) {
        return exists(EXISTS_BY_ORDER_SQL, params(orderId));
    }

    @Override
    public List<Invoice> findAll() {
        return queryForList(
                "SELECT * FROM invoices ORDER BY issue_date DESC",
                params(),
                this::mapRow
        );
    }

    @Override
    public void updatePayment(Integer invoiceId,
                              BigDecimal paidAmount,
                              String status) {
        executeUpdateOrThrow(
                UPDATE_PAYMENT_SQL,
                params(paidAmount, status, invoiceId),
                "Invoice not found"
        );
    }

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getLong("id"));
        i.setOrderId(rs.getInt("order_id"));
        i.setInvoiceNumber(rs.getString("invoice_number"));
        i.setIssueDate(rs.getTimestamp("issue_date").toLocalDateTime());
        i.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setPaidAmount(rs.getBigDecimal("paid_amount"));
        i.setStatus(InvoiceStatus.valueOf(rs.getString("status")));
        return i;
    }
}
