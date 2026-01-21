package com.github.adaken4.lets_play.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.adaken4.lets_play.dto.ProductCreationRequest;
import com.github.adaken4.lets_play.dto.ProductResponse;
import com.github.adaken4.lets_play.service.ProductService;
import com.github.adaken4.lets_play.service.CustomUserDetailsService.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * GET /api/products
     * Public endpoint: returns paginated products from the store
     * 
     * Supports standard Srping Data query parameters:
     * ?page=0&size=20&sort=name,asc
     * ?size=10
     * ?page=1&sort=price,desc
     * 
     * Defaults to page 0, size 20 if unspecified
     * 
     * @param pageable
     * @return Page of ProductResponse
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@PageableDefault(size = 20) Pageable pageable) {
        // Delegates to service for paginated product listing
        // Returns Page<ProductResponse> with metadata (totalElements, totalPages, etc.)
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    /**
     * GET /api/products/{id}
     * Public endpoint: returns a single product by ID, if found
     *
     * @param id
     * @return ProductResponse if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        // Delegates to service, automatically handles Optional<ProductResponse>
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/products
     * Protected endpoint: creates a new product, owned by the authenticated user
     * @param request
     * @param auth
     * @return Created ProductResponse with Location header
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreationRequest request,
            Authentication auth) {
        // Extract authenticated user ID from Spring Security context
        String userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        // Calls service to create product, passing the user ID to associate ownership
        ProductResponse response = productService.createProduct(request, userId);
        return ResponseEntity.created(location(response.id())).body(response);
    }

    private URI location(String id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();
    }

}
