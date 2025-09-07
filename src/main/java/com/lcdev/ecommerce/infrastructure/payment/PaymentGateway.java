package com.lcdev.ecommerce.infrastructure.payment;

import com.lcdev.ecommerce.application.dto.payment.PaymentRequest;
import com.lcdev.ecommerce.application.dto.payment.PaymentResponse;
import com.lcdev.ecommerce.application.dto.payment.PaymentStatusResponse;
import com.lcdev.ecommerce.domain.entities.Order;

public interface PaymentGateway {
    PaymentResponse authorize(PaymentRequest request);
    PaymentResponse create(Order order, PaymentRequest request);
    PaymentStatusResponse checkStatus(String transactionId);
}