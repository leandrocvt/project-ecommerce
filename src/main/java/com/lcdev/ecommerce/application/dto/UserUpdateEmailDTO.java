package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateEmailDTO {


    @Email(message = "Informe um email válido!")
    private String email;


    @NotBlank(message = "Campo obrigatório!")
    @Size(min = 8, message = "Deve ter no minímo 8 caracteres")
    private String password;
}
