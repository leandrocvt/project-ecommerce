package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.OrderDTO;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.ProductVariation;
import com.lcdev.ecommerce.domain.entities.User;

import java.util.List;
import java.util.Map;

public interface OrderMapper {

    Order toEntity(CreateOrderRequest request, User user, List<ProductVariation> variations);
    OrderDTO toDTO(Order order, Map<Long, String> primaryImages);

}
