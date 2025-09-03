package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.auth.NewPasswordDTO;
import com.lcdev.ecommerce.application.dto.auth.RecoverTokenRequest;
import com.lcdev.ecommerce.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody RecoverTokenRequest body){
        authService.createRecoverToken(body.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO body){
        authService.saveNewPassword(body);
        return ResponseEntity.noContent().build();
    }

}
