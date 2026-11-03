package com.wigstore.wigstore.repository;

import com.wigstore.wigstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import com.wigstore.wigstore.model.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
List<User> findByRoleAndEmailConsentTrue(Role role);
}