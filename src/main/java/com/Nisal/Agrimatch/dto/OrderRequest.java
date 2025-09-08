package com.Nisal.Agrimatch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull
    private Long productId;  // Product being ordered

    @NotNull
    @Min(1)
    private Integer quantity; // Quantity to order

    // ✅ Getters & Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
