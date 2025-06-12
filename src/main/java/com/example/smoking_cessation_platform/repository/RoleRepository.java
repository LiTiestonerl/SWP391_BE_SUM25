package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName); // Tìm kiếm Role theo tên
}