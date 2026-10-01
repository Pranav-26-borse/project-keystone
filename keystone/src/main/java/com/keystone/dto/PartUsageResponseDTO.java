package com.keystone.dto;

import java.time.LocalDateTime;

public class PartUsageResponseDTO {

    private Long id;

    private Long workOrderId;
    private String workOrderCode;

    private Long partId;
    private String partName;
    private String partNumber;

    private Integer quantityUsed;

    private LocalDateTime usedAt;

    public PartUsageResponseDTO() {
    }

    public PartUsageResponseDTO(
            Long id,
            Long workOrderId,
            String workOrderCode,
            Long partId,
            String partName,
            String partNumber,
            Integer quantityUsed,
            LocalDateTime usedAt) {

        this.id = id;
        this.workOrderId = workOrderId;
        this.workOrderCode = workOrderCode;
        this.partId = partId;
        this.partName = partName;
        this.partNumber = partNumber;
        this.quantityUsed = quantityUsed;
        this.usedAt = usedAt;
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

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Integer quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}