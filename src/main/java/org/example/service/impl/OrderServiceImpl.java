package org.example.service.impl;

import org.example.dto.request.Order.CreateOrderWithItemsRequest;
import org.example.dto.request.Order.OrderItemRequest;
import org.example.dto.response.Order.OrderItemResponse;
import org.example.dto.response.Order.OrderResponse;
import org.example.entity.enums.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.repository.impl.OrderRepositoryImpl;
import org.example.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository repo = new OrderRepositoryImpl();

    @Override
    public Long createOrder(CreateOrderWithItemsRequest req, Long employeeId) {

        BigDecimal discount = BigDecimal.ZERO;
        if (req.getDiscount() != null) {
            discount = req.getDiscount();
        }
        Long orderId = repo.insertOrder(
                req.getCustomerId(),
                req.getShippingAddress(),
                req.getNote(),
                discount,
                employeeId
        );
        List<OrderItemRequest> items = req.getItems();
        if (items != null) {
            for (OrderItemRequest item : items) {
                repo.insertItem(orderId, item);
            }
        }

        repo.updateOrderTotal(orderId);
        // trả về id của đơn hàng vừa tạo
        return orderId;
    }


    @Override
    public void confirmOrder(Long orderId) {
        // NOTE: Ở đây sẽ trừ kho + stock_movements
        repo.updateStatus(orderId, OrderStatus.CONFIRMED.name());
    }

    @Override
    public List<OrderResponse> getAll() {
        return repo.findAll();
    }
    @Override
    public OrderResponse getById(Long orderId) {
        return repo.findById(orderId);
    }
    @Override
    public List<OrderItemResponse> getItems(Long orderId) {
        return repo.getItems(orderId);
    }
    @Override
    public void cancelOrder(Long orderId) {
        repo.cancelOrder(orderId);
    }
}
