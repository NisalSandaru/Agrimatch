package com.Nisal.Agrimatch.service;

import com.Nisal.Agrimatch.dto.BuyerReportDTO;
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


    public OrderResponse cancelOrder(Long orderId) {
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Only allow the buyer who placed the order to cancel it
        if (!order.getBuyerEmail().equals(buyerEmail)) {
            throw new RuntimeException("You are not authorized to cancel this order");
        }

        // Can only cancel pending orders
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        // Restore product stock
        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() + order.getQuantity());
        productRepository.save(product);

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByStatus(String status) {
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        return orderRepository.findByBuyerEmail(buyerEmail)
                .stream()
                .filter(o -> status == null || o.getStatus().name().equalsIgnoreCase(status))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BuyerReportDTO getBuyerReport() {
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Order> orders = orderRepository.findByBuyerEmail(buyerEmail);

        BuyerReportDTO report = new BuyerReportDTO();
        report.setTotalOrders(orders.size());
        report.setCompletedOrders((int) orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count());
        report.setPendingOrders((int) orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
        report.setTotalSpent(orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalPrice)
                .sum());

        return report;
    }


}
