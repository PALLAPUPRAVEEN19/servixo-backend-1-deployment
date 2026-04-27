package com.servixo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.servixo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // 🔥 FIXED LOGIN (CASE-INSENSITIVE)
    Optional<User> findByEmailIgnoreCase(String email);

    // 🔥 FETCH WITH ROLE (FIXED)
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    // 🔥 GET ALL USERS WITH ROLE
    @Query("SELECT u FROM User u JOIN FETCH u.role")
    List<User> findAllUsersWithRole();

    // 🔥 COUNT USERS BY ROLE
    long countByRole_Name(String roleName);

    // 🔥 VERIFIED USER CHECK (FIXED)
    Optional<User> findByEmailIgnoreCaseAndIsVerifiedTrue(String email);
}