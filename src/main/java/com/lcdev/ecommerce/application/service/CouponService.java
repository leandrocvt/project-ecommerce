package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.coupon.CouponRequestDTO;
import com.lcdev.ecommerce.application.dto.coupon.CouponResponseDTO;
import com.lcdev.ecommerce.application.dto.coupon.CouponValidationResponseDTO;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Coupon;
import com.lcdev.ecommerce.domain.enums.CouponStatus;
import com.lcdev.ecommerce.infrastructure.mapper.CouponMapper;
import com.lcdev.ecommerce.infrastructure.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository repository;
    private final CouponMapper mapper;

    @Transactional
    public CouponResponseDTO save(CouponRequestDTO dto) {
        repository.findByCode(dto.code()).ifPresent(c -> {
            throw new BadRequestException("Cupom com este código já existe.");
        });

        Coupon entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public CouponResponseDTO update(Long id, CouponRequestDTO dto) {
        Coupon entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado."));

        mapper.updateEntity(dto, entity);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public CouponResponseDTO findById(Long id) {
        Coupon entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado."));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<CouponResponseDTO> findAll(String code, Pageable pageable) {
        return repository.search(code, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cupom não encontrado.");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CouponValidationResponseDTO validateCoupon(String code, BigDecimal purchaseAmount) {
        Coupon coupon = repository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom não encontrado"));

        if (!CouponStatus.ACTIVE.equals(coupon.getStatus())) {
            return new CouponValidationResponseDTO(false, "Cupom inativo", null, null);
        }

        if (coupon.getValidUntil().isBefore(LocalDate.now())) {
            return new CouponValidationResponseDTO(false, "Cupom expirado", null, null);
        }

        if (coupon.getCurrentUses() >= coupon.getMaxUses()) {
            return new CouponValidationResponseDTO(false, "Limite de uso atingido", null, null);
        }

        if (purchaseAmount.compareTo(coupon.getMinPurchaseAmount()) < 0) {
            return new CouponValidationResponseDTO(false, "Valor mínimo não atingido", null, null);
        }

        return new CouponValidationResponseDTO(true, "Cupom válido",
                coupon.getDiscountValue(), coupon.getDiscountType());
    }

}
