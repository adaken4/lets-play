package com.github.adaken4.lets_play.service;

import com.github.adaken4.lets_play.dto.ProductCreationRequest;
import com.github.adaken4.lets_play.dto.ProductResponse;
import com.github.adaken4.lets_play.dto.ProductUpdateRequest;
import com.github.adaken4.lets_play.model.Product;

/**
 * Utility class for mapping between Product entities and DTOs
 */
public class ProductMapper {

    /**
     * Maps Product entity to public-facing ProductResponse DTO
     * @param product
     * @return ProductResponse object
     */
    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getUserId() // Owner ID for frontend display/ownership info
        );
    }

    /**
     * Creates new Product entity from validated ProductCreationRequest DTO
     * @param request
     * @return Product entity
     */
    public static Product toEntity(ProductCreationRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        return product;
    }

    /**
     * Updates Product entity with non-null fields from ProductUpdateRequest DTO
     * @param product
     * @param request
     */
    public static void partialUpdate(Product product, ProductUpdateRequest request) {
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
    }
}
