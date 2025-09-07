package com.lcdev.ecommerce.domain.entities;

import com.lcdev.ecommerce.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant moment;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionId;

    private String gateway;

    private String paymentMethod;

    private String cardBrand;

    @OneToOne
    @MapsId
    private Order order;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Payment payment = (Payment) object;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
