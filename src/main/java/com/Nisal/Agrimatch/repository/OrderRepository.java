package com.Nisal.Agrimatch.repository;

import com.Nisal.Agrimatch.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders for a specific buyer
    List<Order> findByBuyerEmail(String buyerEmail);

    // ✅ Add this to support findByProductId
    List<Order> findByProductId(Long productId);
}
