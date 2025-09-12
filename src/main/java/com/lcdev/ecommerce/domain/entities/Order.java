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

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant moment;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @Column(name = "expiration_moment", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE", nullable = true)
    private Instant expirationMoment;

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
        if (coupon == null) {
            throw new IllegalArgumentException("Cupom não pode ser nulo");
        }

        BigDecimal subtotal = getSubtotal();

        if (!coupon.isValid(subtotal)) {
            throw new IllegalArgumentException("Cupom inválido ou expirado");
        }

        BigDecimal discount = coupon.calculateDiscount(subtotal)
                .setScale(2, RoundingMode.HALF_UP);

        this.coupon = coupon;
        this.discountApplied = discount;

        coupon.useOnce();
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELED) {
            return;
        }
        this.status = OrderStatus.CANCELED;
        restoreStock();
    }

    private void restoreStock() {
        for (OrderItem item : this.items) {
            item.getVariation().increaseStock(item.getQuantity());
        }
    }

    public void addItem(ProductVariation variation, int quantity) {
        if (variation == null) {
            throw new IllegalArgumentException("Variação não pode ser nula");
        }
        if (Boolean.FALSE.equals(variation.getProduct().getActive())) {
            throw new IllegalStateException("Produto inativo: " + variation.getProduct().getId());
        }
        if (variation.getStockQuantity() < quantity) {
            throw new IllegalStateException("Produto " + variation.getProduct().getName() + " sem estoque suficiente");
        }

        variation.decreaseStock(quantity);
        OrderItem item = new OrderItem(this, variation, quantity, variation.getFinalPrice());
        this.items.add(item);
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
