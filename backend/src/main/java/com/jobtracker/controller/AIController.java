package com.jobtracker.controller;

import com.jobtracker.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@Tag(name = "AI Analysis", description = "AI resume analysis and matching")
@SecurityRequirement(name = "bearerAuth")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/analyze")
    @Operation(summary = "Analyze the latest uploaded resume using AI")
    public ResponseEntity<?> analyzeResume(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Map<String, Object> result = aiService.analyzeResume(userDetails.getUsername());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/match")
    @Operation(summary = "Match resume against a job description")
    public ResponseEntity<?> matchResume(@RequestBody Map<String, String> request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String jobDescription = request.get("jobDescription");
            if (jobDescription == null || jobDescription.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Job description is required"));
            }
            Map<String, Object> result = aiService.matchResume(jobDescription, userDetails.getUsername());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
