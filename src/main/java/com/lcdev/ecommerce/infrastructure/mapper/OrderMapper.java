package com.lcdev.ecommerce.infrastructure.mapper;

import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderResponseDTO;
import com.lcdev.ecommerce.domain.entities.Order;
import org.aspectj.weaver.ast.Or;

import java.util.Map;

public interface OrderMapper {
    OrderDTO toDTO(Order order, Map<Long, String> primaryImages);

}
