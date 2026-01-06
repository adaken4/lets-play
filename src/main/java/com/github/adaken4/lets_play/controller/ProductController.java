package com.github.adaken4.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Create a new product, associating it with the currently authenticated user
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product, Authentication auth) {
        // Extract the custom user details (principal) from the authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        // Delegate creation to the service, passing the current user's ID as owner/creator
        return ResponseEntity.ok(productService.createProduct(product, userDetails.getId()));
    }
}
