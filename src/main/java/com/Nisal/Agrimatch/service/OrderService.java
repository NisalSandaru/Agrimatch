package com.Nisal.Agrimatch.service;

import com.Nisal.Agrimatch.dto.OrderRequest;
import com.Nisal.Agrimatch.dto.OrderResponse;
import com.Nisal.Agrimatch.entity.Order;
import com.Nisal.Agrimatch.entity.Product;
import com.Nisal.Agrimatch.entity.enums.OrderStatus;
import com.Nisal.Agrimatch.repository.OrderRepository;
import com.Nisal.Agrimatch.repository.ProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Place a new order
    public OrderResponse placeOrder(OrderRequest request) {
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check stock
        if (request.getQuantity() > product.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        // Reduce product stock
        product.setQuantity(product.getQuantity() - request.getQuantity());
        productRepository.save(product);

        // Create order
        Order order = new Order();
        order.setProductId(product.getId());
        order.setBuyerEmail(buyerEmail);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getPrice() * request.getQuantity());
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    // Get all orders for logged-in buyer
    public List<OrderResponse> getBuyerOrders() {
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderRepository.findByBuyerEmail(buyerEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Map Order entity to OrderResponse DTO
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setProductId(order.getProductId());
        response.setBuyerEmail(order.getBuyerEmail());
        response.setQuantity(order.getQuantity());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        return response;
    }
}
