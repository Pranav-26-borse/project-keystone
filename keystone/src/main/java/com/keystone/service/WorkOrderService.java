package com.keystone.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.keystone.dto.StatusHistoryResponseDTO;
import com.keystone.dto.TechnicianDashboardDTO;
import com.keystone.dto.WorkOrderRequestDTO;
import com.keystone.dto.WorkOrderResponseDTO;
import com.keystone.entity.Customer;
import com.keystone.entity.Role;
import com.keystone.entity.Site;
import com.keystone.entity.Status;
import com.keystone.entity.User;
import com.keystone.entity.WorkOrder;
import com.keystone.entity.WorkOrderStatusHistory;
import com.keystone.repository.CustomerRepository;
import com.keystone.repository.SiteRepository;
import com.keystone.repository.UserRepository;
import com.keystone.repository.WorkOrderRepository;
import com.keystone.repository.WorkOrderStatusHistoryRepository;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final WorkOrderStatusHistoryRepository statusHistoryRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            CustomerRepository customerRepository,
            SiteRepository siteRepository,
            UserRepository userRepository,
            WorkOrderStatusHistoryRepository statusHistoryRepository) {

        this.workOrderRepository = workOrderRepository;
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    // =========================================================
    // CREATE WORK ORDER
    // =========================================================

    @Transactional
    public WorkOrderResponseDTO createWorkOrder(
            WorkOrderRequestDTO request) {

        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));

        Site site = siteRepository
                .findById(request.getSiteId())
                .orElseThrow(() ->
                        new RuntimeException("Site not found"));

        if (!site.getCustomer().getId()
                .equals(customer.getId())) {

            throw new RuntimeException(
                    "Site does not belong to the selected customer");
        }

        WorkOrder workOrder = new WorkOrder();

        workOrder.setCode(generateWorkOrderCode());
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setPriority(request.getPriority());
        workOrder.setStatus(Status.OPEN);
        workOrder.setCustomer(customer);
        workOrder.setSite(site);

        return convertToResponse(
                workOrderRepository.save(workOrder));
    }

    // =========================================================
    // GET ALL WORK ORDERS
    // =========================================================

    public List<WorkOrderResponseDTO> getAllWorkOrders() {

        return workOrderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // SEARCH + PAGINATION
    // =========================================================

    @Transactional(readOnly = true)
    public Page<WorkOrderResponseDTO> searchWorkOrders(
            String search,
            Pageable pageable) {

        Page<WorkOrder> workOrders;

        if (search == null || search.trim().isEmpty()) {

            workOrders =
                    workOrderRepository.findAllWorkOrders(pageable);

        } else {

            workOrders =
                    workOrderRepository.searchWorkOrders(
                            search.trim(),
                            pageable);
        }

        return workOrders.map(this::convertToResponse);
    }

    // =========================================================
    // GET WORK ORDER BY ID
    // =========================================================

    public WorkOrderResponseDTO getWorkOrderById(Long id) {

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        return convertToResponse(workOrder);
    }

    // =========================================================
    // GET WORK ORDERS BY TECHNICIAN
    // =========================================================

    public List<WorkOrderResponseDTO> getWorkOrdersByTechnician(
            Long technicianId) {

        User technician = getTechnician(technicianId);

        return workOrderRepository
                .findByAssignedTechnicianId(
                        technician.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET LOGGED-IN TECHNICIAN WORK ORDERS
    // =========================================================

    public List<WorkOrderResponseDTO>
            getWorkOrdersForLoggedInTechnician(
                    String loggedInEmail) {

        User technician =
                getTechnicianByEmail(loggedInEmail);

        return workOrderRepository
                .findByAssignedTechnicianId(
                        technician.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // CUSTOMER WORK ORDERS
    // =========================================================

    public List<WorkOrderResponseDTO> getCustomerWorkOrders(
            String loggedInEmail) {

        User customerUser =
                getCustomerUser(loggedInEmail);

        if (customerUser.getCustomer() == null) {

            throw new RuntimeException(
                    "Customer account is not linked to a customer");
        }

        Long customerId =
                customerUser.getCustomer().getId();

        return workOrderRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // CUSTOMER WORK ORDER BY ID
    // =========================================================

    public WorkOrderResponseDTO getCustomerWorkOrder(
            Long workOrderId,
            String loggedInEmail) {

        User customerUser =
                getCustomerUser(loggedInEmail);

        if (customerUser.getCustomer() == null) {

            throw new RuntimeException(
                    "Customer account is not linked to a customer");
        }

        WorkOrder workOrder =
                workOrderRepository.findById(workOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        if (workOrder.getCustomer() == null
                || !workOrder.getCustomer()
                        .getId()
                        .equals(customerUser
                                .getCustomer()
                                .getId())) {

            throw new RuntimeException(
                    "You are not authorized to access this work order");
        }

        return convertToResponse(workOrder);
    }

    // =========================================================
    // TECHNICIAN DASHBOARD
    // =========================================================

    public TechnicianDashboardDTO getTechnicianDashboard(
            Long technicianId) {

        User technician =
                getTechnician(technicianId);

        return buildTechnicianDashboard(technician);
    }

    public TechnicianDashboardDTO
            getLoggedInTechnicianDashboard(
                    String loggedInEmail) {

        User technician =
                getTechnicianByEmail(loggedInEmail);

        return buildTechnicianDashboard(technician);
    }

    // =========================================================
    // UPDATE WORK ORDER
    // =========================================================

    public WorkOrderResponseDTO updateWorkOrder(
            Long id,
            WorkOrderRequestDTO request) {

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        if (workOrder.getStatus() == Status.CLOSED
                || workOrder.getStatus() == Status.CANCELLED) {

            throw new RuntimeException(
                    "Closed or cancelled work orders cannot be edited");
        }

        Customer customer =
                customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));

        Site site =
                siteRepository
                .findById(request.getSiteId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Site not found"));

        if (!site.getCustomer().getId()
                .equals(customer.getId())) {

            throw new RuntimeException(
                    "Site does not belong to the selected customer");
        }

        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setPriority(request.getPriority());
        workOrder.setCustomer(customer);
        workOrder.setSite(site);

        return convertToResponse(
                workOrderRepository.save(workOrder));
    }

    // =========================================================
    // UPDATE STATUS - MANAGER / DISPATCHER
    // =========================================================

    @Transactional
    public WorkOrderResponseDTO updateStatus(
            Long id,
            Status newStatus) {

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        Status oldStatus =
                workOrder.getStatus();

        validateStatusTransition(
                oldStatus,
                newStatus);

        workOrder.setStatus(newStatus);

        WorkOrder savedWorkOrder =
                workOrderRepository.save(workOrder);

        saveStatusHistory(
                savedWorkOrder,
                oldStatus,
                newStatus,
                null);

        return convertToResponse(
                savedWorkOrder);
    }

    // =========================================================
    // OLD TECHNICIAN STATUS METHOD
    // =========================================================

    public WorkOrderResponseDTO updateTechnicianStatus(
            Long workOrderId,
            Long technicianId,
            Status newStatus) {

        WorkOrder workOrder =
                workOrderRepository.findById(workOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        User technician =
                getTechnician(technicianId);

        validateTechnicianAssignment(
                workOrder,
                technician);

        validateTechnicianStatusChange(
                workOrder,
                newStatus);

        Status oldStatus =
                workOrder.getStatus();

        workOrder.setStatus(newStatus);

        WorkOrder savedWorkOrder =
                workOrderRepository.save(workOrder);

        saveStatusHistory(
                savedWorkOrder,
                oldStatus,
                newStatus,
                technician);

        return convertToResponse(
                savedWorkOrder);
    }

    // =========================================================
    // LOGGED-IN TECHNICIAN STATUS
    // =========================================================

    @Transactional
    public WorkOrderResponseDTO
            updateLoggedInTechnicianStatus(
                    Long workOrderId,
                    String loggedInEmail,
                    Status newStatus) {

        WorkOrder workOrder =
                workOrderRepository.findById(workOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        User technician =
                getTechnicianByEmail(loggedInEmail);

        validateTechnicianAssignment(
                workOrder,
                technician);

        validateTechnicianStatusChange(
                workOrder,
                newStatus);

        Status oldStatus =
                workOrder.getStatus();

        workOrder.setStatus(newStatus);

        WorkOrder savedWorkOrder =
                workOrderRepository.save(workOrder);

        saveStatusHistory(
                savedWorkOrder,
                oldStatus,
                newStatus,
                technician);

        return convertToResponse(
                savedWorkOrder);
    }

    // =========================================================
    // ASSIGN TECHNICIAN
    // =========================================================

    @Transactional
    public WorkOrderResponseDTO assignTechnician(
            Long workOrderId,
            Long technicianId) {

        WorkOrder workOrder =
                workOrderRepository.findById(workOrderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        if (workOrder.getStatus() == Status.CLOSED
                || workOrder.getStatus() == Status.CANCELLED) {

            throw new RuntimeException(
                    "Closed or cancelled work orders cannot be assigned");
        }

        User technician =
                getTechnician(technicianId);

        workOrder.setAssignedTechnician(
                technician);

        if (workOrder.getStatus() == Status.OPEN) {

            workOrder.setStatus(
                    Status.ASSIGNED);
        }

        return convertToResponse(
                workOrderRepository.save(workOrder));
    }

    // =========================================================
    // DELETE WORK ORDER
    // =========================================================

    @Transactional
    public void deleteWorkOrder(Long id) {

        WorkOrder workOrder =
                workOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Work order not found"));

        if (workOrder.getStatus() == Status.CLOSED) {

            throw new RuntimeException(
                    "Closed work orders cannot be deleted");
        }

        workOrderRepository.delete(workOrder);
    }

    // =========================================================
    // STATUS HISTORY
    // =========================================================

    public List<StatusHistoryResponseDTO>
            getStatusHistory(Long workOrderId) {

        if (!workOrderRepository.existsById(
                workOrderId)) {

            throw new RuntimeException(
                    "Work order not found");
        }

        return statusHistoryRepository
                .findByWorkOrderIdOrderByChangedAtAsc(
                        workOrderId)
                .stream()
                .map(history -> {

                    Long changedById = null;
                    String changedByName = null;

                    if (history.getChangedBy() != null) {

                        changedById =
                                history.getChangedBy().getId();

                        changedByName =
                                history.getChangedBy().getName();
                    }

                    return new StatusHistoryResponseDTO(
                            history.getId(),
                            history.getWorkOrder().getId(),
                            history.getOldStatus(),
                            history.getNewStatus(),
                            changedById,
                            changedByName,
                            history.getChangedAt());

                })
                .toList();
    }

    // =========================================================
    // GET TECHNICIAN
    // =========================================================

    private User getTechnician(
            Long technicianId) {

        User technician =
                userRepository.findById(technicianId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Technician not found"));

        if (technician.getRole()
                != Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Selected user is not a technician");
        }

        return technician;
    }

    private User getTechnicianByEmail(
            String email) {

        User technician =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));

        if (technician.getRole()
                != Role.TECHNICIAN) {

            throw new RuntimeException(
                    "Logged-in user is not a technician");
        }

        return technician;
    }

    // =========================================================
    // GET CUSTOMER USER
    // =========================================================

    private User getCustomerUser(
            String email) {

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));

        if (user.getRole() != Role.CUSTOMER) {

            throw new RuntimeException(
                    "Only customers can access this endpoint");
        }

        return user;
    }

    // =========================================================
    // TECHNICIAN ASSIGNMENT VALIDATION
    // =========================================================

    private void validateTechnicianAssignment(
            WorkOrder workOrder,
            User technician) {

        if (workOrder.getAssignedTechnician() == null
                || !workOrder.getAssignedTechnician()
                        .getId()
                        .equals(technician.getId())) {

            throw new RuntimeException(
                    "This work order is not assigned to this technician");
        }
    }

    // =========================================================
    // TECHNICIAN STATUS VALIDATION
    // =========================================================

    private void validateTechnicianStatusChange(
            WorkOrder workOrder,
            Status newStatus) {

        if (workOrder.getStatus() == Status.CLOSED
                || workOrder.getStatus()
                        == Status.CANCELLED) {

            throw new RuntimeException(
                    "Closed or cancelled work orders cannot be changed");
        }

        if (newStatus != Status.IN_PROGRESS
                && newStatus != Status.COMPLETED) {

            throw new RuntimeException(
                    "Technician can only change status to IN_PROGRESS or COMPLETED");
        }

        if (workOrder.getStatus() == Status.ASSIGNED
                && newStatus != Status.IN_PROGRESS) {

            throw new RuntimeException(
                    "An ASSIGNED work order must move to IN_PROGRESS first");
        }

        if (workOrder.getStatus() == Status.IN_PROGRESS
                && newStatus != Status.COMPLETED) {

            throw new RuntimeException(
                    "An IN_PROGRESS work order must move to COMPLETED");
        }
    }

    // =========================================================
    // STATUS TRANSITION VALIDATION
    // =========================================================

    private void validateStatusTransition(
            Status oldStatus,
            Status newStatus) {

        if (newStatus == null) {

            throw new RuntimeException(
                    "New status cannot be null");
        }

        if (oldStatus == newStatus) {

            throw new RuntimeException(
                    "Work order is already in this status");
        }

        if (oldStatus == Status.CLOSED
                || oldStatus == Status.CANCELLED) {

            throw new RuntimeException(
                    "Closed or cancelled work orders cannot be changed");
        }

        boolean validTransition = false;

        switch (oldStatus) {

            case OPEN:

                validTransition =
                        newStatus == Status.ASSIGNED
                        || newStatus == Status.CANCELLED;

                break;

            case ASSIGNED:

                validTransition =
                        newStatus == Status.IN_PROGRESS
                        || newStatus == Status.OPEN
                        || newStatus == Status.CANCELLED;

                break;

            case IN_PROGRESS:

                validTransition =
                        newStatus == Status.COMPLETED
                        || newStatus == Status.CANCELLED;

                break;

            case COMPLETED:

                validTransition =
                        newStatus == Status.CLOSED;

                break;

            default:

                validTransition = false;
        }

        if (!validTransition) {

            throw new RuntimeException(
                    "Invalid status transition from "
                    + oldStatus
                    + " to "
                    + newStatus);
        }
    }

    // =========================================================
    // TECHNICIAN DASHBOARD
    // =========================================================

    private TechnicianDashboardDTO
            buildTechnicianDashboard(
                    User technician) {

        long totalAssigned =
                workOrderRepository
                .countByAssignedTechnicianId(
                        technician.getId());

        long assigned =
                workOrderRepository
                .countByAssignedTechnicianIdAndStatus(
                        technician.getId(),
                        Status.ASSIGNED);

        long inProgress =
                workOrderRepository
                .countByAssignedTechnicianIdAndStatus(
                        technician.getId(),
                        Status.IN_PROGRESS);

        long completed =
                workOrderRepository
                .countByAssignedTechnicianIdAndStatus(
                        technician.getId(),
                        Status.COMPLETED);

        return new TechnicianDashboardDTO(
                technician.getId(),
                technician.getName(),
                totalAssigned,
                assigned,
                inProgress,
                completed);
    }

    // =========================================================
    // SAVE STATUS HISTORY
    // =========================================================

    private void saveStatusHistory(
            WorkOrder workOrder,
            Status oldStatus,
            Status newStatus,
            User changedBy) {

        WorkOrderStatusHistory history =
                new WorkOrderStatusHistory();

        history.setWorkOrder(workOrder);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);

        statusHistoryRepository.save(history);
    }

    // =========================================================
    // GENERATE WORK ORDER CODE
    // =========================================================

    private String generateWorkOrderCode() {

        long nextNumber =
                workOrderRepository
                .getNextWorkOrderCodeNumber();

        return String.format(
                "WO-%d-%05d",
                java.time.LocalDate.now().getYear(),
                nextNumber);
    }

    // =========================================================
    // CONVERT TO RESPONSE DTO
    // =========================================================

    private WorkOrderResponseDTO
            convertToResponse(
                    WorkOrder workOrder) {

        Long technicianId = null;
        String technicianName = null;

        if (workOrder.getAssignedTechnician()
                != null) {

            technicianId =
                    workOrder
                    .getAssignedTechnician()
                    .getId();

            technicianName =
                    workOrder
                    .getAssignedTechnician()
                    .getName();
        }

        return new WorkOrderResponseDTO(
                workOrder.getId(),
                workOrder.getCode(),
                workOrder.getTitle(),
                workOrder.getDescription(),
                workOrder.getPriority(),
                workOrder.getStatus(),
                workOrder.getCustomer().getId(),
                workOrder.getSite().getId(),
                technicianId,
                technicianName,
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt());
    }
}