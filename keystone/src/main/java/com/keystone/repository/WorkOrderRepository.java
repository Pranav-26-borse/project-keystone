package com.keystone.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.keystone.entity.Status;
import com.keystone.entity.WorkOrder;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    // =========================================================
    // TECHNICIAN WORK ORDERS
    // =========================================================

    List<WorkOrder> findByAssignedTechnicianId(Long technicianId);

    long countByAssignedTechnicianId(Long technicianId);

    long countByAssignedTechnicianIdAndStatus(
            Long technicianId,
            Status status);

    // =========================================================
    // STATUS COUNTS
    // =========================================================

    long countByStatus(Status status);

    // =========================================================
    // CUSTOMER WORK ORDERS
    // =========================================================

    List<WorkOrder> findByCustomerId(Long customerId);

    // =========================================================
    // PAGINATION
    // =========================================================

    @Query("SELECT w FROM WorkOrder w")
    Page<WorkOrder> findAllWorkOrders(Pageable pageable);

    // =========================================================
    // SEARCH + PAGINATION
    // =========================================================

    @Query("""
            SELECT w
            FROM WorkOrder w
            WHERE LOWER(w.code) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(w.title) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(w.description) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<WorkOrder> searchWorkOrders(
            @Param("search") String search,
            Pageable pageable);

    // =========================================================
    // WORK ORDER CODE GENERATION
    // =========================================================

    @Query("""
            SELECT COUNT(w)
            FROM WorkOrder w
            """)
    long getNextWorkOrderCodeNumber();
}