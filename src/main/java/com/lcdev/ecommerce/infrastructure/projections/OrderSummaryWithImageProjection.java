package com.lcdev.ecommerce.infrastructure.projections;

import com.lcdev.ecommerce.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public interface OrderSummaryWithImageProjection {
    Long getId();
    Instant getMoment();
    OrderStatus getStatus();
    String getPaymentMethod();
    BigDecimal getTotal();
    String getImgUrl();
}
