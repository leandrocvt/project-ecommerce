package com.lcdev.ecommerce.application.service.exceptions;

public class EmailException extends RuntimeException{

    public EmailException(String msg) {
        super(msg);
    }
}
