package com.lcdev.ecommerce.application.dto.user;

import lombok.Builder;

@Builder
public record UserUpdateDTO(
        String firstName,
        String lastName,
        String phone
) {}
