package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.address.AddressDTO;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Address;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.AddressMapper;
import com.lcdev.ecommerce.infrastructure.repositories.AddressRepository;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository repository;
    private final AddressMapper addressMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Transactional
    public AddressDTO addAddress(AddressDTO dto) {
        User user = getCurrentUser();

        repository.findDuplicateByZipCodeAndNumber(user, dto.getZipCode(), dto.getNumber())
                .ifPresent(a -> {
                    throw new BadRequestException("Endereço já cadastrado para este usuário");
                });

        Address address = addressMapper.toEntity(dto, user);
        address = repository.save(address);
        return addressMapper.toDTO(address);
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO dto) {
        User user = getCurrentUser();
        Address address = repository.findByIdAndUser_Id(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        addressMapper.updateEntityFromDto(dto, address);
        address = repository.save(address);

        return addressMapper.toDTO(address);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        User user = getCurrentUser();
        Address address = repository.findByIdAndUser_Id(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        user.getAddresses().remove(address);

        repository.deleteById(address.getId());
    }

    private User getCurrentUser() {
        return authService.authenticated(userRepository::findByEmailOptional);
    }

}
