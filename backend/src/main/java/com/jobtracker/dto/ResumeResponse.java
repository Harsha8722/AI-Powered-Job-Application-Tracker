package com.jobtracker.dto;

import java.time.LocalDateTime;

public class ResumeResponse {
    private Long id;
    private Long userId;
    private String originalFilename;
    private LocalDateTime uploadDate;
    private String analysisResult;

    public ResumeResponse() {}
    public ResumeResponse(Long id, Long userId, String originalFilename,
                          LocalDateTime uploadDate, String analysisResult) {
        this.id = id;
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.uploadDate = uploadDate;
        this.analysisResult = analysisResult;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public String getAnalysisResult() { return analysisResult; }
    public void setAnalysisResult(String analysisResult) { this.analysisResult = analysisResult; }
}
