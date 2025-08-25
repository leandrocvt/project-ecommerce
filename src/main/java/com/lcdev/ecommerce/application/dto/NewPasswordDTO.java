package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordDTO {

    @NotBlank(message = "Campo obrigatório.")
    private String token;

    @NotBlank(message = "Campo obrigatório.")
    @Size(min = 8, message = "Deve ter no minímo 8 caracteres")
    private String password;

}
