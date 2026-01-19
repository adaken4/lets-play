package com.github.adaken4.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adaken4.lets_play.dto.ProductCreationRequest;
import com.github.adaken4.lets_play.dto.ProductResponse;
import com.github.adaken4.lets_play.dto.ProductUpdateRequest;
import com.github.adaken4.lets_play.model.Product;
import com.github.adaken4.lets_play.repository.ProductRepository;
import com.github.adaken4.lets_play.service.ProductService;
import com.github.adaken4.lets_play.service.CustomUserDetailsService.UserDetailsImpl;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // Public endpoint: returns all products in the store
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getUserId()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getUserId())))
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new product, associating it with the currently authenticated user
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreationRequest request,
            Authentication auth) {
        // Extract the custom user details (principal) from the authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        // Convert DTO to Product entity
        Product newProduct = new Product();
        newProduct.setName(request.name());
        newProduct.setDescription(request.description());
        newProduct.setPrice(request.price());
        // Delegate creation to the service, passing the current user's ID as
        // owner/creator
        Product savedProduct = productService.createProduct(newProduct, userDetails.getId());
        // Convert saved entity back to response DTO
        ProductResponse response = new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> partialUpdate(@PathVariable String id,
            @RequestBody ProductUpdateRequest request,
            Authentication auth) {
        // Get authenticated user info from the security context
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        // Delegate partial update logic to the service, including
        // ownership/authorization checks
        Product patchedProduct = productService.patchProduct(id, request, userDetails.getId());
        // Convert patched entity back to response DTO
        ProductResponse response = new ProductResponse(
                patchedProduct.getId(),
                patchedProduct.getName(),
                patchedProduct.getDescription(),
                patchedProduct.getPrice(),
                patchedProduct.getUserId());
        return ResponseEntity.ok(response);
    }

    // Delete a product by ID; authorization is enforced inside the service/method
    // security
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        // Return 204 No Content on successful deletion
        return ResponseEntity.noContent().build();
    }
}
