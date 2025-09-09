package com.Nisal.Agrimatch.controller;

import com.Nisal.Agrimatch.dto.FarmerReportDTO;
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

    // Mark order as REJECTED
    @PreAuthorize("hasRole('FARMER')")
    @PutMapping("/{orderId}/reject")
    public ResponseEntity<OrderResponse> rejectOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(farmerOrderService.rejectOrder(orderId));
    }

    // Full summary report for farmer
    @GetMapping("/report/summary")
    public ResponseEntity<FarmerReportDTO> getReport() {
        FarmerReportDTO report = farmerOrderService.getFarmerReport();
        return ResponseEntity.ok(report);
    }

    // Orders report, optionally filtered by status
    @GetMapping("/report/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersReport(@RequestParam(required = false) String status) {
        List<OrderResponse> orders = farmerOrderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }


}
