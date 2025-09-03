package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.Coupon;
import com.lcdev.ecommerce.domain.enums.CouponStatus;
import com.lcdev.ecommerce.domain.enums.DiscountType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


public record CouponResponseDTO(
        Long id,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        LocalDate validUntil,
        Integer maxUses,
        Integer currentUses,
        BigDecimal minPurchaseAmount,
        CouponStatus status,
        String formattedDiscount
) {
    public static CouponResponseDTO fromEntity(Coupon entity) {
        return new CouponResponseDTO(
                entity.getId(),
                entity.getCode(),
                entity.getDiscountType(),
                entity.getDiscountValue(),
                entity.getValidUntil(),
                entity.getMaxUses(),
                entity.getCurrentUses(),
                entity.getMinPurchaseAmount(),
                entity.getStatus(),
                formatDiscount(entity.getDiscountType(), entity.getDiscountValue())
        );
    }

    private static String formatDiscount(DiscountType type, BigDecimal value) {
        if (type == null || value == null) return null;
        return switch (type) {
            case PERCENTAGE -> value.stripTrailingZeros().toPlainString() + "%";
            case FIXED_VALUE -> "R$ " + value.setScale(2, RoundingMode.HALF_UP);
        };
    }
}

