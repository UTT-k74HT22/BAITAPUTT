package org.example.controller;

import org.example.dto.request.Order.AddOrderItemRequest;
import org.example.dto.request.Order.CreateOrderRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.service.OrderService;
import org.example.service.impl.OrderServiceImpl;

import java.util.List;

public class OrderController {

    private final OrderService orderService = new OrderServiceImpl();

    /**
     * Tạo mới đơn hàng
     *
     * @param request dữ liệu tạo đơn
     * @param employeeId nhân viên tạo đơn
     * @return ID đơn hàng vừa tạo
     */
    public Long createOrder(CreateOrderRequest request, Long employeeId) {
        return orderService.createOrder(request, employeeId);
    }

    /**
     * Thêm sản phẩm vào đơn hàng
     */
    public void addItem(Long orderId, AddOrderItemRequest request) {
        orderService.addItem(orderId, request);
    }

    /**
     * Lấy danh sách tất cả đơn hàng
     */
    public List<OrderResponse> getAllOrders() {
        return orderService.getAll();
    }

    /**
     * Lấy chi tiết 1 đơn hàng
     */
    public OrderResponse getOrderById(Long orderId) {
        return orderService.getById(orderId);
    }

    /**
     * Lấy danh sách sản phẩm trong đơn
     */
    public List<OrderItemResponse> getOrderItems(Long orderId) {
        return orderService.getItems(orderId);
    }

    /**
     * Xác nhận đơn hàng
     */
    public void confirmOrder(Long orderId) {
        orderService.confirm(orderId);
    }

    /**
     * Hủy đơn hàng (xóa mềm)
     */
    public void cancelOrder(Long orderId) {
        orderService.cancel(orderId);
    }
}
