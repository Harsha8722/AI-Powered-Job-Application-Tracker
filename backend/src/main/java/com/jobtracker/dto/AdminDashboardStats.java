package com.jobtracker.dto;

public class AdminDashboardStats {
    private long totalUsers;
    private long totalJobs;
    private long totalResumes;
    
    public AdminDashboardStats() {}

    public AdminDashboardStats(long totalUsers, long totalJobs, long totalResumes) {
        this.totalUsers = totalUsers;
        this.totalJobs = totalJobs;
        this.totalResumes = totalResumes;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalJobs() { return totalJobs; }
    public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }
    public long getTotalResumes() { return totalResumes; }
    public void setTotalResumes(long totalResumes) { this.totalResumes = totalResumes; }
}
