package com.github.adaken4.lets_play.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.dto.ProductCreationRequest;
import com.github.adaken4.lets_play.dto.ProductResponse;
import com.github.adaken4.lets_play.dto.ProductUpdateRequest;
import com.github.adaken4.lets_play.exception.ForbiddenException;
import com.github.adaken4.lets_play.exception.ProductNotFoundException;
import com.github.adaken4.lets_play.exception.UserNotFoundException;
import com.github.adaken4.lets_play.model.Product;
import com.github.adaken4.lets_play.repository.ProductRepository;
import com.github.adaken4.lets_play.repository.UserRepository;

@Service
public class ProductService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Retrieves paginated list of all products
     * 
     * @param pageable
     * @return Page of ProductResponse objects
     */
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductMapper::toResponse);
    }

    /**
     * Retrieves a single product by its ID
     * 
     * @param id
     * @return
     */
    public Optional<ProductResponse> getProductById(String id) {
        return productRepository.findById(id).map(ProductMapper::toResponse);
    }

    /**
     * Creates a new product owned by the authenticated user
     * 
     * @param request
     * @param userId
     * @return Created ProductResponse object
     */
    public ProductResponse createProduct(ProductCreationRequest request, String userId) {
        // Ensure user exists before creating product
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Transform validated DTO -> Product entity
        Product product = ProductMapper.toEntity(request);

        // Generate globally unique product ID
        product.setId(UUID.randomUUID().toString());

        // Establish ownership relationship for authorization
        product.setUserId(userId);

        // Persist and return created product
        return ProductMapper.toResponse(productRepository.save(product));
    }

    /**
     * Partially updates a product if user is ADMIN or the product's owner
     * 
     * @param productId
     * @param request
     * @param userId
     * @param userRoles
     * @return Updated ProductResponse object
     */
    public ProductResponse updateProduct(String productId, ProductUpdateRequest request, String userId,
            String userRoles) {
        Product product = findAndAuthorizeProduct(productId, userId, userRoles);

        // Only non-null fields from request are applied
        ProductMapper.partialUpdate(product, request);
        return ProductMapper.toResponse(productRepository.save(product));
    }

}
