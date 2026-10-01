package com.keystone.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTechnicianDTO {

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    public AssignTechnicianDTO() {
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}