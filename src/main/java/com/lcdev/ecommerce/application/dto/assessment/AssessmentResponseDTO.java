package com.lcdev.ecommerce.application.dto.assessment;

import com.lcdev.ecommerce.infrastructure.projections.AssessmentProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDTO {
    private Long id;
    private Double score;
    private String comment;
    private String photoUrl;
    private String userName;
    private LocalDateTime createdAt;

    public AssessmentResponseDTO(AssessmentProjection projection) {
        id = projection.getId();
        score = projection.getScore();
        comment = projection.getComment();
        photoUrl = projection.getPhotoUrl();
        userName = projection.getUsername();
        createdAt = projection.getCreatedAt();
    }

}

