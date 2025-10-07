package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.product.ProductVariationImageDTO;
import com.lcdev.ecommerce.application.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/variations/{variationId}/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProductVariationImageDTO>> uploadImages(
            @PathVariable Long productId,
            @PathVariable Long variationId,
            @RequestPart List<MultipartFile> files
    ) {
        return ResponseEntity.ok(productImageService.uploadImages(productId, variationId, files));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
