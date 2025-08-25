package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.UserDTO;
import com.lcdev.ecommerce.application.dto.UserInsertDTO;
import com.lcdev.ecommerce.application.dto.UserUpdateDTO;
import com.lcdev.ecommerce.application.service.exceptions.DatabaseException;
import com.lcdev.ecommerce.application.service.exceptions.ResourceNotFoundException;
import com.lcdev.ecommerce.domain.entities.Role;
import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.mapper.UserMapper;
import com.lcdev.ecommerce.infrastructure.projections.UserDetailsProjection;
import com.lcdev.ecommerce.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(String email, Pageable pageable){
        Page<User> result = repository.searchByEmail(email, pageable);
        return result.map(userMapper::mapUserDTO);
    }

    public UserDTO save(UserInsertDTO dto){
        User entity = userMapper.toEntity(dto);
        entity.setPassword(dto.getPassword());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return userMapper.mapUserDTO(entity);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found! Id:" + id));
        return userMapper.mapUserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto){
        try {
            User entity = repository.getReferenceById(id);
            entity = userMapper.mapUpdate(dto, entity);
            entity = repository.save(entity);
            return userMapper.mapUserDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found! Id:" + id);
        }
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
