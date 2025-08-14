package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT obj FROM User obj " +
            "WHERE UPPER(obj.email) LIKE UPPER(CONCAT('%', :email, '%'))" +
            "ORDER BY obj.firstName ASC")
    Page<User> searchByEmail(String email, Pageable pageable);
}
