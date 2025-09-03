package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.CouponRequestDTO;
import com.lcdev.ecommerce.application.dto.CouponResponseDTO;
import com.lcdev.ecommerce.domain.entities.Coupon;
import com.lcdev.ecommerce.domain.enums.CouponStatus;
import com.lcdev.ecommerce.infrastructure.mapper.CouponMapper;
import org.springframework.stereotype.Component;

@Component
public class CouponMapperImpl implements CouponMapper {

    @Override
    public Coupon toEntity(CouponRequestDTO dto) {
        return Coupon.builder()
                .code(dto.code())
                .discountType(dto.discountType())
                .discountValue(dto.discountValue())
                .validUntil(dto.validUntil())
                .maxUses(dto.maxUses())
                .minPurchaseAmount(dto.minPurchaseAmount())
                .status(dto.status() != null ? dto.status() : CouponStatus.ACTIVE)
                .currentUses(0)
                .build();
    }

    @Override
    public void updateEntity(CouponRequestDTO dto, Coupon entity) {
        if (dto.discountType() != null) entity.setDiscountType(dto.discountType());
        if (dto.discountValue() != null) entity.setDiscountValue(dto.discountValue());
        if (dto.validUntil() != null) entity.setValidUntil(dto.validUntil());
        if (dto.maxUses() != null) entity.setMaxUses(dto.maxUses());
        if (dto.minPurchaseAmount() != null) entity.setMinPurchaseAmount(dto.minPurchaseAmount());
        if (dto.status() != null) entity.setStatus(dto.status());
    }

    @Override
    public CouponResponseDTO toResponse(Coupon entity) {
        return CouponResponseDTO.fromEntity(entity);
    }

}
