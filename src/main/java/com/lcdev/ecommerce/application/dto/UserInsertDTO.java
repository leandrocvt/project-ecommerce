package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInsertDTO extends UserBaseRequestDTO {

    @NotBlank(message = "Campo obrigatório!")
    @Size(min = 8, message = "Deve ter no minímo 8 caracteres")
    private String password;

    @NotNull(message = "Endereço principal é obrigatório!")
    private AddressDTO address;

}
