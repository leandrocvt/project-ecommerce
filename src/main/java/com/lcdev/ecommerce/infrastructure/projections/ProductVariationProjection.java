package com.lcdev.ecommerce.infrastructure.projections;

import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;

import java.math.BigDecimal;

public interface ProductVariationProjection {
    Long getProductId();
    String getName();
    String getDescription();
    BigDecimal getBasePrice();
    Long getCategoryId();
    Boolean getActive();

    Long getVariationId();
    Color getColor();
    Size getSize();
    BigDecimal getPriceAdjustment();
    BigDecimal getDiscountAmount();
    Integer getVariationStock();
}
