package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.*;
import com.lcdev.ecommerce.application.service.exceptions.BadRequestException;
import com.lcdev.ecommerce.application.service.exceptions.DatabaseException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Address;
import com.lcdev.ecommerce.domain.entities.Role;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.AddressMapper;
import com.lcdev.ecommerce.infrastructure.mapper.UserMapper;
import com.lcdev.ecommerce.infrastructure.projections.UserDetailsProjection;
import com.lcdev.ecommerce.infrastructure.projections.UserMinProjection;
import com.lcdev.ecommerce.infrastructure.repositories.AddressRepository;
import com.lcdev.ecommerce.infrastructure.repositories.RoleRepository;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<UserMinResponseDTO> findAll(String email, Pageable pageable) {
        Page<UserMinProjection> result = repository.searchByEmailProjection(email, pageable);
        return result.map(userMapper::mapUserMinResponseDTO);
    }

    public UserResponseDTO save(UserInsertDTO dto){

        if (Objects.nonNull(repository.findByEmail(dto.getEmail()))) {
            throw new BadRequestException("Email já cadastrado!");
        }

        User entity = userMapper.toEntity(dto);

        Role role = roleRepository.findByAuthority("ROLE_CLIENT");
        entity.getRoles().add(role);

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        Address address = addressMapper.toEntity(dto.getAddress(), entity);
        entity.getAddresses().add(address);

        entity = repository.save(entity);

        return userMapper.mapUserResponseDTO(entity);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User entity = repository.findByIdWithAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found! Id:" + id));
        return userMapper.mapUserResponseDTO(entity);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findProfile(){
        User entity = authService.authenticated();
        return userMapper.mapUserResponseDTO(entity);
    }

    @Transactional
    public UserResponseDTO update(UserUpdateDTO dto){
        User entity = authService.authenticated();
        entity = userMapper.mapUpdate(dto, entity);
        entity = repository.save(entity);
        return userMapper.mapUserResponseDTO(entity);
    }

    @Transactional
    public void updateEmail(UserUpdateEmailDTO dto) {
        User user = authService.authenticated();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Senha incorreta!");
        }

        if (Objects.nonNull(repository.findByEmail(dto.getEmail()))) {
            throw new BadRequestException("Email já cadastrado!");
        }

        user.setEmail(dto.getEmail());
        repository.save(user);
    }

    @Transactional
    public void updatePassword(UserUpdatePasswordDTO dto) {
        User user = authService.authenticated();

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Senha incorreta!");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found Id:" + id);
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation!");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }
}
