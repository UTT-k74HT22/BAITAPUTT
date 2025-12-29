package org.example.repository.impl;


import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.repository.BaseRepository;
import org.example.repository.OrderRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderRepositoryImpl extends BaseRepository implements OrderRepository {
    /**
     * @param order đối tượng Order chứa thông tin đơn hàng cần thêm
     * @return
     */
    @Override
    public Long insertOrder(Order order) {
        String sql = """
            INSERT INTO orders
            (order_number, customer_id, order_date, status,
             subtotal, discount, tax, total_amount,
             created_by_user_id, note)
            VALUES (?, ?, NOW(), 'NEW', 0, 0, 0, 0, ?, ?)
        """;

        return executeInsertAndReturnId(sql, ps -> {
            ps.setString(1, order.getOrderNumber());
            ps.setLong(2, order.getCustomerId());
            ps.setLong(3, order.getCreatedByEmployeeId());
            ps.setString(4, order.getNote());
        });
    }
    /**
     * @param item đối tượng OrderItem cần thêm vào đơn hàng
     */
    @Override
    public void insertItem(OrderItem item) {
        String sql = """
            INSERT INTO order_items
            (order_id, product_id, batch_id,
             quantity, unit_price, discount, line_total)
            VALUES (?, ?, ?, ?, ?, 0, ?)
        """;

        executeUpdate(sql, ps -> {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setObject(3, item.getBatchId());
            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getUnitPrice());
            ps.setBigDecimal(6, item.getLineTotal());
        });

        updateOrderTotal(item.getOrderId());
    }


    /**
     * @param orderId ID của đơn hàng cần cập nhật
     * @param status  trạng thái mới cần cập nhật cho đơn hàng
     */
    @Override
    public void updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        executeUpdate(sql, params(status, orderId));
    }

    /**
     * @param orderId ID của đơn hàng cần cập nhật tổng tiền
     */
    @Override
    public void updateOrderTotal(Long orderId) {
        String sql = """
            UPDATE orders
            SET subtotal = (
                SELECT IFNULL(SUM(line_total), 0)
                FROM order_items
                WHERE order_id = ?
            ),
            total_amount = subtotal
            WHERE id = ?
        """;

        executeUpdate(sql, params(orderId, orderId));
    }


    /**
     * @param orderId ID của đơn hàng cần hủy
     */
    @Override
    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, "CANCELED");
    }

    /**
     * @return
     */
    @Override
    public List<OrderResponse> findAll() {
        String sql = """
            SELECT
                o.*,
                up.code      AS customerCode,
                up.full_name AS customerName
            FROM orders o
            JOIN user_profiles up ON up.id = o.customer_id
            WHERE o.status <> 'CANCELED'
            ORDER BY o.created_at DESC
        """;

        return queryForList(sql, this::mapOrder);
    }

    /**
     * @param orderId ID của đơn hàng cần lấy thông tin
     * @return
     */
    @Override
    public OrderResponse findById(Long orderId) {
        String sql = """
            SELECT
                o.*,
                up.code      AS customerCode,
                up.full_name AS customerName
            FROM orders o
            JOIN user_profiles up ON up.id = o.customer_id
            WHERE o.id = ?
        """;

        OrderResponse order =
                queryForObject(sql, params(orderId), this::mapOrder)
                        .orElse(null);

        if (order != null) {
            order.setItems(findItems(orderId));
        }

        return order;
    }

    /**
     * @param orderId ID của đơn hàng
     * @return
     */
    @Override
    public List<OrderItemResponse> findItems(Long orderId) {
        String sql = """
            SELECT
                oi.*,
                p.code       AS productCode,
                p.name       AS productName,
                b.batch_code AS batchCode
            FROM order_items oi
            JOIN products p ON p.id = oi.product_id
            LEFT JOIN batches b ON b.id = oi.batch_id
            WHERE oi.order_id = ?
        """;

        return queryForList(sql, params(orderId), this::mapItem);
    }
    private OrderResponse mapOrder(ResultSet rs) throws SQLException {
        OrderResponse o = new OrderResponse();

        o.setId(rs.getLong("id"));
        o.setOrderNumber(rs.getString("order_number"));

        o.setCustomerId(rs.getLong("customer_id"));
        o.setCustomerCode(rs.getString("customerCode"));
        o.setCustomerName(rs.getString("customerName"));

        o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        o.setStatus(rs.getString("status"));

        o.setSubtotal(rs.getBigDecimal("subtotal"));
        o.setDiscount(rs.getBigDecimal("discount"));
        o.setTax(rs.getBigDecimal("tax"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));

        o.setNote(rs.getString("note"));
        o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        o.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return o;
    }

    private OrderItemResponse mapItem(ResultSet rs) throws SQLException {
        OrderItemResponse i = new OrderItemResponse();

        i.setId(rs.getLong("id"));
        i.setOrderId(rs.getLong("order_id"));

        i.setProductId(rs.getLong("product_id"));
        i.setProductCode(rs.getString("productCode"));
        i.setProductName(rs.getString("productName"));

        i.setBatchId(rs.getObject("batch_id", Long.class));
        i.setBatchCode(rs.getString("batchCode"));

        i.setQuantity(rs.getInt("quantity"));
        i.setUnitPrice(rs.getBigDecimal("unit_price"));
        i.setDiscount(rs.getBigDecimal("discount"));
        i.setLineTotal(rs.getBigDecimal("line_total"));

        return i;
    }
}
