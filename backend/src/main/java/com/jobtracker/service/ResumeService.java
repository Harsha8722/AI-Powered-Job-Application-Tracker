package com.jobtracker.service;

import com.jobtracker.dto.ResumeResponse;
import com.jobtracker.model.Resume;
import com.jobtracker.model.User;
import com.jobtracker.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    public ResumeResponse uploadResume(MultipartFile file, String email) throws IOException {
        User user = authService.getUserByEmail(email);

        // Validate file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.toLowerCase().endsWith(".pdf") &&
                 !originalFilename.toLowerCase().endsWith(".docx"))) {
            throw new RuntimeException("Only PDF and DOCX files are allowed");
        }

        // Validate file size (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("File size must be under 10MB");
        }

        // Create upload directory
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file with unique name
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Save resume record
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFilePath(filePath.toString());
        resume.setOriginalFilename(originalFilename);

        Resume saved = resumeRepository.save(resume);
        
        notificationService.createNotification(user, "Resume Uploaded", 
            "Your resume '" + originalFilename + "' has been uploaded and is ready for AI analysis.");
            
        return mapToResponse(saved);
    }

    public List<ResumeResponse> getUserResumes(String email) {
        User user = authService.getUserByEmail(email);
        return resumeRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Resume getLatestResume(Long userId) {
        return resumeRepository.findTopByUserIdOrderByUploadDateDesc(userId)
                .orElseThrow(() -> new RuntimeException("No resume found. Please upload a resume first."));
    }

    public void updateAnalysisResult(Long resumeId, String result) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        resume.setAnalysisResult(result);
        resumeRepository.save(resume);
    }

    public void deleteResume(Long resumeId, String email) {
        User user = authService.getUserByEmail(email);
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Ensure the resume belongs to the requesting user
        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: you can only delete your own resumes");
        }

        // Delete the physical file
        try {
            File file = new File(resume.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // Log but don't fail if file deletion fails
        }

        // Delete DB record
        resumeRepository.deleteById(resumeId);
    }

    private ResumeResponse mapToResponse(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getUser().getId(),
                resume.getOriginalFilename(),
                resume.getUploadDate(),
                resume.getAnalysisResult()
        );
    }
}
