package org.example.repository;


import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.request.Order.OrderItemRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository {

    Long insertOrder(Long customerId,
                     String shippingAddress,
                     String note,
                     BigDecimal discount,
                     Long employeeId );

    void insertItem(Long orderId, OrderItemRequest item);

    void updateOrderTotal(Long orderId);

    void updateStatus(Long orderId, String status);

    void cancelOrder(Long orderId);

    List<OrderResponse> findAll();

    List<OrderItemResponse> getItems(Long orderId);

    OrderResponse findById(Long orderId);

}
