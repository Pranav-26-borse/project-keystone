package com.keystone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.keystone.dto.TimeLogRequestDTO;
import com.keystone.dto.TimeLogResponseDTO;
import com.keystone.service.TimeLogService;

@RestController
@RequestMapping("/api/work-orders/{workOrderId}/time-logs")
public class TimeLogController {

    private final TimeLogService timeLogService;

    public TimeLogController(TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @PostMapping
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<TimeLogResponseDTO> createTimeLog(
            @PathVariable Long workOrderId,
            @Valid @RequestBody TimeLogRequestDTO request,
            Authentication authentication) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        timeLogService.createTimeLog(
                                workOrderId,
                                request,
                                authentication.getName()));
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @GetMapping
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<List<TimeLogResponseDTO>>
            getTimeLogsByWorkOrder(
                    @PathVariable Long workOrderId,
                    Authentication authentication) {

        return ResponseEntity.ok(
                timeLogService.getTimeLogsByWorkOrder(
                        workOrderId,
                        authentication.getName()));
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<TimeLogResponseDTO> getTimeLogById(
            @PathVariable Long workOrderId,
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                timeLogService.getTimeLogById(
                        id,
                        authentication.getName()));
    }

    // MANAGER / DISPATCHER / TECHNICIAN
    @GetMapping("/technician/{technicianId}")
    @PreAuthorize(
            "hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<List<TimeLogResponseDTO>>
            getTimeLogsByTechnician(
                    @PathVariable Long workOrderId,
                    @PathVariable Long technicianId,
                    Authentication authentication) {

        return ResponseEntity.ok(
                timeLogService.getTimeLogsByTechnician(
                        technicianId,
                        authentication.getName()));
    }
}