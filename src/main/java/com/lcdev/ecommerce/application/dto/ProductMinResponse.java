package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.infrastructure.projections.ProductMinProjection;

import java.math.BigDecimal;

public record ProductMinResponse(
        Long id,
        String name,
        BigDecimal basePrice,
        BigDecimal finalPrice,
        String firstImageUrl
) {
    public static ProductMinResponse from(ProductMinProjection p) {
        return new ProductMinResponse(
                p.getId(),
                p.getName(),
                p.getBasePrice(),
                p.getFinalPrice(),
                p.getFirstImageUrl()
        );
    }
}

