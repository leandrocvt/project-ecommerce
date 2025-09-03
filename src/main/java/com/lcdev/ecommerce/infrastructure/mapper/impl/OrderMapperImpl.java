package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderItemDTO;
import com.lcdev.ecommerce.application.service.OrderFactory;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.infrastructure.mapper.OrderMapper;
import com.lcdev.ecommerce.infrastructure.repositories.ProductVariationRepository;
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

    @Override
    public OrderDTO toDTO(Order order, Map<Long, String> primaryImages) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item,
                        primaryImages.get(item.getVariation().getId())
                ))
                .toList();

        return new OrderDTO(order, itemDTOs);
    }

}
