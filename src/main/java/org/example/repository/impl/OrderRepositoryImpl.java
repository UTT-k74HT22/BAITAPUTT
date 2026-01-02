package org.example.repository.impl;

import org.example.dto.request.Order.OrderItemRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.enums.OrderStatus;
import org.example.repository.BaseRepository;
import org.example.repository.OrderRepository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;

public class OrderRepositoryImpl extends BaseRepository implements OrderRepository {

    /**
     * Tạo đơn hàng mới
     * */
    @Override
    public Long insertOrder(Long customerId,
                            String shippingAddress,
                            String note,
                            BigDecimal discount,
                            Long userId) {

        String sql = """
                    INSERT INTO orders
                    (customer_id, order_number, order_date,
                     shipping_address, note, discount, tax, created_by_user_id, status)
                    VALUES (?, CONCAT('ORD-', UNIX_TIMESTAMP()), NOW(), ?, ?, ?, 0, ?, 'NEW')
                """;
        return executeInsertAndReturnId(sql, ps -> {
            ps.setLong(1, customerId);
            ps.setString(2, shippingAddress);
            ps.setString(3, note == null ? "" : note);
            ps.setBigDecimal(4, discount == null ? BigDecimal.ZERO : discount);
            ps.setLong(5, userId);
        });

    }

