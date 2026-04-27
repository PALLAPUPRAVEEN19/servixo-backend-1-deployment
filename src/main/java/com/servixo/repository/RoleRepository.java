package com.servixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.servixo.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    Optional<Role> findByNameIgnoreCase(String name);
}