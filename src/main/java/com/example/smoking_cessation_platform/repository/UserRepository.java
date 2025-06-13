package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
}