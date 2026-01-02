package org.example.controller;

import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.service.OrderService;
import org.example.service.impl.OrderServiceImpl;

import java.util.List;

public class OrderController {

    private final OrderService service = new OrderServiceImpl();

    /**
     * Tạo đơn hàng
     * @param req
     * @param employeeId
     * @return
     */
    public Long createOrder(CreateOrderWithItemsRequest req, Long employeeId) {
        return service.createOrder(req, employeeId);
    }
    /**
     * Xác nhận đơn hàng
     * @param orderId
     */
    public void confirm(Long orderId) {
        service.confirmOrder(orderId);
    }
    /**
     * Lấy danh sách đơn hàng
     * @return
     */
    public List<OrderResponse> getAll() {
        return service.getAll();
    }
    /**
     * Lấy đơn hàng theo id
     * @param orderId
     * @return
     */
    public OrderResponse getById(Long orderId) {
        return service.getById(orderId);
    }
    /**
     * Hủy đơn hàng
     * @param orderId
     */
    public void cancel(Long orderId){
        service.cancelOrder(orderId);
    }
    // Lấy danh sách thuốc trong đơn
    public List<OrderItemResponse> getItems(Long orderId) {
        return service.getItems(orderId);
    }
}
