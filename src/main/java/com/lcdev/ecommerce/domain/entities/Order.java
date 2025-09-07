package com.lcdev.ecommerce.domain.entities;

import com.lcdev.ecommerce.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant moment;
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "id.order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "discount_applied", precision = 19, scale = 2)
    private BigDecimal discountApplied;

    private String shippingMethod;
    private BigDecimal shippingCost;
    private LocalDate shippingDeadline;

    public BigDecimal getSubtotal() {
        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        BigDecimal subtotal = getSubtotal();
        BigDecimal shipping = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        BigDecimal discount = discountApplied != null ? discountApplied : BigDecimal.ZERO;

        return subtotal
                .subtract(discount)
                .add(shipping)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void applyCoupon(Coupon coupon) {
        BigDecimal subtotal = getSubtotal();
        BigDecimal discount = coupon.calculateDiscount(subtotal)
                .setScale(2, RoundingMode.HALF_UP);

        this.coupon = coupon;
        this.discountApplied = discount;

        coupon.incrementUsage();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Order order = (Order) object;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
