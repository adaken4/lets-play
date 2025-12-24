package com.github.adaken4.lets_play.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.adaken4.lets_play.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}