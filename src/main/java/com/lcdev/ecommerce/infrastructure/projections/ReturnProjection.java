package com.lcdev.ecommerce.infrastructure.projections;

import com.lcdev.ecommerce.domain.enums.ReturnStatus;

import java.math.BigDecimal;
import java.time.Instant;

public interface ReturnProjection {
    Long getId();
    Instant getCreatedAt();
    ReturnStatus getStatus();
    String getClientName();
    Long getOrderId();
    BigDecimal getTotalRefund();
}