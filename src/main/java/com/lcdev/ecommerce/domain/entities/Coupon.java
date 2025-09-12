package com.lcdev.ecommerce.domain.entities;

import com.lcdev.ecommerce.domain.enums.CouponStatus;
import com.lcdev.ecommerce.domain.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal discountValue;

    private LocalDate validUntil;

    private Integer maxUses;
    private Integer currentUses = 0;

    private BigDecimal minPurchaseAmount;

    @OneToMany(mappedBy = "coupon")
    private List<Order> orders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    public boolean isValid(BigDecimal orderTotal) {
        return CouponStatus.ACTIVE.equals(this.status)
                && (Objects.isNull(this.validUntil) || !this.validUntil.isBefore(LocalDate.now()))
                && (Objects.isNull(this.minPurchaseAmount) || orderTotal.compareTo(this.minPurchaseAmount) >= 0)
                && (Objects.isNull(this.maxUses) || this.currentUses < this.maxUses);
    }

    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (!isValid(orderTotal)) {
            throw new IllegalArgumentException("Cupom inválido ou expirado");
        }

        if (DiscountType.PERCENTAGE.equals(discountType)) {
            return orderTotal.multiply(discountValue.divide(BigDecimal.valueOf(100)));
        }
        return discountValue.min(orderTotal);
    }

    public void useOnce() {
        if (maxUses != null && currentUses >= maxUses) {
            throw new IllegalStateException("Limite de uso atingido");
        }

        this.currentUses++;

        if (maxUses != null && currentUses >= maxUses) {
            this.status = CouponStatus.EXPIRED;
        }
    }

    public boolean isExpired() {
        return CouponStatus.EXPIRED.equals(this.status)
                || (validUntil != null && validUntil.isBefore(LocalDate.now()));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Coupon coupon = (Coupon) object;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
