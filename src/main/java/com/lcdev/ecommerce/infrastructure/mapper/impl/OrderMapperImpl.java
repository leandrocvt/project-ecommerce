package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.application.service.AuthService;
import com.lcdev.ecommerce.application.service.UserService;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.mapper.OrderMapper;
import com.lcdev.ecommerce.infrastructure.mapper.UserMapper;
import com.lcdev.ecommerce.infrastructure.repositories.OrderItemRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationImageRepository;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {


    private final ProductVariationRepository variationRepository;

    @Override
    public Order toEntity(OrderDTO dto, User user, List<ProductVariation> variations) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setClient(user);

        Map<Long, ProductVariation> variationMap = variations.stream()
                .collect(Collectors.toMap(ProductVariation::getId, Function.identity()));

        Set<OrderItem> items = dto.getItems().stream()
                .map(itemDTO -> {
                    ProductVariation variation = variationMap.get(itemDTO.getVariationId());
                    if (Objects.isNull(variation)) {
                        throw new ResourceNotFoundException(
                                "Variação de produto não encontrada: " + itemDTO.getVariationId()
                        );
                    }
                    if (Boolean.FALSE.equals(variation.getProduct().getActive())) {
                        throw new ResourceNotFoundException(
                                "Produto inativo: " + variation.getProduct().getId()
                        );
                    }
                    return new OrderItem(order, variation, itemDTO.getQuantity(), variation.getFinalPrice());
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        order.setItems(items);
        return order;
    }

    @Override
    public OrderDTO toDTO(Order order, Map<Long, String> primaryImages) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    String imageUrl = primaryImages.get(item.getVariation().getId());
                    return new OrderItemDTO(item, imageUrl);
                })
                .toList();

        return new OrderDTO(order, itemDTOs);
    }

}
