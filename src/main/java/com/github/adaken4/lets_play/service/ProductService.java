package com.github.adaken4.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.model.Product;
import com.github.adaken4.lets_play.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    // Creates a new product and associates it with the current authenticated user
    public Product createProduct(Product product, String currentUserId) {
        // Links the product to the user who created it (for ownership tracking)
        product.setUserId(currentUserId);
        return productRepository.save(product);
    }

    // Authorization helper method: checks if the given user owns the specified product
    public boolean isOwner(String productId, String userId) {
        return productRepository.findById(productId)
                // Returns true only if product exists AND current user matches product owner
                .map(product -> product.getUserId().equals(userId ))
                .orElse(false);
    }
}
