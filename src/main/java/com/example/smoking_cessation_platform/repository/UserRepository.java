package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserPublicId(String userPublicId);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);


}