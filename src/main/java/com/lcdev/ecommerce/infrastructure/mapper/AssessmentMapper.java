package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.assessment.AssessmentRequestDTO;
import com.lcdev.ecommerce.domain.entities.Assessment;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.User;

public interface AssessmentMapper {
    Assessment toEntity(AssessmentRequestDTO dto, User user, Product product);
}
