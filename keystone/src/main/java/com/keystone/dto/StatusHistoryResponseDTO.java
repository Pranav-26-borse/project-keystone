package com.keystone.dto;

import java.time.LocalDateTime;

import com.keystone.entity.Status;

public class StatusHistoryResponseDTO {

    private Long id;
    private Long workOrderId;

    private Status oldStatus;
    private Status newStatus;

    private Long changedById;
    private String changedByName;

    private LocalDateTime changedAt;

    public StatusHistoryResponseDTO() {
    }

    public StatusHistoryResponseDTO(
            Long id,
            Long workOrderId,
            Status oldStatus,
            Status newStatus,
            Long changedById,
            String changedByName,
            LocalDateTime changedAt) {

        this.id = id;
        this.workOrderId = workOrderId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedById = changedById;
        this.changedByName = changedByName;
        this.changedAt = changedAt;
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

    public Status getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Status oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}