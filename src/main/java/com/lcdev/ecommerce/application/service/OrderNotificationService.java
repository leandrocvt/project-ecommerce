package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.message.EmailMessageDTO;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.messaging.rabbitmq.EmailProducer;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final UserRepository userRepository;
    private final EmailProducer emailProducer;

    @Transactional
    public void notifyDeliveredOrder(Long orderId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String link = "https://meufront/review?orderId=" + orderId;
        String text = "Olá, " + user.getFirstName() + " " + user.getLastName() + "!\n\n"
                + "Seu pedido #" + orderId + " foi entregue. "
                + "Gostaríamos de saber sua opinião!\n\n"
                + "Avalie os produtos acessando: " + link;

        EmailMessageDTO email = new EmailMessageDTO(
                user.getEmail(),
                "Avalie seu pedido #" + orderId,
                text
        );

        emailProducer.sendToQueue(email);
    }
}
