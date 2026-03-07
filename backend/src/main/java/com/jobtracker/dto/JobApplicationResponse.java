package com.jobtracker.dto;

import com.jobtracker.model.JobApplication;
import java.time.LocalDate;

public class JobApplicationResponse {
    private Long id;
    private String company;
    private String role;
    private JobApplication.ApplicationStatus status;
    private LocalDate applicationDate;
    private LocalDate interviewDate;
    private String notes;
    private Long userId;
    private String companyWebsite;
    private String companyLogo;
    private String applicationSource;

    public JobApplicationResponse() {}
    public JobApplicationResponse(Long id, String company, String role,
            JobApplication.ApplicationStatus status, LocalDate applicationDate,
            LocalDate interviewDate, String notes, Long userId,
            String companyWebsite, String companyLogo, String applicationSource) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.status = status;
        this.applicationDate = applicationDate;
        this.interviewDate = interviewDate;
        this.notes = notes;
        this.userId = userId;
        this.companyWebsite = companyWebsite;
        this.companyLogo = companyLogo;
        this.applicationSource = applicationSource;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public JobApplication.ApplicationStatus getStatus() { return status; }
    public void setStatus(JobApplication.ApplicationStatus status) { this.status = status; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }
    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }
    public String getApplicationSource() { return applicationSource; }
    public void setApplicationSource(String applicationSource) { this.applicationSource = applicationSource; }
}
