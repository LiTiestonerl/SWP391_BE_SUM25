package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);
    @EntityGraph(attributePaths = "role")
    Optional<User> findByUserName(String username);
    Optional<User> findByUserPublicId(String userPublicId);
    @EntityGraph(attributePaths = "role")
    Optional<User> findByUserId(Long userId);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
}