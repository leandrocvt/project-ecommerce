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
    private Size size;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAdjustment;

    private Integer stockQuantity;

    @ElementCollection
    @CollectionTable(name = "tb_product_variation_images", joinColumns = @JoinColumn(name = "variation_id"))
    @Column(name = "img_url")
    private List<String> imgUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

}
