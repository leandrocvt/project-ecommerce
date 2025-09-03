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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {


    private final ProductVariationRepository variationRepository;

    @Override
    public Order toEntity(CreateOrderRequest request, User user, List<ProductVariation> variations) {
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setClient(user);

        Map<Long, ProductVariation> variationMap = variations.stream()
                .collect(Collectors.toMap(ProductVariation::getId, Function.identity()));

        Set<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    ProductVariation variation = variationMap.get(itemRequest.variationId());
                    if (Objects.isNull(variation)) {
                        throw new ResourceNotFoundException(
                                "Variação de produto não encontrada: " + itemRequest.variationId()
                        );
                    }
                    if (Boolean.FALSE.equals(variation.getProduct().getActive())) {
                        throw new ResourceNotFoundException(
                                "Produto inativo: " + variation.getProduct().getId()
                        );
                    }
                    return new OrderItem(order, variation, itemRequest.quantity(), variation.getFinalPrice());
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

        OrderDTO dto = new OrderDTO(order, itemDTOs);

        BigDecimal subtotal = itemDTOs.stream()
                .map(OrderItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        if (order.getCoupon() != null && order.getDiscountApplied() != null) {
            BigDecimal discount = order.getDiscountApplied().setScale(2, RoundingMode.HALF_UP);
            dto.setCouponCode(order.getCoupon().getCode());
            dto.setDiscountApplied(discount);

            BigDecimal finalTotal = subtotal.subtract(discount).max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            dto.setTotal(finalTotal);
        } else {
            dto.setTotal(subtotal);
        }

        return dto;
    }

}
