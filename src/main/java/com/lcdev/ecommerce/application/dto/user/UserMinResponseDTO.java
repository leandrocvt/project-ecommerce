package com.lcdev.ecommerce.application.dto.user;

public record UserMinResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email
) {}
