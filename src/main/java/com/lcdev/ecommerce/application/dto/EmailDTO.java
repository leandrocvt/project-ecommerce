package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    @NotBlank(message = "Campo obrigatório")
    @Email(message = "Email inválido")
    private String email;

}
