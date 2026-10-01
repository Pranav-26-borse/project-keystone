package com.keystone.dto;

import java.time.LocalDateTime;

public class SiteResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private Long customerId;
    private LocalDateTime createdAt;

    public SiteResponseDTO() {
    }

    public SiteResponseDTO(
            Long id,
            String name,
            String address,
            String phone,
            Long customerId,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.customerId = customerId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}