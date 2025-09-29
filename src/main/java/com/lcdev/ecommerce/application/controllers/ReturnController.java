package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.PageResponse;
import com.lcdev.ecommerce.application.dto.payback.ReturnCreateDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnResponseDTO;
import com.lcdev.ecommerce.application.dto.payback.ReturnSummaryDTO;
import com.lcdev.ecommerce.application.service.ReturnService;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.enums.ReturnStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ResponseEntity<ReturnResponseDTO> requestReturn(@RequestBody @Valid ReturnCreateDTO dto) {
        ReturnResponseDTO response = returnService.requestReturn(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReturnResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam ReturnStatus status
    ) {
        ReturnResponseDTO response = returnService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/by-order/{orderId}")
    public List<ReturnResponseDTO> findByOrderId(@PathVariable Long orderId) {
        return returnService.findByOrderId(orderId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnResponseDTO> findById(@PathVariable Long id) {
        return returnService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Devolução não encontrada"));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<ReturnSummaryDTO>> findAll(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) ReturnStatus status,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(required = false) String clientName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int sizePage
    ) {
        Pageable pageable = PageRequest.of(page, sizePage);
        Page<ReturnSummaryDTO> result = returnService.findAll(id, status, startDate, endDate, clientName, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }


}

