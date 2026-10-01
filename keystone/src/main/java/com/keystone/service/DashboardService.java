package com.keystone.service;

import org.springframework.stereotype.Service;

import com.keystone.dto.DashboardDTO;
import com.keystone.entity.Role;
import com.keystone.entity.Status;
import com.keystone.repository.CustomerRepository;
import com.keystone.repository.SiteRepository;
import com.keystone.repository.UserRepository;
import com.keystone.repository.WorkOrderRepository;

@Service
public class DashboardService {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;

    public DashboardService(
            WorkOrderRepository workOrderRepository,
            CustomerRepository customerRepository,
            SiteRepository siteRepository,
            UserRepository userRepository) {

        this.workOrderRepository = workOrderRepository;
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.userRepository = userRepository;
    }

    public DashboardDTO getDashboard() {

        long totalWorkOrders = workOrderRepository.count();

        long open = workOrderRepository.countByStatus(Status.OPEN);
        long assigned = workOrderRepository.countByStatus(Status.ASSIGNED);
        long inProgress = workOrderRepository.countByStatus(Status.IN_PROGRESS);
        long completed = workOrderRepository.countByStatus(Status.COMPLETED);
        long closed = workOrderRepository.countByStatus(Status.CLOSED);
        long cancelled = workOrderRepository.countByStatus(Status.CANCELLED);

        long totalCustomers = customerRepository.count();
        long totalSites = siteRepository.count();

        long totalTechnicians = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.TECHNICIAN)
                .count();

        return new DashboardDTO(
                totalWorkOrders,
                open,
                assigned,
                inProgress,
                completed,
                closed,
                cancelled,
                totalCustomers,
                totalSites,
                totalTechnicians
        );
    }
}