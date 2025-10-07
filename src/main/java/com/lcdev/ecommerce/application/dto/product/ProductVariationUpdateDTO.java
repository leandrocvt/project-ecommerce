package com.lcdev.ecommerce.application.dto.product;

import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductVariationUpdateDTO(
        Color color,
        Size size,
        @PositiveOrZero BigDecimal priceAdjustment,
        @PositiveOrZero BigDecimal discountAmount,
        @PositiveOrZero Integer stockQuantity
) {}
