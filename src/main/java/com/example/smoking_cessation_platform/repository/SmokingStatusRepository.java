package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.SmokingStatus;
import com.example.smoking_cessation_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SmokingStatusRepository extends JpaRepository<SmokingStatus, Integer> {

    Optional<SmokingStatus> findTopByUser_UserIdOrderByRecordDateDesc(Long userId);

    SmokingStatus findByUser_UserId(Long userId);

}
