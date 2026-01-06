package com.github.adaken4.lets_play.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @NotBlank
    private String name;
    private String description;

    @NotNull
    @PositiveOrZero
    private Double price;

    private String userId;
}
