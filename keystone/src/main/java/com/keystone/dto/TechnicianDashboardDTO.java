package com.keystone.dto;

public class TechnicianDashboardDTO {

    private Long technicianId;
    private String technicianName;

    private long totalAssigned;
    private long assigned;
    private long inProgress;
    private long completed;

    public TechnicianDashboardDTO() {
    }

    public TechnicianDashboardDTO(
            Long technicianId,
            String technicianName,
            long totalAssigned,
            long assigned,
            long inProgress,
            long completed) {

        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.totalAssigned = totalAssigned;
        this.assigned = assigned;
        this.inProgress = inProgress;
        this.completed = completed;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public long getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(long totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}