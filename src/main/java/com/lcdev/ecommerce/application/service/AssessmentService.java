package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.assessment.AssessmentRequestDTO;
import com.lcdev.ecommerce.application.dto.assessment.AssessmentResponseDTO;
import com.lcdev.ecommerce.application.service.exceptions.BusinessException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Assessment;
import com.lcdev.ecommerce.domain.entities.Product;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.mapper.AssessmentMapper;
import com.lcdev.ecommerce.infrastructure.projections.AssessmentProjection;
import com.lcdev.ecommerce.infrastructure.repositories.AssessmentRepository;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository repository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final AssessmentMapper mapper;

    @Transactional(readOnly = true)
    public Page<AssessmentResponseDTO> search(Long productId, Pageable pageable) {
        return repository.findByProductId(productId, pageable)
                .map(AssessmentResponseDTO::new);
    }

    @Transactional
    public AssessmentResponseDTO create(AssessmentRequestDTO dto) {
        User user = authService.authenticated();

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        if (repository.existsByUserAndProduct(user, product)) {
            throw new BusinessException("Você já avaliou este produto");
        }

        if (!orderRepository.existsByClientAndProductAndStatus(user.getId(), product.getId(), OrderStatus.DELIVERED)) {
            throw new BusinessException("Você só pode avaliar produtos que já comprou e recebeu.");
        }

        Assessment entity = mapper.toEntity(dto, user, product);
        repository.save(entity);

        return repository.findProjectionById(entity.getId())
                .map(AssessmentResponseDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Erro ao carregar avaliação criada"));
    }


}
