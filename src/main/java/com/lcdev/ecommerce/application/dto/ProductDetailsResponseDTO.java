package com.lcdev.ecommerce.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsResponseDTO extends ProductResponseDTO{

    private Double averageScore;
    private Long totalReviews;
    private List<AssessmentResponseDTO> sampleReviews;

    public ProductDetailsResponseDTO(Long id, String name, String description, BigDecimal basePrice, Long categoryId, Boolean active, Integer stockQuantity, List<ProductVariationDTO> variations, Double averageScore, Long totalReviews, List<AssessmentResponseDTO> sampleReviews) {
        super(id, name, description, basePrice, categoryId, active,  stockQuantity, variations);
        this.averageScore = averageScore;
        this.totalReviews = totalReviews;
        this.sampleReviews = sampleReviews;
    }

}
