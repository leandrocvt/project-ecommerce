package com.lcdev.ecommerce.infrastructure.messaging.rabbitmq;

import com.lcdev.ecommerce.application.dto.message.EmailMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProducer {


    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    public void sendToQueue(EmailMessageDTO message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
