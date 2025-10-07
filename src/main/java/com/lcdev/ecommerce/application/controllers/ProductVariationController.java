package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.product.ProductVariationRequestDTO;
import com.lcdev.ecommerce.application.dto.product.ProductVariationResponseDTO;
import com.lcdev.ecommerce.application.dto.product.ProductVariationUpdateDTO;
import com.lcdev.ecommerce.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/variations")
public class ProductVariationController {

    private final ProductService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductVariationResponseDTO> addVariation(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariationRequestDTO dto) {

        ProductVariationResponseDTO newDto = service.addVariation(productId, dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{variationId}")
    public ResponseEntity<ProductVariationResponseDTO> updateVariation(
            @PathVariable Long productId,
            @PathVariable Long variationId,
            @Valid @RequestBody ProductVariationUpdateDTO dto) {

        ProductVariationResponseDTO updated = service.updateVariation(productId, variationId, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{variationId}")
    public ResponseEntity<Void> deleteVariation(
            @PathVariable Long productId,
            @PathVariable Long variationId) {

        service.deleteVariation(productId, variationId);
        return ResponseEntity.noContent().build();
    }
}

