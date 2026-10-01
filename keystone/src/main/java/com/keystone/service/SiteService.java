package com.keystone.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.keystone.dto.SiteRequestDTO;
import com.keystone.dto.SiteResponseDTO;
import com.keystone.entity.Customer;
import com.keystone.entity.Role;
import com.keystone.entity.Site;
import com.keystone.entity.User;
import com.keystone.repository.CustomerRepository;
import com.keystone.repository.SiteRepository;
import com.keystone.repository.UserRepository;

@Service
public class SiteService {

    private final SiteRepository siteRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SiteService(
            SiteRepository siteRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository) {

        this.siteRepository = siteRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    // MANAGER / DISPATCHER
    public SiteResponseDTO createSite(
            Long customerId,
            SiteRequestDTO request) {

        Customer customer = getCustomer(customerId);

        Site site = new Site();

        site.setName(request.getName());
        site.setAddress(request.getAddress());
        site.setPhone(request.getPhone());
        site.setCustomer(customer);

        Site savedSite = siteRepository.save(site);

        return convertToResponse(savedSite);
    }

    // MANAGER / DISPATCHER
    public List<SiteResponseDTO> getSitesByCustomer(
            Long customerId) {

        getCustomer(customerId);

        return siteRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // CUSTOMER - ONLY THEIR OWN SITES
    public List<SiteResponseDTO> getMySites(
            String loggedInEmail) {

        User user = getCustomerUser(loggedInEmail);

        if (user.getCustomer() == null) {
            throw new RuntimeException(
                    "No customer organization linked to this account");
        }

        Long customerId = user.getCustomer().getId();

        return siteRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // MANAGER / DISPATCHER
    public SiteResponseDTO getSiteById(Long id) {

        Site site = getSite(id);

        return convertToResponse(site);
    }

    // CUSTOMER - ONLY THEIR OWN SITE
    public SiteResponseDTO getMySite(
            Long siteId,
            String loggedInEmail) {

        User user = getCustomerUser(loggedInEmail);

        if (user.getCustomer() == null) {
            throw new RuntimeException(
                    "No customer organization linked to this account");
        }

        Site site = getSite(siteId);

        if (!site.getCustomer().getId()
                .equals(user.getCustomer().getId())) {

            throw new RuntimeException(
                    "You are not authorized to access this site");
        }

        return convertToResponse(site);
    }

    // MANAGER / DISPATCHER
    public SiteResponseDTO updateSite(
            Long id,
            SiteRequestDTO request) {

        Site site = getSite(id);

        site.setName(request.getName());
        site.setAddress(request.getAddress());
        site.setPhone(request.getPhone());

        Site updatedSite = siteRepository.save(site);

        return convertToResponse(updatedSite);
    }

    private Customer getCustomer(Long customerId) {

        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Customer not found"));
    }

    private Site getSite(Long siteId) {

        return siteRepository.findById(siteId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Site not found"));
    }

    private User getCustomerUser(
            String loggedInEmail) {

        User user = userRepository
                .findByEmail(loggedInEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));

        if (user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException(
                    "Logged-in user is not a customer");
        }

        return user;
    }

    private SiteResponseDTO convertToResponse(
            Site site) {

        return new SiteResponseDTO(
                site.getId(),
                site.getName(),
                site.getAddress(),
                site.getPhone(),
                site.getCustomer().getId(),
                site.getCreatedAt());
    }
}