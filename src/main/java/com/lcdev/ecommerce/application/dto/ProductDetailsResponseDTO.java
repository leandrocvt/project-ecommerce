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

}
