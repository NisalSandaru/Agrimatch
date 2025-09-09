package com.Nisal.Agrimatch.repository;

import com.Nisal.Agrimatch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find all products by farmer email
    List<Product> findByFarmerEmail(String farmerEmail);

}
