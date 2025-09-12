package com.lcdev.ecommerce.application.service.payment;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.domain.enums.PaymentMethod;

public interface PaymentProcessor {
    boolean supports(PaymentMethod method);
    OrderDTO process(CreateOrderRequest request, User user);
}
