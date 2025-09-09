package com.Nisal.Agrimatch.service;

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
public class FarmerOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public FarmerOrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Get all orders for products owned by the logged-in farmer
    public List<OrderResponse> getOrdersForFarmerProducts() {
        String farmerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Get all product IDs belonging to this farmer
        List<Long> farmerProductIds = productRepository.findByFarmerEmail(farmerEmail)
                .stream()
                .map(Product::getId)
                .toList();

        // Get all orders for these products
        return orderRepository.findAll()
                .stream()
                .filter(o -> farmerProductIds.contains(o.getProductId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Mark order as COMPLETED
    public OrderResponse completeOrder(Long orderId) {
        String farmerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Ensure this order belongs to the farmer
        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getFarmerEmail().equals(farmerEmail)) {
            throw new RuntimeException("You cannot complete an order for another farmer’s product");
        }

        order.setStatus(OrderStatus.COMPLETED);
        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
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
