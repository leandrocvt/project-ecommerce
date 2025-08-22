package com.lcdev.ecommerce.infrastructure.projections;

public interface ProductVariationImageProjection {
    Long getImageId();
    String getImgUrl();
    Boolean getPrimary();
    Long getVariationId();
}
