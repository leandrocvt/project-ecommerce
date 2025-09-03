package com.lcdev.ecommerce.application.dto.coupon;

import com.lcdev.ecommerce.domain.enums.CouponStatus;
import com.lcdev.ecommerce.domain.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponRequestDTO(
        @NotBlank String code,
        @NotNull DiscountType discountType,
        @NotNull BigDecimal discountValue,
        LocalDate validUntil,
        Integer maxUses,
        BigDecimal minPurchaseAmount,
        CouponStatus status
) {}