package com.lcdev.ecommerce.application.dto.product;

import com.lcdev.ecommerce.infrastructure.projections.ProductMinProjection;

import java.math.BigDecimal;

public record ProductMinResponseDTO(
        Long id,
        String name,
        BigDecimal basePrice,
        BigDecimal finalPrice,
        String firstImageUrl
) {
    public static ProductMinResponseDTO from(ProductMinProjection p) {
        return new ProductMinResponseDTO(
                p.getId(),
                p.getName(),
                p.getBasePrice(),
                p.getFinalPrice(),
                p.getFirstImageUrl()
        );
    }
}

