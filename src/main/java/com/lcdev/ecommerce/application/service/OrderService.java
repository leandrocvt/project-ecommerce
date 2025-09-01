package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.OrderDTO;
import com.lcdev.ecommerce.application.dto.OrderItemDTO;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.impl.OrderMapperImpl;
import com.lcdev.ecommerce.infrastructure.projections.ProductVariationImageProjection;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductVariationImageRepository imageRepository;
    private final ProductVariationRepository variationRepository;
    private final OrderMapperImpl orderMapper;
    private final AuthService authService;


    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        User user = authService.authenticated();

        List<Long> variationIds = dto.getItems().stream()
                .map(OrderItemDTO::getVariationId)
                .toList();

        List<ProductVariation> variations = variationRepository
                .findAllWithProductAndCategoryByIds(variationIds);

        Order order = orderMapper.toEntity(dto, user, variations);
        repository.save(order);

        Map<Long, String> primaryImages = imageRepository.findImagesByVariationIds(variationIds)
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getPrimary()))
                .collect(Collectors.toMap(
                        ProductVariationImageProjection::getVariationId,
                        ProductVariationImageProjection::getImgUrl
                ));

        return orderMapper.toDTO(order, primaryImages);
    }


}
