package com.Nisal.Agrimatch.service;

import com.Nisal.Agrimatch.dto.ProductRequest;
import com.Nisal.Agrimatch.dto.ProductResponse;
import com.Nisal.Agrimatch.entity.Product;
import com.Nisal.Agrimatch.repository.ProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create a new product
    public ProductResponse addProduct(ProductRequest request) {
        String farmerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setFarmerEmail(farmerEmail);

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    // Get all products for the logged-in farmer
    public List<ProductResponse> getFarmerProducts() {
        String farmerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return productRepository.findByFarmerEmail(farmerEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update a product by id
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        String farmerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Ensure the farmer owns this product
        if (!product.getFarmerEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product updated = productRepository.save(product);
        return mapToResponse(updated);
    }

    // Delete a product by id
    public void deleteProduct(Long productId) {
        String farmerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Ensure the farmer owns this product
        if (!product.getFarmerEmail().equals(farmerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        productRepository.delete(product);
    }

    // Map Product entity to ProductResponse DTO
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setFarmerEmail(product.getFarmerEmail());
        return response;
    }

    public List<ProductResponse> getAllAvailableProducts() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getQuantity() > 0) // only show products with stock
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

}
