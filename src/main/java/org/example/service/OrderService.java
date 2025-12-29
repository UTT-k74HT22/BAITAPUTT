package org.example.service;


import org.example.dto.request.Order.AddOrderItemRequest;
import org.example.dto.request.Order.CreateOrderRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;

import java.util.List;

public interface OrderService {

    Long createOrder(CreateOrderRequest request, Long employeeId);

    void addItem(Long orderId, AddOrderItemRequest request);

    List<OrderResponse> getAll();

    OrderResponse getById(Long orderId);

    List<OrderItemResponse> getItems(Long orderId);

    void confirm(Long orderId);

    void cancel(Long orderId);
}
