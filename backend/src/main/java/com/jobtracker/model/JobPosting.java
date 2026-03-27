package com.jobtracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String location;

    private String salary;
    private String type;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @Column(name = "deadline")
    private LocalDate deadline;

    private Integer openings;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "external_link")
    private String externalLink;

    @Column(name = "applicants_count")
    private Integer applicantsCount = 0;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        postedDate = LocalDateTime.now();
    }

    public JobPosting() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDriveDate() { return driveDate; }
    public void setDriveDate(LocalDate driveDate) { this.driveDate = driveDate; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public Integer getOpenings() { return openings; }
    public void setOpenings(Integer openings) { this.openings = openings; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getExternalLink() { return externalLink; }
    public void setExternalLink(String externalLink) { this.externalLink = externalLink; }
    public Integer getApplicantsCount() { return applicantsCount; }
    public void setApplicantsCount(Integer applicantsCount) { this.applicantsCount = applicantsCount; }
    public LocalDateTime getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDateTime postedDate) { this.postedDate = postedDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
