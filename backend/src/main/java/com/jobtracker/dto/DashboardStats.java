package com.jobtracker.dto;

public class DashboardStats {
    private long totalApplications;
    private long interviews;
    private long offers;
    private long rejections;
    private long applied;
    private double rejectionRate;

    public DashboardStats() {}
    public DashboardStats(long totalApplications, long interviews, long offers,
                          long rejections, long applied, double rejectionRate) {
        this.totalApplications = totalApplications;
        this.interviews = interviews;
        this.offers = offers;
        this.rejections = rejections;
        this.applied = applied;
        this.rejectionRate = rejectionRate;
    }

    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getInterviews() { return interviews; }
    public void setInterviews(long interviews) { this.interviews = interviews; }
    public long getOffers() { return offers; }
    public void setOffers(long offers) { this.offers = offers; }
    public long getRejections() { return rejections; }
    public void setRejections(long rejections) { this.rejections = rejections; }
    public long getApplied() { return applied; }
    public void setApplied(long applied) { this.applied = applied; }
    public double getRejectionRate() { return rejectionRate; }
    public void setRejectionRate(double rejectionRate) { this.rejectionRate = rejectionRate; }
}
