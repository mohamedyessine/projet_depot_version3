package com.example.bureau.repo;

import com.example.bureau.models.ERole;
import com.example.bureau.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
