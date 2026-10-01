package com.keystone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.keystone.dto.PartUsageRequestDTO;
import com.keystone.dto.PartUsageResponseDTO;
import com.keystone.service.PartUsageService;

@RestController
@RequestMapping("/api/work-orders/{workOrderId}/parts")
public class PartUsageController {

    private final PartUsageService partUsageService;

    public PartUsageController(
            PartUsageService partUsageService) {

        this.partUsageService = partUsageService;
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @PostMapping
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<PartUsageResponseDTO> addPartUsage(
            @PathVariable Long workOrderId,
            @Valid @RequestBody PartUsageRequestDTO request,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        partUsageService.addPartUsage(
                                workOrderId,
                                request,
                                authentication.getName()));
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @GetMapping
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<List<PartUsageResponseDTO>> getPartsUsed(
            @PathVariable Long workOrderId,
            Authentication authentication) {

        return ResponseEntity.ok(
                partUsageService.getPartsUsedByWorkOrder(
                        workOrderId,
                        authentication.getName()));
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @GetMapping("/{usageId}")
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<PartUsageResponseDTO> getPartUsageById(
            @PathVariable Long workOrderId,
            @PathVariable Long usageId,
            Authentication authentication) {

        return ResponseEntity.ok(
                partUsageService.getPartUsageById(
                        usageId,
                        authentication.getName()));
    }
}