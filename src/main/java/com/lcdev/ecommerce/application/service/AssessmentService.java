package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.AssessmentResponseDTO;
import com.lcdev.ecommerce.infrastructure.repositories.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository repository;

    @Transactional(readOnly = true)
    public Page<AssessmentResponseDTO> search(Long productId, Pageable pageable) {
        return repository.findByProductId(productId, pageable)
                .map(AssessmentResponseDTO::new);
    }

}
