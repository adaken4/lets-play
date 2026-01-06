package com.github.adaken4.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Updates a product if user is ADMIN or the product's owner
    @PreAuthorize("hasRole('ADMIN') or @productService.isOwner(#productId, authentication.principal.id)")
    public Product updateProduct(String productId, Product updatedProduct, String userId) {
        // Fetch existing product or throw exception if not found
        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Update only the mutable fields from the request
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        
        return productRepository.save(existing);
    }

    // Deletes a product if user is ADMIN or the product's owner
    @PreAuthorize("hasRole('ADMIN') or @productService.isOwner(#productId, authentication.principal.id)")
    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }

    // Authorization helper method: checks if the given user owns the specified product
    public boolean isOwner(String productId, String userId) {
        return productRepository.findById(productId)
                // Returns true only if product exists AND current user matches product owner
                .map(product -> product.getUserId().equals(userId ))
                .orElse(false);
    }
}
