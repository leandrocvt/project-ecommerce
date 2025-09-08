package com.lcdev.ecommerce.application.dto.order;

import com.lcdev.ecommerce.application.dto.payment.PaymentDTO;
import com.lcdev.ecommerce.application.dto.user.ClientDTO;
import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    private ClientDTO client;
    private PaymentDTO payment;
    @NotEmpty(message = "Deve ter pelo menos um item.")
    private List<OrderItemDTO> items = new ArrayList<>();
    private BigDecimal total;
    private String couponCode;
    private BigDecimal discountApplied;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private LocalDate shippingDeadline;
    private String pixQrCode;

    public OrderDTO(Order entity, List<OrderItemDTO> itemDTOs) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.status = entity.getStatus();
        this.client = entity.getClient() != null ? new ClientDTO(entity.getClient()) : null;
        this.payment = entity.getPayment() != null ? new PaymentDTO(entity.getPayment()) : null;
        this.items = itemDTOs;
        this.couponCode = entity.getCoupon() != null ? entity.getCoupon().getCode() : null;
        this.discountApplied = entity.getDiscountApplied();
        this.shippingMethod = entity.getShippingMethod();
        this.shippingCost = entity.getShippingCost();
        this.shippingDeadline = entity.getShippingDeadline();
        this.total = entity.getTotal();
    }

}
