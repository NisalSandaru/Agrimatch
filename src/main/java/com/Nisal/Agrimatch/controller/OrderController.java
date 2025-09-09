package com.Nisal.Agrimatch.controller;

import com.Nisal.Agrimatch.dto.BuyerReportDTO;
import com.Nisal.Agrimatch.dto.OrderRequest;
import com.Nisal.Agrimatch.dto.OrderResponse;
import com.Nisal.Agrimatch.dto.ProductResponse;
import com.Nisal.Agrimatch.entity.Product;
import com.Nisal.Agrimatch.service.OrderService;
import com.Nisal.Agrimatch.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
@PreAuthorize("hasRole('BUYER')") // Only BUYER role can access
public class OrderController {

    private final ProductService productService;
    private final OrderService orderService;

    public OrderController(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    // Get all products (buyers can browse)
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        // Optionally, you can filter out products with quantity 0
        List<ProductResponse> products = productService.getAllAvailableProducts();
        return ResponseEntity.ok(products);
    }

    // Place an order
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.ok(response);
    }

    // Get all orders of logged-in buyer
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> orders = orderService.getBuyerOrders();
        return ResponseEntity.ok(orders);
    }

    // In OrderController.java
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/report/orders") // for filtered orders
    public ResponseEntity<List<OrderResponse>> getOrdersReport(@RequestParam(required = false) String status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/report/summary") // for buyer summary
    public ResponseEntity<BuyerReportDTO> getReport() {
        BuyerReportDTO report = orderService.getBuyerReport();
        return ResponseEntity.ok(report);
    }


}
