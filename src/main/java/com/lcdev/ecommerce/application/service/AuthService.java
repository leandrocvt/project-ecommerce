package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.EmailMessageDTO;
import com.lcdev.ecommerce.application.dto.NewPasswordDTO;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.PasswordRecover;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.messaging.rabbitmq.EmailProducer;
import com.lcdev.ecommerce.infrastructure.repositories.PasswordRecoverRepository;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    private final PasswordEncoder passwordEncoder;

    private final PasswordRecoverRepository passwordRecoverRepository;

    private final EmailProducer emailProducer;

    public void createRecoverToken(String email) {

        User user = userRepository.findByEmail(email);
        if (Objects.isNull(user)) {
            throw new ResourceNotFoundException("Email não encontrado!");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(email);
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        passwordRecoverRepository.save(entity);

        String text = "Acesse o link para definir uma nova senha\n\n"
                + recoverUri + token
                + ". Validade de " + tokenMinutes + " minutos";


        EmailMessageDTO emailMessage = new EmailMessageDTO(email, "Recuperação de senha", text);
        emailProducer.sendToQueue(emailMessage);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {

        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());
        if (result.isEmpty()){
            throw new ResourceNotFoundException("Token inválido!");
        }

        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

}
