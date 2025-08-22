package com.lcdev.ecommerce.infrastructure.projections;

public interface ReviewSummaryProjection {
    Double getAverageScore();
    Long getTotalReviews();
}
