package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.application.dto.assessment.AssessmentRequestDTO;
import com.lcdev.ecommerce.application.dto.assessment.AssessmentResponseDTO;
import com.lcdev.ecommerce.application.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/reviews")
public class AssessmentController {

    private final AssessmentService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping("/{productId}")
    public ResponseEntity<PageResponse<AssessmentResponseDTO>> findAll(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int sizePage,
            @RequestParam(defaultValue = "all") String sort
    ) {
        Sort sorting = switch (sort) {
            case "highestRating" -> Sort.by(Sort.Direction.DESC, "score");
            case "lowestRating"  -> Sort.by(Sort.Direction.ASC, "score");
            case "latest"        -> Sort.by(Sort.Direction.DESC, "createdAt");
            default              -> Sort.by(Sort.Direction.DESC, "id");
        };

        Pageable pageable = PageRequest.of(page, sizePage, sorting);

        Page<AssessmentResponseDTO> result = service.search(productId, pageable);

        return ResponseEntity.ok(PageResponse.from(result));
    }


    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PostMapping
    public ResponseEntity<AssessmentResponseDTO> create(@RequestBody @Valid AssessmentRequestDTO dto) {
        AssessmentResponseDTO response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
