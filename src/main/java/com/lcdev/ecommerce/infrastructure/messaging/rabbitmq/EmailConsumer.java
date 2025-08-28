package com.lcdev.ecommerce.infrastructure.messaging.rabbitmq;

import com.lcdev.ecommerce.application.dto.EmailMessageDTO;
import com.lcdev.ecommerce.application.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveMessage(EmailMessageDTO message) {
        emailService.sendEmail(
                message.getTo(),
                message.getSubject(),
                message.getBody()
        );
    }
}
