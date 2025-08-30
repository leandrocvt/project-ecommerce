package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBaseRequestDTO {

    @NotBlank(message = "Campo obrigatório!")
    private String firstName;

    @NotBlank(message = "Campo obrigatório!")
    private String lastName;

    @Email(message = "Informe um email válido!")
    private String email;

    @NotBlank(message = "Campo obrigatório!")
    private String phone;

    @NotNull(message = "Campo obrigatório!")
    private LocalDate birthDate;

    @NotBlank(message = "Campo obrigatório!")
    private String cpf;
}
