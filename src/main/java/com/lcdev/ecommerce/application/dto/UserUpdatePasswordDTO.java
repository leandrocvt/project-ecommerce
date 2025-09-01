package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdatePasswordDTO {

    @NotBlank(message = "Campo obrigatório!")
    private String currentPassword;

    @NotBlank(message = "Campo obrigatório!")
    @Size(min = 8, message = "Deve ter no minímo 8 caracteres")
    private String newPassword;

}
