package com.lcdev.ecommerce.infrastructure.messaging.listener;

import com.lcdev.ecommerce.application.dto.order.OrderDeliveredEvent;
import com.lcdev.ecommerce.application.service.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderNotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        notificationService.notifyDeliveredOrder(event.orderId(), event.userId());
    }

}