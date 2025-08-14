package com.lcdev.ecommerce.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserInsertDTO extends UserDTO{

    @NotBlank(message = "Campo obrigatório!")
    @Size(min = 8, message = "Deve ter no minímo 8 caracteres")
    private String password;

    UserInsertDTO(){
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
