package org.example.repository;

import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.Order;
import org.example.entity.OrderItem;

import java.util.List;
public interface OrderRepository {
    /**
     * Thêm mới một đơn hàng vào cơ sở dữ liệu.
     *
     * @param order đối tượng Order chứa thông tin đơn hàng cần thêm
     * @return ID của đơn hàng vừa tạo nếu thành công, ngược lại trả về null
     */
    Long insertOrder(Order order);

    /**
     * Thêm mới một sản phẩm vào đơn hàng hiện có.
     *
     * @param item đối tượng OrderItem cần thêm vào đơn hàng
     */
    void insertItem(OrderItem item);

    /**
     * Cập nhật trạng thái của đơn hàng.
     *
     * @param orderId ID của đơn hàng cần cập nhật
     * @param status trạng thái mới cần cập nhật cho đơn hàng
     */
    void updateOrderStatus(Long orderId, String status);

    /**
     * Tính toán lại và cập nhật tổng tiền của đơn hàng.
     * Nên được gọi sau khi thêm, cập nhật hoặc xóa sản phẩm trong đơn hàng.
     *
     * @param orderId ID của đơn hàng cần cập nhật tổng tiền
     */
    void updateOrderTotal(Long orderId);

    /**
     * Hủy đơn hàng bằng cách cập nhật trạng thái thành 'ĐÃ HỦY'.
     *
     * @param orderId ID của đơn hàng cần hủy
     */
    void cancelOrder(Long orderId);

    /**
     * Lấy tất cả đơn hàng trong hệ thống.
     * Chủ yếu được sử dụng bởi quản trị viên hoặc nhân viên để quản lý đơn hàng.
     *
     * @return danh sách các đối tượng OrderResponse chứa thông tin đơn hàng
     */
    List<OrderResponse> findAll();

    /**
     * Lấy thông tin chi tiết của một đơn hàng cụ thể.
     *
     * @param orderId ID của đơn hàng cần lấy thông tin
     * @return Đối tượng OrderResponse chứa thông tin chi tiết đơn hàng, hoặc null nếu không tìm thấy
     */
    OrderResponse findById(Long orderId);

    /**
     * Lấy tất cả sản phẩm trong một đơn hàng cụ thể.
     *
     * @param orderId ID của đơn hàng
     * @return danh sách các đối tượng OrderItemResponse chứa thông tin chi tiết sản phẩm
     */
    List<OrderItemResponse> findItems(Long orderId);
}
