package org.example.service.impl;

import org.example.dto.request.Order.AddOrderItemRequest;
import org.example.dto.request.Order.CreateOrderRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.entity.enums.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository = new OrderRepositoryImpl();

    @Override
    public Long createOrder(CreateOrderRequest request, Long employeeId) {
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setCreatedByEmployeeId(employeeId);
        order.setNote(request.getNote());

        return orderRepository.insertOrder(order);
    }

    @Override
    public void addItem(Long orderId, AddOrderItemRequest request) {
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setProductId(request.getProductId());
        item.setBatchId(request.getBatchId());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());

        BigDecimal total = request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));
        item.setLineTotal(total);

        orderRepository.insertItem(item);
    }

    @Override
    public List<OrderResponse> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public OrderResponse getById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<OrderItemResponse> getItems(Long orderId) {
        return orderRepository.findItems(orderId);
    }

    @Override
    public void confirm(Long orderId) {
        orderRepository.updateOrderStatus(orderId, OrderStatus.CONFIRMED.name());
    }

    @Override
    public void cancel(Long orderId) {
        orderRepository.cancelOrder(orderId);
    }
}
