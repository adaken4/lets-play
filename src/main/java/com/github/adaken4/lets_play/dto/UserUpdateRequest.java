package com.github.adaken4.lets_play.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        String name,
        @Email String email,
        @Size(min = 6, max = 40) String password) {

}
