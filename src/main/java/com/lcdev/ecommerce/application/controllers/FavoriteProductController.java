package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.product.ProductMinResponseDTO;
import com.lcdev.ecommerce.application.service.FavoriteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "products/favorites")
public class FavoriteProductController {

    private final FavoriteProductService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<List<ProductMinResponseDTO>> listFavorites() {
        return ResponseEntity.ok(service.listFavorites());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping("/{variationId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long variationId) {
        service.addFavorite(variationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @DeleteMapping("/{variationId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long variationId) {
        service.removeFavorite(variationId);
        return ResponseEntity.noContent().build();
    }

}
