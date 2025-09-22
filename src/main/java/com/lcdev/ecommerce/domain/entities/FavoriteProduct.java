package com.lcdev.ecommerce.domain.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_favorite_product")
public class FavoriteProduct {

    @EmbeddedId
    private FavoriteProductPK id = new FavoriteProductPK();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variationId")
    @JoinColumn(name = "variation_id", insertable = false, updatable = false)
    private ProductVariation variation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public FavoriteProduct() {}


    public FavoriteProduct(User user, ProductVariation variation) {
        this.id = new FavoriteProductPK(variation.getId(), user.getId());
        this.user = user;
        this.variation = variation;
    }

    public FavoriteProductPK getId() {
        return id;
    }

    public void setId(FavoriteProductPK id) {
        this.id = id;
    }

    public ProductVariation getVariation() {
        return variation;
    }

    public void setVariation(ProductVariation variation) {
        this.variation = variation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FavoriteProduct that = (FavoriteProduct) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
