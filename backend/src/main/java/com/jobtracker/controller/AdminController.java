package com.jobtracker.controller;

import com.jobtracker.dto.AdminDashboardStats;
import com.jobtracker.repository.JobRepository;
import com.jobtracker.repository.ResumeRepository;
import com.jobtracker.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin dashboard endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "Get aggregate stats for admin dashboard")
    public ResponseEntity<?> getDashboardStats(@AuthenticationPrincipal UserDetails userDetails) {
        long totalUsers = userRepository.count();
        long totalJobs = jobRepository.count();
        long totalResumes = resumeRepository.count();

        AdminDashboardStats stats = new AdminDashboardStats(totalUsers, totalJobs, totalResumes);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(com.jobtracker.dto.UserDto::new).toList());
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<?> deleteUser(@org.springframework.web.bind.annotation.PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
    @GetMapping("/analytics")
    @Operation(summary = "Get platform-wide analytics")
    public ResponseEntity<?> getAnalytics() {
        long applied = jobRepository.countByStatus(com.jobtracker.model.JobApplication.ApplicationStatus.APPLIED);
        long interviews = jobRepository.countByStatus(com.jobtracker.model.JobApplication.ApplicationStatus.INTERVIEW);
        long offers = jobRepository.countByStatus(com.jobtracker.model.JobApplication.ApplicationStatus.OFFER);
        long rejections = jobRepository.countByStatus(com.jobtracker.model.JobApplication.ApplicationStatus.REJECTED);

        return ResponseEntity.ok(Map.of(
            "applied", applied,
            "interviews", interviews,
            "offers", offers,
            "rejections", rejections,
            "total", applied + interviews + offers + rejections
        ));
    }
}
