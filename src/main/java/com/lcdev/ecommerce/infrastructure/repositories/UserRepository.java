package com.lcdev.ecommerce.infrastructure.repositories;

import com.lcdev.ecommerce.domain.entities.User;
import com.lcdev.ecommerce.infrastructure.projections.UserDetailsProjection;
import com.lcdev.ecommerce.infrastructure.projections.UserMinProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
                SELECT u.id as id,
                       u.firstName as firstName,
                       u.lastName as lastName,
                       u.email as email
                FROM User u
                WHERE UPPER(u.email) LIKE UPPER(CONCAT('%', :email, '%'))
                ORDER BY u.firstName ASC
            """)
    Page<UserMinProjection> searchByEmailProjection(@Param("email") String email, Pageable pageable);

    @Query(nativeQuery = true, value = """
            	SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
            	FROM tb_user
            	INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
            	INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
            	WHERE tb_user.email = :email
            """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    User findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses WHERE u.id = :id")
    Optional<User> findByIdWithAddresses(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses WHERE u.email = :email")
    Optional<User> findByEmailWithAddresses(@Param("email") String email);
}
