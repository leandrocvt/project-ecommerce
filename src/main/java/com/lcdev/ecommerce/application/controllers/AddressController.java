package com.lcdev.ecommerce.application.controllers;

import com.lcdev.ecommerce.application.dto.address.AddressDTO;
import com.lcdev.ecommerce.application.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/address")
public class AddressController {

    private final AddressService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping
    public ResponseEntity<AddressDTO> insert(@Valid @RequestBody AddressDTO dto) {
        AddressDTO newDto = service.addAddress(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> update(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDTO dto) {
        AddressDTO updated = service.updateAddress(addressId, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Long addressId) {
        service.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

}
