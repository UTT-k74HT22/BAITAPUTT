package org.example.repository.impl;

import org.example.entity.Payment;
import org.example.entity.enums.PaymentStatus;
import org.example.repository.PaymentRepository;
import org.example.repository.BaseRepository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class PaymentRepositoryImpl extends BaseRepository implements PaymentRepository {

    @Override
    public List<Payment> findAll() {
        String  sql = """
                SELECT id, invoice_id, payment_number, payment_date, amount, method,
                       status, created_at
                FROM payments
                ORDER BY id DESC""";
        return queryForList(sql, this::mapRow);
    }

    @Override
    public Payment findById(Long id) {
        String sql = """
                SELECT id, invoice_id, payment_number, payment_date, amount, method,
                       status, created_at
                FROM payments
                WHERE id = ?
                """;
        Optional<Payment> result = queryForObject(sql, params(id), this::mapRow);
        return result.orElse(null);
    }

    @Override
    public long insert(Payment p) {
        String sql = """
                INSERT INTO payments(invoice_id, payment_number, payment_date, amount, method,
                            status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        return executeUpdate(sql, ps -> {
            ps.setInt(1, p.getInvoiceId());
            ps.setString(2, p.getPaymentNumber());
            ps.setTimestamp(3, Timestamp.valueOf(p.getPaymentDate()));
            ps.setBigDecimal(4, p.getAmount());
            ps.setString(5, p.getMethod());
            ps.setString(6, p.getStatus().name());
        });
    }

    @Override
    public void update(Payment p) {
        String sql = """
                UPDATE payments
                SET invoice_id = ?, payment_number = ?, payment_date = ?, amount = ?,  method = ?, status = ?
                WHERE id = ?
                """;
        executeUpdate(sql, params(
                p.getInvoiceId(),
                p.getPaymentNumber(),
                Timestamp.valueOf(p.getPaymentDate()),
                p.getAmount(),
                p.getMethod(),
                p.getStatus().name(),
                p.getId()
        ));
    }

    @Override
    public void delete(Long id) {
        String sql = """
                UPDATE payments
                SET status = ?
                WHERE id = ?
                """;
        executeUpdate(sql, params(PaymentStatus.CANCELED.name(), id));
    }

    @Override
    public boolean existsByPaymentNumber(String paymentNumber) {
        String sql = """
            SELECT 1 FROM payments
            WHERE payment_number = ?
            LIMIT 1
        """;
        return exists(sql, params(paymentNumber));
    }


    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getLong("id"));
        p.setInvoiceId(rs.getInt("invoice_id"));
        p.setPaymentNumber(rs.getString("payment_number"));
        Timestamp paymentTs = rs.getTimestamp("payment_date");
        if (paymentTs != null) p.setPaymentDate(paymentTs.toLocalDateTime());
        p.setAmount(rs.getBigDecimal("amount"));
        p.setMethod(rs.getString("method"));
        p.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());

        return p;
    }
}
