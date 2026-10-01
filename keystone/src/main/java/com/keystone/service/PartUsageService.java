package com.keystone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keystone.dto.PartUsageRequestDTO;
import com.keystone.dto.PartUsageResponseDTO;
import com.keystone.entity.Part;
import com.keystone.entity.PartUsage;
import com.keystone.entity.Role;
import com.keystone.entity.Status;
import com.keystone.entity.User;
import com.keystone.entity.WorkOrder;
import com.keystone.repository.PartRepository;
import com.keystone.repository.PartUsageRepository;
import com.keystone.repository.UserRepository;
import com.keystone.repository.WorkOrderRepository;

@Service
public class PartUsageService {

    private final PartUsageRepository partUsageRepository;
    private final PartRepository partRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    public PartUsageService(
            PartUsageRepository partUsageRepository,
            PartRepository partRepository,
            WorkOrderRepository workOrderRepository,
            UserRepository userRepository) {

        this.partUsageRepository = partUsageRepository;
        this.partRepository = partRepository;
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    @Transactional
    public PartUsageResponseDTO addPartUsage(
            Long workOrderId,
            PartUsageRequestDTO request,
            String loggedInEmail) {

        WorkOrder workOrder = getWorkOrder(workOrderId);

        validateTechnicianAccess(workOrder, loggedInEmail);

        Part part = partRepository
                .findById(request.getPartId())
                .orElseThrow(() ->
                        new RuntimeException("Part not found"));

        if (workOrder.getStatus() == Status.CLOSED
                || workOrder.getStatus() == Status.CANCELLED) {

            throw new RuntimeException(
                    "Parts cannot be added to closed or cancelled work orders");
        }

        if (part.getQuantityAvailable()
                < request.getQuantityUsed()) {

            throw new RuntimeException(
                    "Insufficient part quantity available");
        }

        List<PartUsage> existingUsages =
                partUsageRepository
                        .findByWorkOrderId(workOrderId);

        boolean alreadyUsed = existingUsages
                .stream()
                .anyMatch(usage ->
                        usage.getPart()
                                .getId()
                                .equals(part.getId()));

        if (alreadyUsed) {

            throw new RuntimeException(
                    "This part is already used in this work order");
        }

        part.setQuantityAvailable(
                part.getQuantityAvailable()
                        - request.getQuantityUsed());

        partRepository.save(part);

        PartUsage partUsage = new PartUsage();

        partUsage.setWorkOrder(workOrder);
        partUsage.setPart(part);
        partUsage.setQuantityUsed(
                request.getQuantityUsed());

        return convertToResponse(
                partUsageRepository.save(partUsage));
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    public List<PartUsageResponseDTO> getPartsUsedByWorkOrder(
            Long workOrderId,
            String loggedInEmail) {

        WorkOrder workOrder = getWorkOrder(workOrderId);

        validateTechnicianAccess(workOrder, loggedInEmail);

        return partUsageRepository
                .findByWorkOrderId(workOrderId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // MANAGER / DISPATCHER / ASSIGNED TECHNICIAN
    public PartUsageResponseDTO getPartUsageById(
            Long id,
            String loggedInEmail) {

        PartUsage partUsage = partUsageRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Part usage record not found"));

        validateTechnicianAccess(
                partUsage.getWorkOrder(),
                loggedInEmail);

        return convertToResponse(partUsage);
    }

    private WorkOrder getWorkOrder(Long workOrderId) {

        return workOrderRepository
                .findById(workOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));
    }

    private void validateTechnicianAccess(
            WorkOrder workOrder,
            String loggedInEmail) {

        User user = userRepository
                .findByEmail(loggedInEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));

        // Manager and Dispatcher can access all work orders.
        if (user.getRole() == Role.MANAGER
                || user.getRole() == Role.DISPATCHER) {
            return;
        }

        // Only technicians reach this point.
        if (user.getRole() != Role.TECHNICIAN) {
            throw new RuntimeException(
                    "You are not authorized to access part usage");
        }

        // Technician must be assigned to this work order.
        if (workOrder.getAssignedTechnician() == null
                || !workOrder.getAssignedTechnician()
                        .getId()
                        .equals(user.getId())) {

            throw new RuntimeException(
                    "This work order is not assigned to this technician");
        }
    }

    private PartUsageResponseDTO convertToResponse(
            PartUsage partUsage) {

        WorkOrder workOrder =
                partUsage.getWorkOrder();

        Part part =
                partUsage.getPart();

        return new PartUsageResponseDTO(
                partUsage.getId(),
                workOrder.getId(),
                workOrder.getCode(),
                part.getId(),
                part.getName(),
                part.getPartNumber(),
                partUsage.getQuantityUsed(),
                partUsage.getUsedAt()
        );
    }
}