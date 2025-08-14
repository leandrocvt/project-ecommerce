package com.lcdev.ecommerce.application.dto;

import lombok.Builder;

@Builder
public record UserUpdateDTO(
        String firstName,
        String lastName,
        String email,
        String phone
) {}
