package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.PageResponse;
import com.lcdev.ecommerce.application.dto.coupon.CouponResponseDTO;
import com.lcdev.ecommerce.application.dto.order.CreateOrderRequest;
import com.lcdev.ecommerce.application.dto.order.OrderDTO;
import com.lcdev.ecommerce.application.dto.order.OrderResponseDTO;
import com.lcdev.ecommerce.application.dto.order.OrderSummaryDTO;
import com.lcdev.ecommerce.application.service.OrderService;
import com.lcdev.ecommerce.domain.enums.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findOrderByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(service.findOrderByIdForAdmin(id));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDTO>> findAll(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(required = false) String clientName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int sizePage
    ) {
        Pageable pageable = PageRequest.of(page, sizePage);
        Page<OrderResponseDTO> result = service.findAll(id, status, startDate, endDate, clientName, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

}
