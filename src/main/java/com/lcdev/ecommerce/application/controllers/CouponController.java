package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.CouponRequestDTO;
import com.lcdev.ecommerce.application.dto.CouponResponseDTO;
import com.lcdev.ecommerce.application.dto.CouponValidationResponseDTO;
import com.lcdev.ecommerce.application.dto.PageResponse;
import com.lcdev.ecommerce.application.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/coupon")
public class CouponController {

    private final CouponService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<CouponResponseDTO> insert(@Valid @RequestBody CouponRequestDTO dto) {
        CouponResponseDTO newDto = service.save(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(newDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    @GetMapping("/validate")
    public ResponseEntity<CouponValidationResponseDTO> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal purchaseAmount) {

        CouponValidationResponseDTO result = service.validateCoupon(code, purchaseAmount);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<CouponResponseDTO> update(
            @Valid @RequestBody CouponRequestDTO dto,
            @PathVariable Long id
    ) {
        CouponResponseDTO newDto = service.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<CouponResponseDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int sizePage
    ) {
        Pageable pageable = PageRequest.of(page, sizePage);
        Page<CouponResponseDTO> result = service.findAll(pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
