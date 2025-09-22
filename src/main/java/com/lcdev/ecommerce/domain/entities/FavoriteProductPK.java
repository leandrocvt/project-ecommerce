package com.lcdev.ecommerce.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavoriteProductPK implements Serializable {
    @Column(name = "variation_id")
    private Long variationId;

    @Column(name = "user_id")
    private Long userId;

    public FavoriteProductPK() {}

    public FavoriteProductPK(Long variationId, Long userId) {
        this.variationId = variationId;
        this.userId = userId;
    }

    public Long getVariationIdId() {
        return variationId;
    }

    public void setVariationIdId(Long variationId) {
        this.variationId = variationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteProductPK that)) return false;
        return Objects.equals(variationId, that.variationId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variationId, userId);
    }
}

