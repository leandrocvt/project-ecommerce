package com.lcdev.ecommerce.infrastructure.projections;

import java.math.BigDecimal;

public interface ProductMinProjection {
    Long getId();
    String getName();
    BigDecimal getBasePrice();
    BigDecimal getFinalPrice();
    BigDecimal getDiscountPercent();
    String getFirstImageUrl();
}
