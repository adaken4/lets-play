package com.github.adaken4.lets_play.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.adaken4.lets_play.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
}