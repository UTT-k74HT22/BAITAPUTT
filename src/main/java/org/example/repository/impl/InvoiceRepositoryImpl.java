package org.example.repository.impl;

import org.example.dto.response.InvoiceResponse;
import org.example.entity.Invoice;
import org.example.entity.enums.InvoiceStatus;
import org.example.repository.BaseRepository;
import org.example.repository.InvoiceRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation cho InvoiceRepository.
 */
public class InvoiceRepositoryImpl extends BaseRepository implements InvoiceRepository {

    private static final String INSERT_SQL = """
        INSERT INTO invoices
        (order_id, invoice_number, issue_date, due_date,
         total_amount, paid_amount, status, tax_code)
        VALUES (?,?,?,?,?,?,?,?)
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
                        invoice.getStatus().name(),
                        invoice.getTaxCode()
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
    public List<Invoice> findAllByOrderId(Integer orderId) {
        String sql = """
        SELECT
            i.id,
            i.invoice_number,
            i.order_id,
            i.issue_date,
            i.due_date,
            i.total_amount,
            i.paid_amount,
            i.status,
            i.tax_code
        FROM invoices i
        WHERE i.order_id = ?
        ORDER BY issue_date DESC
        """;

        return queryForList(
                sql,
                params(orderId),
                this::mapRow
        );
    }
    @Override
    public List<Invoice> findAll() {
        String sql = """
        SELECT * FROM invoices
        ORDER BY issue_date DESC
    """;

        return queryForList(sql, params(), this::mapRow);
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

    /**
     * Map ResultSet → Invoice entity.
     */
    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId((long)rs.getInt("id"));
        i.setOrderId(rs.getInt("order_id"));
        i.setInvoiceNumber(rs.getString("invoice_number"));
        i.setIssueDate(rs.getTimestamp("issue_date").toLocalDateTime());

        i.setDueDate(
                rs.getTimestamp("due_date") != null
                        ? rs.getTimestamp("due_date").toLocalDateTime()
                        : null
        );

        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setPaidAmount(rs.getBigDecimal("paid_amount"));
        i.setStatus(InvoiceStatus.valueOf(rs.getString("status")));
        i.setTaxCode(rs.getString("tax_code"));
        i.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        i.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return i;
    }

    /**
     * Map to response
     * @param rs
     * @param rowNum
     * @return
     * @throws SQLException
     */
    private  InvoiceResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        InvoiceResponse i = new InvoiceResponse();
        i.setId(rs.getInt("id"));
        i.setOrderId(rs.getInt("order_id"));
        i.setInvoiceNumber(rs.getString("invoice_number"));
        i.setIssueDate(rs.getTimestamp("issue_date").toLocalDateTime());
        i.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setPaidAmount(rs.getBigDecimal("paid_amount"));
        i.setStatus(InvoiceStatus.valueOf(rs.getString("status")));
        i.setTaxCode(rs.getString("tax_code"));
        return i;
    }
}

