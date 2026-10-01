package com.keystone.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keystone.dto.TimeLogRequestDTO;
import com.keystone.dto.TimeLogResponseDTO;
import com.keystone.entity.Role;
import com.keystone.entity.Status;
import com.keystone.entity.TimeLog;
import com.keystone.entity.User;
import com.keystone.entity.WorkOrder;
import com.keystone.repository.TimeLogRepository;
import com.keystone.repository.UserRepository;
import com.keystone.repository.WorkOrderRepository;

@Service
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    public TimeLogService(
            TimeLogRepository timeLogRepository,
            WorkOrderRepository workOrderRepository,
            UserRepository userRepository) {

        this.timeLogRepository = timeLogRepository;
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    // CREATE TIME LOG
    @Transactional
    public TimeLogResponseDTO createTimeLog(
            Long workOrderId,
            TimeLogRequestDTO request,
            String loggedInEmail) {

        WorkOrder workOrder = getWorkOrder(workOrderId);

        User loggedInUser = getUser(loggedInEmail);

        User technician = getTechnicianForRequest(
                request.getTechnicianId(),
                loggedInUser);

        validateTechnicianAssignment(
                workOrder,
                technician);

        if (workOrder.getStatus() == Status.CLOSED
                || workOrder.getStatus() == Status.CANCELLED) {

            throw new RuntimeException(
                    "Time logs cannot be added to closed or cancelled work orders");
        }

        if (request.getEndTime() != null
                && request.getEndTime()
                        .isBefore(request.getStartTime())) {

            throw new RuntimeException(
                    "End time cannot be before start time");
        }

        if (request.getStartTime()
                .isAfter(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Start time cannot be in the future");
        }

        TimeLog timeLog = new TimeLog();

        timeLog.setWorkOrder(workOrder);
        timeLog.setTechnician(technician);
        timeLog.setStartTime(request.getStartTime());
        timeLog.setEndTime(request.getEndTime());
        timeLog.setNotes(request.getNotes());

        return convertToResponse(
                timeLogRepository.save(timeLog));
    }

    // GET TIME LOGS FOR WORK ORDER
    public List<TimeLogResponseDTO> getTimeLogsByWorkOrder(
            Long workOrderId,
            String loggedInEmail) {

        WorkOrder workOrder = getWorkOrder(workOrderId);

        validateWorkOrderAccess(
                workOrder,
                loggedInEmail);

        return timeLogRepository
                .findByWorkOrderId(workOrderId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET TIME LOGS FOR TECHNICIAN
    public List<TimeLogResponseDTO> getTimeLogsByTechnician(
            Long technicianId,
            String loggedInEmail) {

        User loggedInUser = getUser(loggedInEmail);

        if (loggedInUser.getRole() == Role.TECHNICIAN
                && !loggedInUser.getId()
                        .equals(technicianId)) {

            throw new RuntimeException(
                    "You are not authorized to access another technician's time logs");
        }

        User technician = userRepository
                .findById(technicianId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Technician not found"));

        if (technician.getRole() != Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Selected user is not a technician");
        }

        return timeLogRepository
                .findByTechnicianId(technicianId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET TIME LOG BY ID
    public TimeLogResponseDTO getTimeLogById(
            Long id,
            String loggedInEmail) {

        TimeLog timeLog = timeLogRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Time log not found"));

        validateWorkOrderAccess(
                timeLog.getWorkOrder(),
                loggedInEmail);

        return convertToResponse(timeLog);
    }

    private WorkOrder getWorkOrder(Long workOrderId) {

        return workOrderRepository
                .findById(workOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));
    }

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));
    }

    private User getTechnicianForRequest(
            Long requestedTechnicianId,
            User loggedInUser) {

        if (loggedInUser.getRole() == Role.TECHNICIAN) {

            if (!loggedInUser.getId()
                    .equals(requestedTechnicianId)) {

                throw new RuntimeException(
                        "Technicians can only create time logs for themselves");
            }

            return loggedInUser;
        }

        User technician = userRepository
                .findById(requestedTechnicianId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Technician not found"));

        if (technician.getRole() != Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Selected user is not a technician");
        }

        if (loggedInUser.getRole() != Role.MANAGER
                && loggedInUser.getRole() != Role.DISPATCHER) {

            throw new RuntimeException(
                    "You are not authorized to create time logs");
        }

        return technician;
    }

    private void validateTechnicianAssignment(
            WorkOrder workOrder,
            User technician) {

        if (workOrder.getAssignedTechnician() == null
                || !workOrder.getAssignedTechnician()
                        .getId()
                        .equals(technician.getId())) {

            throw new RuntimeException(
                    "This technician is not assigned to this work order");
        }
    }

    private void validateWorkOrderAccess(
            WorkOrder workOrder,
            String loggedInEmail) {

        User loggedInUser = getUser(loggedInEmail);

        if (loggedInUser.getRole() == Role.MANAGER
                || loggedInUser.getRole() == Role.DISPATCHER) {

            return;
        }

        if (loggedInUser.getRole() != Role.TECHNICIAN) {

            throw new RuntimeException(
                    "You are not authorized to access this time log");
        }

        if (workOrder.getAssignedTechnician() == null
                || !workOrder.getAssignedTechnician()
                        .getId()
                        .equals(loggedInUser.getId())) {

            throw new RuntimeException(
                    "This work order is not assigned to this technician");
        }
    }

    private TimeLogResponseDTO convertToResponse(
            TimeLog timeLog) {

        WorkOrder workOrder =
                timeLog.getWorkOrder();

        User technician =
                timeLog.getTechnician();

        return new TimeLogResponseDTO(
                timeLog.getId(),
                workOrder.getId(),
                workOrder.getCode(),
                technician.getId(),
                technician.getName(),
                timeLog.getStartTime(),
                timeLog.getEndTime(),
                timeLog.getNotes());
    }
}