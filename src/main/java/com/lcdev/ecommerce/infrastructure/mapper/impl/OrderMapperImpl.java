package com.lcdev.ecommerce.infrastructure.mapper.impl;

import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderItemDTO;
import com.lcdev.ecommerce.application.dto.order.OrderResponseDTO;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
