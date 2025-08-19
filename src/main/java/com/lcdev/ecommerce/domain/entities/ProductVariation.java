package com.lcdev.ecommerce.domain.entities;

import com.lcdev.ecommerce.domain.enums.Color;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "tb_product_variation")
public class ProductVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private Size size;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAdjustment;

    @Column(name = "discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount;

    private Integer stockQuantity;

    @OneToMany(mappedBy = "variation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariationImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public BigDecimal getVariantPrice() {
        BigDecimal adj = priceAdjustment != null ? priceAdjustment : BigDecimal.ZERO;
        return product.getBasePrice().add(adj);
    }

    public BigDecimal getFinalPrice() {
        BigDecimal price = product.getBasePrice()
                .add(priceAdjustment != null ? priceAdjustment : BigDecimal.ZERO);
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        return price.subtract(discount).max(BigDecimal.ZERO);
    }


    public BigDecimal getDiscountPercent() {
        BigDecimal base = getVariantPrice();
        if (base.signum() == 0) return BigDecimal.ZERO;
        BigDecimal disc = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        return disc.multiply(new BigDecimal("100")).divide(base, 2, java.math.RoundingMode.HALF_UP);
    }

}
