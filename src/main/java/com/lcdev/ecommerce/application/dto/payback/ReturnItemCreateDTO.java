package com.lcdev.ecommerce.application.dto.payback;

public record ReturnItemCreateDTO(
        Long variationId,
        Integer quantity,
        String reason,
        String comment
) {}