package org.example.service;




import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;

import java.util.List;

public interface OrderService {

    Long createOrder(CreateOrderWithItemsRequest req, Long employeeId);

    void confirmOrder(Long orderId);

    void cancelOrder(Long orderId);

    List<OrderResponse> getAll();

    OrderResponse getById(Long orderId);

    List<OrderItemResponse> getItems(Long orderId);

}
