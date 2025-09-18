package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.domain.entities.Order;
import com.lcdev.ecommerce.domain.entities.Payment;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import com.lcdev.ecommerce.domain.enums.PaymentStatus;
import com.lcdev.ecommerce.infrastructure.repositories.OrderRepository;
import com.lcdev.ecommerce.infrastructure.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderExpirationService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Fallback: roda 1 vez por hora e cancela pedidos que não foram atualizados pelo webhook.
     */
    @Transactional
    @Scheduled(fixedRate = 120000) // 1h
    public void cancelExpiredOrders() {
        Instant now = Instant.now();

        // Busca apenas pedidos que ainda estão "travados" em WAITING_PAYMENT além do prazo
        List<Order> expiredOrders = orderRepository.findByStatusAndExpirationMomentBefore(
                OrderStatus.WAITING_PAYMENT, now
        );

        for (Order order : expiredOrders) {
            order.cancel();

            Payment payment = order.getPayment();
            if (payment != null && payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.EXPIRED);
                paymentRepository.save(payment);
            }

            orderRepository.save(order);
        }
    }
}
