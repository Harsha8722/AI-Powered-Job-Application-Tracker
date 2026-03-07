package com.jobtracker.repository;

import com.jobtracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long userId);
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
    long countByUserIdAndStatus(Long userId, JobApplication.ApplicationStatus status);
    long countByUserId(Long userId);
    long countByStatus(JobApplication.ApplicationStatus status);
}
