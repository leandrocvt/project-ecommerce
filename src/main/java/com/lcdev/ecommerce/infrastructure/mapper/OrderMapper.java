package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    OrderDTO toDTO(Order order, Map<Long, String> primaryImages);

}
