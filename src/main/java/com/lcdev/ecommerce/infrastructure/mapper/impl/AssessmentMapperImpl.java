package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.assessment.AssessmentRequestDTO;
import com.lcdev.ecommerce.domain.entities.Assessment;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.AssessmentMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AssessmentMapperImpl implements AssessmentMapper {

    @Override
    public Assessment toEntity(AssessmentRequestDTO dto, User user, Product product) {
        Assessment entity = new Assessment();
        entity.setProduct(product);
        entity.setUser(user);
        entity.setScore(dto.score());
        entity.setComment(dto.comment());
        entity.setPhotoUrl(dto.photoUrl());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
