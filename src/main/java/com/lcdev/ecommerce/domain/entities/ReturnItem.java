package com.lcdev.ecommerce.domain.entities;

import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_return_item")
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "order_id", referencedColumnName = "order_id"),
            @JoinColumn(name = "variation_id", referencedColumnName = "variation_id")
    })
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    private Integer quantity;

    private String reason;
    private String comment;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status = ReturnStatus.REQUESTED;

    private BigDecimal refundAmount;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ReturnItem that = (ReturnItem) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
