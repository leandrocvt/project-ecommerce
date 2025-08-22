package com.lcdev.ecommerce.infrastructure.projections;

import java.time.LocalDateTime;

public interface    AssessmentProjection {
    Long getId();
    Double getScore();
    String getComment();
    String getPhotoUrl();
    String getUsername();
    LocalDateTime getCreatedAt();
}
