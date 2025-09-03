package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.enums.DiscountType;

import java.math.BigDecimal;

public record CouponValidationResponseDTO(
        Boolean valid,
        String message,
        BigDecimal discountValue,
        DiscountType discountType
) { }
