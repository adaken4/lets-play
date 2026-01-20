package com.github.adaken4.lets_play.dto;

public record ProductResponse(
        String id,
        String name,
        String description,
        Double price,
        String userId) {

}
