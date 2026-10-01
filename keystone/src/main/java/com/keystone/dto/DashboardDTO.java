package com.keystone.dto;

public class DashboardDTO {

    private long totalWorkOrders;
    private long open;
    private long assigned;
    private long inProgress;
    private long completed;
    private long closed;
    private long cancelled;

    private long totalCustomers;
    private long totalSites;
    private long totalTechnicians;

    public DashboardDTO() {
    }

    public DashboardDTO(
            long totalWorkOrders,
            long open,
            long assigned,
            long inProgress,
            long completed,
            long closed,
            long cancelled,
            long totalCustomers,
            long totalSites,
            long totalTechnicians) {

        this.totalWorkOrders = totalWorkOrders;
        this.open = open;
        this.assigned = assigned;
        this.inProgress = inProgress;
        this.completed = completed;
        this.closed = closed;
        this.cancelled = cancelled;
        this.totalCustomers = totalCustomers;
        this.totalSites = totalSites;
        this.totalTechnicians = totalTechnicians;
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
    }

    public long getOpen() {
        return open;
    }

    public void setOpen(long open) {
        this.open = open;
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

    public long getClosed() {
        return closed;
    }

    public void setClosed(long closed) {
        this.closed = closed;
    }

    public long getCancelled() {
        return cancelled;
    }

    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalSites() {
        return totalSites;
    }

    public void setTotalSites(long totalSites) {
        this.totalSites = totalSites;
    }

    public long getTotalTechnicians() {
        return totalTechnicians;
    }

    public void setTotalTechnicians(long totalTechnicians) {
        this.totalTechnicians = totalTechnicians;
    }
}