package com.github.adaken4.lets_play.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.dto.ProductResponse;
import com.github.adaken4.lets_play.repository.ProductRepository;

@Service
public class ProductService {

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

}
