package com.Nisal.Agrimatch.controller;

import com.Nisal.Agrimatch.dto.OrderResponse;
import com.Nisal.Agrimatch.service.FarmerOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmer/orders")
@PreAuthorize("hasRole('FARMER')")
public class FarmerOrderController {

    private final FarmerOrderService farmerOrderService;

    public FarmerOrderController(FarmerOrderService farmerOrderService) {
        this.farmerOrderService = farmerOrderService;
    }

    @GetMapping("/debug")
    public String debugAuth() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "null" : auth.getName() + " | " + auth.getAuthorities();
    }


    // Get all orders for farmer's products
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(farmerOrderService.getOrdersForFarmerProducts());
    }

    // Mark order as COMPLETED
    @PreAuthorize("hasRole('FARMER')")
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long orderId) {
        System.out.println(">>> Entered completeOrder endpoint with ID: " + orderId);
        return ResponseEntity.ok(farmerOrderService.completeOrder(orderId));
    }


}
