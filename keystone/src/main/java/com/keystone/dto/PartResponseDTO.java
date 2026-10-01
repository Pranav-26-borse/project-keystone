package com.keystone.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PartResponseDTO {

    private Long id;
    private String name;
    private String partNumber;
    private Integer quantityAvailable;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;

    public PartResponseDTO() {
    }

    public PartResponseDTO(
            Long id,
            String name,
            String partNumber,
            Integer quantityAvailable,
            BigDecimal unitPrice,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.partNumber = partNumber;
        this.quantityAvailable = quantityAvailable;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}