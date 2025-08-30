package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;

    @NotBlank(message = "Campo obrigatório!")
    private String road;

    @NotBlank(message = "Campo obrigatório!")
    private String neighborhood;

    @NotBlank(message = "Campo obrigatório!")
    private String city;

    @NotBlank(message = "Campo obrigatório!")
    private String state;

    @NotBlank(message = "Campo obrigatório!")
    private String zipCode;

    private String number;

    private String complement;
}
