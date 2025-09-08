package com.Nisal.Agrimatch.entity;

import com.Nisal.Agrimatch.entity.enums.OrderStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;     // ID of the product ordered
    private String buyerEmail;  // Logged-in buyer
    private int quantity;       // Quantity ordered
    private double totalPrice;  // Calculated: product.price * quantity

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PENDING, COMPLETED, etc.

    // ✅ Getters and Setters
    public Long getId() { return id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
