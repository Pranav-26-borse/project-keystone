package com.keystone.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keystone.entity.TimeLog;

public interface TimeLogRepository
        extends JpaRepository<TimeLog, Long> {

    List<TimeLog> findByWorkOrderId(Long workOrderId);

    List<TimeLog> findByTechnicianId(Long technicianId);
}