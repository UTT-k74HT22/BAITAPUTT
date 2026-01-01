package org.example.controller;

import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.service.OrderService;
import org.example.service.impl.OrderServiceImpl;

import java.util.List;

public class OrderController {

    private final OrderService service = new OrderServiceImpl();


    public Long createOrder(CreateOrderWithItemsRequest req, Long employeeId) {
        return service.createOrder(req, employeeId);
    }

    public void confirm(Long orderId) {
        service.confirmOrder(orderId);
    }

    public List<OrderResponse> getAll() {
        return service.getAll();
    }
    public OrderResponse getById(Long orderId) {
        return service.getById(orderId);
    }
    OrderResponse findById(Long orderId){
        return service.getById(orderId);

    }
    public void cancel(Long orderId){
        service.cancelOrder(orderId);
    }
    // Lấy danh sách thuốc trong đơn
    public List<OrderItemResponse> getItems(Long orderId) {
        return service.getItems(orderId);
    }
}
