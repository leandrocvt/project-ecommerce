package com.lcdev.ecommerce.application.dto;

import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.OrderItem;
import com.lcdev.ecommerce.domain.entities.ProductVariationImage;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
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

    public OrderDTO(Long id, Instant moment, OrderStatus status, ClientDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.client = client;
        this.payment = payment;
    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        status = entity.getStatus();
        client = entity.getClient() != null ? new ClientDTO(entity.getClient()) : null;
        payment = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        items = entity.getItems().stream()
                .map(item -> {
                    String imageUrl = item.getVariation().getImages().stream()
                            .filter(ProductVariationImage::isPrimary)
                            .findFirst()
                            .map(ProductVariationImage::getImgUrl)
                            .orElse(null);
                    return new OrderItemDTO(item, imageUrl);
                })
                .toList();
    }

    public OrderDTO(Order entity, List<OrderItemDTO> itemDTOs) {
        this(entity);
        this.items = itemDTOs;

        this.total = items.stream()
                .map(OrderItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
