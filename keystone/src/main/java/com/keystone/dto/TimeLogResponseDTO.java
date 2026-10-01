package com.keystone.dto;

import java.time.LocalDateTime;

public class TimeLogResponseDTO {

    private Long id;

    private Long workOrderId;
    private String workOrderCode;

    private Long technicianId;
    private String technicianName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String notes;

    public TimeLogResponseDTO() {
    }

    public TimeLogResponseDTO(
            Long id,
            Long workOrderId,
            String workOrderCode,
            Long technicianId,
            String technicianName,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String notes) {

        this.id = id;
        this.workOrderId = workOrderId;
        this.workOrderCode = workOrderCode;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderCode() {
        return workOrderCode;
    }

    public void setWorkOrderCode(String workOrderCode) {
        this.workOrderCode = workOrderCode;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}