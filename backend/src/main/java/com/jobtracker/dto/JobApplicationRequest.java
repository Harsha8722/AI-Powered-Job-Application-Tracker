package com.jobtracker.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.jobtracker.model.JobApplication;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class JobApplicationRequest {

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    // Status is optional from frontend; defaults to APPLIED
    private JobApplication.ApplicationStatus status = JobApplication.ApplicationStatus.APPLIED;

    private String location;
    private LocalDate applicationDate;
    private LocalDate interviewDate;
    private String notes;
    private String companyWebsite;
    private String companyLogo;
    private String applicationSource;

    // driveId — optional field sent when applying from job board (ignored for now, stored in notes)
    private Long driveId;

    public JobApplicationRequest() {}

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public JobApplication.ApplicationStatus getStatus() {
        return status != null ? status : JobApplication.ApplicationStatus.APPLIED;
    }

    // Accept both enum value and raw string (e.g. "APPLIED" or "applied")
    public void setStatus(Object statusRaw) {
        if (statusRaw == null) {
            this.status = JobApplication.ApplicationStatus.APPLIED;
            return;
        }
        try {
            this.status = JobApplication.ApplicationStatus.valueOf(statusRaw.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.status = JobApplication.ApplicationStatus.APPLIED;
        }
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }

    public LocalDate getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDate interviewDate) { this.interviewDate = interviewDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public String getApplicationSource() { return applicationSource; }
    public void setApplicationSource(String applicationSource) { this.applicationSource = applicationSource; }

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }
}
