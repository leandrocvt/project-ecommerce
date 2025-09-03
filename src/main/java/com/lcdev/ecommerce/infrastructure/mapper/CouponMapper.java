package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.coupon.CouponRequestDTO;
import com.lcdev.ecommerce.application.dto.coupon.CouponResponseDTO;
import com.lcdev.ecommerce.domain.entities.Coupon;

public interface CouponMapper {

    Coupon toEntity(CouponRequestDTO dto);
    void updateEntity(CouponRequestDTO dto, Coupon entity);
    CouponResponseDTO toResponse(Coupon entity);

}
