package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.application.service.ProductService;
import com.lcdev.ecommerce.domain.enums.Size;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> insert(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO newDto = service.save(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductResponseDTO> update(@Valid @RequestBody ProductRequestDTO dto, @PathVariable Long id){
        ProductResponseDTO newDto = service.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductMinResponseDTO>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Size size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int sizePage,
            @RequestParam(defaultValue = "relevance") String sort
    ) {
        Sort sorting = switch (sort) {
            case "highestPrice" -> Sort.by(Sort.Direction.DESC, "finalPrice");
            case "lowestPrice" -> Sort.by(Sort.Direction.ASC, "finalPrice");
            case "biggestDiscount" -> Sort.by(Sort.Direction.DESC, "discountPercent");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };

        Pageable pageable = PageRequest.of(page, sizePage, sorting);

        Page<ProductMinResponseDTO> result = service.search(
                name, categoryId, minPrice, maxPrice, size, sort, pageable
        );

        return ResponseEntity.ok(PageResponse.from(result));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id){
        ProductResponseDTO dto  = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

}
