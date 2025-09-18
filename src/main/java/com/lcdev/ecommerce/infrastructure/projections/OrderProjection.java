package com.lcdev.ecommerce.infrastructure.projections;

import com.lcdev.ecommerce.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public interface OrderProjection {
    Long getId();
    Instant getMoment();
    OrderStatus getStatus();
    BigDecimal getSubtotal();
    BigDecimal getShippingCost();
    BigDecimal getDiscountApplied();
    String getClientName();
    Instant getPaymentMoment();
}
