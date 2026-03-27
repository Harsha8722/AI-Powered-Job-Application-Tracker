package com.jobtracker.service;

import com.jobtracker.dto.DashboardStats;
import com.jobtracker.dto.JobApplicationRequest;
import com.jobtracker.dto.JobApplicationResponse;
import com.jobtracker.model.JobApplication;
import com.jobtracker.model.User;
import com.jobtracker.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    public JobApplicationResponse createJob(JobApplicationRequest request, String email) {
        User user = authService.getUserByEmail(email);
        JobApplication job = new JobApplication();
        job.setUser(user);
        job.setCompany(request.getCompany());
        job.setRole(request.getRole());
        job.setStatus(request.getStatus());
        job.setApplicationDate(request.getApplicationDate());
        job.setInterviewDate(request.getInterviewDate());
        job.setNotes(request.getNotes());
        job.setCompanyWebsite(request.getCompanyWebsite());
        job.setCompanyLogo(request.getCompanyLogo());
        job.setApplicationSource(request.getApplicationSource());

        JobApplication saved = jobRepository.save(job);
        
        notificationService.createNotification(user, "New Application Added", 
            "Successfully added application for " + job.getRole() + " at " + job.getCompany());
            
        return mapToResponse(saved);
    }

    public List<JobApplicationResponse> getAllJobs(String email) {
        User user = authService.getUserByEmail(email);
        return jobRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobApplicationResponse updateJob(Long id, JobApplicationRequest request, String email) {
        User user = authService.getUserByEmail(email);
        JobApplication job = jobRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Job application not found"));

        job.setCompany(request.getCompany());
        job.setRole(request.getRole());
        job.setStatus(request.getStatus());
        job.setApplicationDate(request.getApplicationDate());
        job.setInterviewDate(request.getInterviewDate());
        job.setNotes(request.getNotes());
        job.setCompanyWebsite(request.getCompanyWebsite());
        job.setCompanyLogo(request.getCompanyLogo());
        job.setApplicationSource(request.getApplicationSource());

        JobApplication saved = jobRepository.save(job);
        
        notificationService.createNotification(user, "Application Updated", 
            "Updated application for " + job.getRole() + " at " + job.getCompany());
            
        return mapToResponse(saved);
    }

    public void deleteJob(Long id, String email) {
        User user = authService.getUserByEmail(email);
        JobApplication job = jobRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Job application not found"));
        jobRepository.delete(job);
    }

    public JobApplicationResponse updateStatus(Long id, String statusStr, String email) {
        User user = authService.getUserByEmail(email);
        JobApplication job = jobRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Job application not found"));
        try {
            JobApplication.ApplicationStatus status = JobApplication.ApplicationStatus.valueOf(statusStr.toUpperCase());
            job.setStatus(status);
            JobApplication saved = jobRepository.save(job);
            notificationService.createNotification(user, "Status Updated",
                "Application for " + job.getRole() + " at " + job.getCompany() + " is now " + status);
            return mapToResponse(saved);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + statusStr);
        }
    }

    public DashboardStats getDashboardStats(String email) {
        User user = authService.getUserByEmail(email);
        Long userId = user.getId();

        long total = jobRepository.countByUserId(userId);
        long interviews = jobRepository.countByUserIdAndStatus(userId, JobApplication.ApplicationStatus.INTERVIEW);
        long offers = jobRepository.countByUserIdAndStatus(userId, JobApplication.ApplicationStatus.OFFER);
        long rejections = jobRepository.countByUserIdAndStatus(userId, JobApplication.ApplicationStatus.REJECTED);
        long applied = jobRepository.countByUserIdAndStatus(userId, JobApplication.ApplicationStatus.APPLIED);
        double rejectionRate = total > 0 ? (double) rejections / total * 100 : 0;

        return new DashboardStats(total, interviews, offers, rejections, applied, rejectionRate);
    }

    private JobApplicationResponse mapToResponse(JobApplication job) {
        return new JobApplicationResponse(
                job.getId(),
                job.getCompany(),
                job.getRole(),
                job.getStatus(),
                job.getApplicationDate(),
                job.getInterviewDate(),
                job.getNotes(),
                job.getUser().getId(),
                job.getCompanyWebsite(),
                job.getCompanyLogo(),
                job.getApplicationSource()
        );
    }
}
