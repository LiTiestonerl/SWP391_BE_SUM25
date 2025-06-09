package com.example.smoking_cessation_platform.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {

}