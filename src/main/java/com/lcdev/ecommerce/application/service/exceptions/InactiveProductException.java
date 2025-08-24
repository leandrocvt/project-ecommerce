package com.lcdev.ecommerce.application.service.exceptions;

public class InactiveProductException extends RuntimeException{

    public InactiveProductException(String msg){
        super(msg);
    }
}
