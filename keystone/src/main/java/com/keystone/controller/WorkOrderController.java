package com.keystone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.keystone.dto.AssignTechnicianDTO;
import com.keystone.dto.StatusHistoryResponseDTO;
import com.keystone.dto.StatusUpdateDTO;
import com.keystone.dto.TechnicianDashboardDTO;
import com.keystone.dto.WorkOrderRequestDTO;
import com.keystone.dto.WorkOrderResponseDTO;
import com.keystone.service.WorkOrderService;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    // =========================================================
    // MANAGER / DISPATCHER - CREATE
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<WorkOrderResponseDTO> createWorkOrder(
            @Valid @RequestBody WorkOrderRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workOrderService.createWorkOrder(request));
    }

    // =========================================================
    // MANAGER / DISPATCHER - GET ALL
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<List<WorkOrderResponseDTO>> getAllWorkOrders() {

        return ResponseEntity.ok(
                workOrderService.getAllWorkOrders());
    }

    // =========================================================
    // MANAGER / DISPATCHER - GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<WorkOrderResponseDTO> getWorkOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                workOrderService.getWorkOrderById(id));
    }

    // =========================================================
    // MANAGER / DISPATCHER - TECHNICIAN WORK ORDERS
    // =========================================================

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<List<WorkOrderResponseDTO>>
            getWorkOrdersByTechnician(
                    @PathVariable Long technicianId) {

        return ResponseEntity.ok(
                workOrderService.getWorkOrdersByTechnician(
                        technicianId));
    }

    // =========================================================
    // TECHNICIAN - OWN WORK ORDERS
    // =========================================================

    @GetMapping("/technician/me")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<List<WorkOrderResponseDTO>>
            getMyWorkOrders(Authentication authentication) {

        return ResponseEntity.ok(
                workOrderService.getWorkOrdersForLoggedInTechnician(
                        authentication.getName()));
    }

    // =========================================================
    // CUSTOMER - OWN WORK ORDERS
    // =========================================================

    @GetMapping("/customer/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<WorkOrderResponseDTO>>
            getMyCustomerWorkOrders(Authentication authentication) {

        return ResponseEntity.ok(
                workOrderService.getCustomerWorkOrders(
                        authentication.getName()));
    }

    // =========================================================
    // CUSTOMER - OWN WORK ORDER BY ID
    // =========================================================

    @GetMapping("/customer/me/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WorkOrderResponseDTO>
            getMyCustomerWorkOrder(
                    @PathVariable Long id,
                    Authentication authentication) {

        return ResponseEntity.ok(
                workOrderService.getCustomerWorkOrder(
                        id,
                        authentication.getName()));
    }

    // =========================================================
    // MANAGER / DISPATCHER - TECHNICIAN DASHBOARD
    // =========================================================

    @GetMapping("/technician/{technicianId}/dashboard")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<TechnicianDashboardDTO>
            getTechnicianDashboard(
                    @PathVariable Long technicianId) {

        return ResponseEntity.ok(
                workOrderService.getTechnicianDashboard(
                        technicianId));
    }

    // =========================================================
    // TECHNICIAN - OWN DASHBOARD
    // =========================================================

    @GetMapping("/technician/me/dashboard")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<TechnicianDashboardDTO>
            getMyTechnicianDashboard(
                    Authentication authentication) {

        return ResponseEntity.ok(
                workOrderService.getLoggedInTechnicianDashboard(
                        authentication.getName()));
    }

    // =========================================================
    // MANAGER / DISPATCHER - UPDATE WORK ORDER
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<WorkOrderResponseDTO> updateWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderRequestDTO request) {

        return ResponseEntity.ok(
                workOrderService.updateWorkOrder(
                        id,
                        request));
    }

    // =========================================================
    // MANAGER / DISPATCHER - UPDATE STATUS
    // =========================================================

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<WorkOrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO request) {

        return ResponseEntity.ok(
                workOrderService.updateStatus(
                        id,
                        request.getStatus()));
    }

    // =========================================================
    // MANAGER / DISPATCHER - ASSIGN TECHNICIAN
    // =========================================================

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<WorkOrderResponseDTO> assignTechnician(
            @PathVariable Long id,
            @Valid @RequestBody AssignTechnicianDTO request) {

        return ResponseEntity.ok(
                workOrderService.assignTechnician(
                        id,
                        request.getTechnicianId()));
    }

    // =========================================================
    // TECHNICIAN - UPDATE OWN ASSIGNED WORK ORDER
    // =========================================================

    @PatchMapping("/{id}/my-status")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<WorkOrderResponseDTO>
            updateMyTechnicianStatus(
                    @PathVariable Long id,
                    @Valid @RequestBody StatusUpdateDTO request,
                    Authentication authentication) {

        return ResponseEntity.ok(
                workOrderService.updateLoggedInTechnicianStatus(
                        id,
                        authentication.getName(),
                        request.getStatus()));
    }

    // =========================================================
    // MANAGER / DISPATCHER - STATUS HISTORY
    // =========================================================

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<List<StatusHistoryResponseDTO>>
            getStatusHistory(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                workOrderService.getStatusHistory(id));
    }

    // =========================================================
    // MANAGER / DISPATCHER - DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<Void> deleteWorkOrder(
            @PathVariable Long id) {

        workOrderService.deleteWorkOrder(id);

        return ResponseEntity.noContent().build();
    }
}