    /**
     * Thêm sản phẩm vào đơn hàng
     * */
    @Override
    public void insertItem(Long orderId, OrderItemRequest item) {
        String sql = """
        INSERT INTO order_items
        (order_id, product_id, batch_id, quantity, unit_price, line_total, warehouse_id, note)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        executeUpdate(sql, ps -> {
            ps.setLong(1, orderId);
            ps.setLong(2, item.getProductId());

            if (item.getBatchId() != null)
                ps.setLong(3, item.getBatchId());
            else
                ps.setNull(3, java.sql.Types.INTEGER);

            ps.setInt(4, item.getQuantity());
            ps.setBigDecimal(5, item.getUnitPrice());
            ps.setBigDecimal(6, item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // ✅ set warehouseId vào tham số 7
            if (item.getWarehouseId() != null)
                ps.setLong(7, item.getWarehouseId());
            else
                ps.setNull(7, java.sql.Types.INTEGER);

            // ✅ set note vào tham số 8
            ps.setString(8, item.getNote() == null ? "" : item.getNote());
        });
    }

    /**
     * Cập nhật tổng tiền đơn hàng
     * */
    @Override
    public void updateOrderTotal(Long orderId) {
        String sql = """
            UPDATE orders o
            SET subtotal = (
                SELECT COALESCE(SUM(line_total), 0)
                FROM order_items WHERE order_id = o.id
            ),
            total_amount = subtotal - discount
            WHERE o.id = ?
        """;

        executeUpdate(sql, ps -> ps.setLong(1, orderId));
    }
    /**
     * Cập nhật trạng thái đơn hàng
     * */
    @Override
    public void updateStatus(Long orderId, String status) {
        executeUpdate(
                "UPDATE orders SET status = ? WHERE id = ?",
                ps -> {
                    ps.setString(1, status);
                    ps.setLong(2, orderId);
                }
        );
    }

    /**
     *  xóa đơn hàng bằng cách cập nhật status (soft delete)
     * @param orderId ID của đơn cần hủy
     */
    @Override
    public void cancelOrder(Long orderId) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        executeUpdate(sql, ps -> {
            ps.setString(1, OrderStatus.CANCELED.name());
            ps.setLong(2, orderId);
        });
    }
    /**
     * Lấy danh sách tất cả đơn hàng
     * */
    @Override
    public List<OrderResponse> findAll() {
        String sql = """
        SELECT 
            o.id, 
            o.order_number, 
            o.status, 
            o.total_amount, 
            o.order_date, 
            o.note,
            o.shipping_address,
            u.full_name AS creator_name,
            u.phone     AS creator_phone,
            u.email     AS creator_email
            
        FROM orders o
        LEFT JOIN user_profiles u ON o.created_by_user_id = u.id
        ORDER BY o.order_date DESC
            """;
        return queryForList(sql, rs -> {
            try {
                return map(rs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * Lấy thông tin đơn hàng theo ID
     * @param orderId ID của đơn hàng
     * @return thông tin đơn hàng
     */
        @Override
        public OrderResponse findById(Long orderId) {
            String sql = """
        SELECT
            o.id,
            o.order_number,
            o.order_date,
            o.status,
            o.total_amount,
            o.note,
            o.shipping_address,
            o.discount,
            o.tax,
            o.created_by_user_id,

            u.full_name AS customer_name,
            u.phone     AS customer_phone,
            u.email     AS customer_email,

            c.full_name AS creator_name,
            c.phone     AS creator_phone,
            c.email     AS creator_email
           
            
            

        FROM orders o
        JOIN user_profiles u ON o.customer_id = u.id
        LEFT JOIN user_profiles c ON o.created_by_user_id = c.id  
        
        WHERE o.id = ?
        """;

            return queryForObject(sql, ps -> ps.setLong(1, orderId), rs -> {
                OrderResponse o = new OrderResponse();

                o.setId(rs.getLong("id"));
                o.setOrderNumber(rs.getString("order_number"));
                o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                o.setStatus(rs.getString("status"));
                o.setTotalAmount(rs.getBigDecimal("total_amount"));
                o.setNote(rs.getString("note"));
                o.setShippingAddress(rs.getString("shipping_address"));

                // customer info
                o.setCustomerName(rs.getString("customer_name"));
                o.setCustomerPhone(rs.getString("customer_phone"));
                o.setCustomerEmail(rs.getString("customer_email"));

                // creator info
                o.setCreatorName(rs.getString("creator_name"));
                o.setCreatorEmail(rs.getString("creator_email"));
                o.setCreatorPhone(rs.getString("creator_phone"));


                return o;
            }).orElseThrow(() ->
                    new RuntimeException("Không tìm thấy đơn hàng với ID " + orderId)
            );
        }

    /**
     * Lấy danh sách sản phẩm trong đơn hàng
     * @param orderId ID của đơn hàng
     * @return danh sách sản phẩm trong đơn hàng
     */
    @Override
    public List<OrderItemResponse> getItems(Long orderId) {
        String sql = """
        SELECT
            oi.id,
            p.name AS product_name,
            oi.product_id,
            oi.batch_id,
            oi.warehouse_id,
            oi.quantity,
            oi.unit_price,
            oi.line_total,
            oi.note
        FROM order_items oi
        JOIN products p ON oi.product_id = p.id
        WHERE oi.order_id = ?
    """;

        return queryForList(sql, ps -> ps.setLong(1, orderId), rs -> {
            OrderItemResponse i = new OrderItemResponse();
            i.setId(rs.getLong("id"));
            i.setProductName(rs.getString("product_name"));
            i.setProductId(rs.getLong("product_id"));     // map product id
            i.setBatchId(rs.getLong("batch_id"));          // map batch id
            i.setWarehouseId(rs.getLong("warehouse_id"));  // ✅ map warehouse id
            i.setQuantity(rs.getInt("quantity"));
            i.setUnitPrice(rs.getBigDecimal("unit_price"));
            i.setLineTotal(rs.getBigDecimal("line_total"));
            i.setNote(rs.getString("note"));
            return i;
        });
    }


    /**
     * Mapper liên kết với csdl
     * đang để dùng chung map, có thể tách ra để phù hợp code hởn find
     * */
    private OrderResponse map(ResultSet rs) {
        try {
            OrderResponse o = new OrderResponse();
            o.setId(rs.getLong("id"));
            o.setOrderNumber(rs.getString("order_number"));
            o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            o.setStatus(rs.getString("status"));
            o.setTotalAmount(rs.getBigDecimal("total_amount"));
            o.setNote(rs.getString("note") == null ? "-" : rs.getString("note"));
            o.setShippingAddress(rs.getString("shipping_address"));
            o.setCreatorName(rs.getString("creator_name"));
            o.setCreatorEmail(rs.getString("creator_email"));
            o.setCreatorPhone(rs.getString("creator_phone"));

            return o;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi map Order: " + e.getMessage(), e);
        }
    }
}
