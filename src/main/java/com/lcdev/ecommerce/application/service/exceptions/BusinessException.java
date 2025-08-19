package com.lcdev.ecommerce.application.service.exceptions;

public class BusinessException extends RuntimeException{

    public BusinessException(String msg){
        super(msg);
    }
}

