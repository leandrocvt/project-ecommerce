package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderSummaryDTO;
import com.lcdev.ecommerce.application.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderDTO> insert(@Valid @RequestBody CreateOrderRequest request) {
        OrderDTO dto = service.insert(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("/me") public ResponseEntity<List<OrderSummaryDTO>> getMyOrders() {
        return ResponseEntity.ok(service.findMyOrders());
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<OrderDTO> getMyOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findMyOrderById(id));
    }

}
