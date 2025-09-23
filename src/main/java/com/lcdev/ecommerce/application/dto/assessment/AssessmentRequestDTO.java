package com.lcdev.ecommerce.application.dto.assessment;

public record AssessmentRequestDTO(
        Long productId,
        Double score,
        String comment,
        String photoUrl
) {
}
