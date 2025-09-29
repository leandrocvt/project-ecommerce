package com.lcdev.ecommerce.domain.factories;

import com.lcdev.ecommerce.application.dto.payback.ReturnItemCreateDTO;
import com.lcdev.ecommerce.domain.entities.*;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

@Component
public class ReturnFactory {

    public ReturnRequest createRequest(Order order, User user, Instant deadline) {
        ReturnRequest request = new ReturnRequest();
        request.setOrder(order);
        request.setUser(user);
        request.setDeadline(deadline);
        request.setRequestedAt(Instant.now());
        request.setStatus(ReturnStatus.REQUESTED);
        request.setItems(new ArrayList<>());
        return request;
    }

    public ReturnItem createItem(ReturnRequest request, OrderItem orderItem, ReturnItemCreateDTO dto) {
        ReturnItem item = new ReturnItem();
        item.setReturnRequest(request);
        item.setOrderItem(orderItem);
        item.setQuantity(dto.quantity());
        item.setReason(dto.reason());
        item.setComment(dto.comment());
        item.setStatus(ReturnStatus.REQUESTED);

        BigDecimal unitPrice = orderItem.getVariation().getFinalPrice();
        item.setRefundAmount(unitPrice.multiply(BigDecimal.valueOf(dto.quantity())));
        return item;
    }
}

