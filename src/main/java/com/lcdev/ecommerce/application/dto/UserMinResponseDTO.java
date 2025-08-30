package com.lcdev.ecommerce.application.dto;

public record UserMinResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email
) {}
