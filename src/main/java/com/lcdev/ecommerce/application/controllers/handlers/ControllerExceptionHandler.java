package com.lcdev.ecommerce.application.controllers.handlers;

import com.lcdev.ecommerce.application.dto.CustomError;
import com.lcdev.ecommerce.application.dto.ValidationError;
import com.lcdev.ecommerce.application.service.exceptions.*;
import com.lcdev.ecommerce.infrastructure.payment.mercadopago.exceptions.MercadoPagoException;
import com.lcdev.ecommerce.infrastructure.payment.mercadopago.exceptions.PaymentException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> dataBase(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomError> business(BusinessException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InactiveProductException.class)
    public ResponseEntity<CustomError> inactiveProduct(InactiveProductException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomError> badRequest(BadRequestException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados Inválidos!", request.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<CustomError> email(EmailException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(Instant.now(), status.value(), "Email exception", request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MercadoPagoException.class)
    public ResponseEntity<CustomError> mercadoPago(MercadoPagoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_GATEWAY; // erro externo
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage() + " - detalhes: " + e.getDetails(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<CustomError> payment(PaymentException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler({DuplicateKeyException.class, org.hibernate.NonUniqueObjectException.class})
    public ResponseEntity<CustomError> duplicateKey(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                "Conflito de persistência: " + e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

}
