package com.keystone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PartUsageRequestDTO {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotNull(message = "Quantity used is required")
    @Min(value = 1, message = "Quantity used must be at least 1")
    private Integer quantityUsed;

    public PartUsageRequestDTO() {
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public Integer getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Integer quantityUsed) {
        this.quantityUsed = quantityUsed;
    }
}