package com.jobtracker.controller;

import com.jobtracker.model.JobPosting;
import com.jobtracker.repository.JobPostingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/job-postings")
@Tag(name = "Job Postings", description = "Admin job posting CRUD")
@SecurityRequirement(name = "bearerAuth")
public class JobPostingController {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    /** GET all postings — accessible to all authenticated users (both student and admin) */
    @GetMapping
    @Operation(summary = "Get all active job postings")
    public ResponseEntity<List<JobPosting>> getAllPostings() {
        return ResponseEntity.ok(jobPostingRepository.findByActiveTrueOrderByPostedDateDesc());
    }

    /** POST new job posting — admin only */
    @PostMapping
    @Operation(summary = "Create new job posting (admin only)")
    public ResponseEntity<?> createPosting(@RequestBody Map<String, Object> body) {
        try {
            JobPosting posting = buildFromBody(body, new JobPosting());
            JobPosting saved = jobPostingRepository.save(posting);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT update job posting */
    @PutMapping("/{id}")
    @Operation(summary = "Update job posting (admin only)")
    public ResponseEntity<?> updatePosting(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return jobPostingRepository.findById(id).map(posting -> {
            buildFromBody(body, posting);
            return ResponseEntity.ok(jobPostingRepository.save(posting));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** DELETE job posting */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job posting (admin only)")
    public ResponseEntity<?> deletePosting(@PathVariable Long id) {
        return jobPostingRepository.findById(id).map(posting -> {
            posting.setActive(false); // soft delete
            jobPostingRepository.save(posting);
            return ResponseEntity.ok(Map.of("message", "Deleted"));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** PATCH increment applicant count when someone applies */
    @PatchMapping("/{id}/apply")
    @Operation(summary = "Increment applicant count")
    public ResponseEntity<?> incrementApplicants(@PathVariable Long id) {
        return jobPostingRepository.findById(id).map(p -> {
            p.setApplicantsCount((p.getApplicantsCount() == null ? 0 : p.getApplicantsCount()) + 1);
            return ResponseEntity.ok(jobPostingRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    private JobPosting buildFromBody(Map<String, Object> body, JobPosting posting) {
        if (body.get("company") != null) posting.setCompany(body.get("company").toString());
        if (body.get("role") != null) posting.setRole(body.get("role").toString());
        if (body.get("location") != null) posting.setLocation(body.get("location").toString());
        if (body.get("salary") != null) posting.setSalary(body.get("salary").toString());
        if (body.get("type") != null) posting.setType(body.get("type").toString());
        if (body.get("description") != null) posting.setDescription(body.get("description").toString());
        if (body.get("requirements") != null) posting.setRequirements(body.get("requirements").toString());
        if (body.get("skills") != null) posting.setSkills(body.get("skills").toString());
        if (body.get("externalLink") != null) posting.setExternalLink(body.get("externalLink").toString());
        if (body.get("openings") != null) {
            try { posting.setOpenings(Integer.parseInt(body.get("openings").toString())); } catch (Exception ignored) {}
        }
        if (body.get("deadline") != null && !body.get("deadline").toString().isBlank()) {
            try { posting.setDeadline(LocalDate.parse(body.get("deadline").toString())); } catch (Exception ignored) {}
        }
        if (body.get("driveDate") != null && !body.get("driveDate").toString().isBlank()) {
            try { posting.setDriveDate(LocalDate.parse(body.get("driveDate").toString())); } catch (Exception ignored) {}
        }
        return posting;
    }
}
