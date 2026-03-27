package com.jobtracker.controller;

import com.jobtracker.dto.DashboardStats;
import com.jobtracker.dto.JobApplicationRequest;
import com.jobtracker.dto.JobApplicationResponse;
import com.jobtracker.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Applications", description = "Job application CRUD operations")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping
    @Operation(summary = "Create a new job application")
    public ResponseEntity<?> createJob(@Valid @RequestBody JobApplicationRequest request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            JobApplicationResponse response = jobService.createJob(request, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all job applications for current user")
    public ResponseEntity<List<JobApplicationResponse>> getAllJobs(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getAllJobs(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job application")
    public ResponseEntity<?> updateJob(@PathVariable Long id,
                                       @Valid @RequestBody JobApplicationRequest request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            JobApplicationResponse response = jobService.updateJob(id, request, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job application")
    public ResponseEntity<?> deleteJob(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            jobService.deleteJob(id, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("message", "Job application deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update job application status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String statusStr = body.get("status");
            JobApplicationResponse response = jobService.updateStatus(id, statusStr, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<DashboardStats> getDashboardStats(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(jobService.getDashboardStats(userDetails.getUsername()));
    }
}
