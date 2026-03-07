package com.jobtracker.controller;

import com.jobtracker.dto.ResumeResponse;
import com.jobtracker.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume", description = "Resume upload endpoints")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a resume (PDF or DOCX)")
    public ResponseEntity<?> uploadResume(@RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ResumeResponse response = resumeService.uploadResume(file, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException | java.io.IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all resumes for current user")
    public ResponseEntity<List<ResumeResponse>> getResumes(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(resumeService.getUserResumes(userDetails.getUsername()));
    }
}
