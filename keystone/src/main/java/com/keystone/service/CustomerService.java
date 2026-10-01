package com.keystone.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.keystone.dto.CustomerRequestDTO;
import com.keystone.dto.CustomerResponseDTO;
import com.keystone.entity.Customer;
import com.keystone.entity.Role;
import com.keystone.entity.User;
import com.keystone.repository.CustomerRepository;
import com.keystone.repository.UserRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            UserRepository userRepository) {

        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    // MANAGER / DISPATCHER
    public CustomerResponseDTO createCustomer(
            CustomerRequestDTO request) {

        if (request.getEmail() != null
                && !request.getEmail().isBlank()
                && customerRepository.existsByEmail(
                        request.getEmail())) {

            throw new RuntimeException(
                    "Customer email already exists");
        }

        Customer customer = new Customer();

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer savedCustomer =
                customerRepository.save(customer);

        return convertToResponse(savedCustomer);
    }

    // MANAGER / DISPATCHER
    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"));

        return convertToResponse(customer);
    }

    // CUSTOMER - ONLY THEIR OWN CUSTOMER
    public CustomerResponseDTO getMyCustomer(
            String loggedInEmail) {

        User user = getCustomerUser(loggedInEmail);

        if (user.getCustomer() == null) {
            throw new RuntimeException(
                    "No customer organization linked to this account");
        }

        return convertToResponse(user.getCustomer());
    }

    // MANAGER / DISPATCHER
    public CustomerResponseDTO updateCustomer(
            Long id,
            CustomerRequestDTO request) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"));

        if (request.getEmail() != null
                && !request.getEmail().isBlank()
                && !request.getEmail()
                        .equalsIgnoreCase(customer.getEmail())
                && customerRepository.existsByEmail(
                        request.getEmail())) {

            throw new RuntimeException(
                    "Customer email already exists");
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer updatedCustomer =
                customerRepository.save(customer);

        return convertToResponse(updatedCustomer);
    }

    // MANAGER / DISPATCHER
    public Page<CustomerResponseDTO> getCustomers(
            String search,
            int page,
            int size) {

        if (page < 0) {
            throw new RuntimeException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new RuntimeException(
                    "Page size must be between 1 and 100");
        }

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Customer> customers;

        if (search == null || search.isBlank()) {

            customers =
                    customerRepository.findAll(pageable);

        } else {

            customers =
                    customerRepository
                            .findByNameContainingIgnoreCase(
                                    search.trim(),
                                    pageable);
        }

        return customers.map(
                this::convertToResponse);
    }

    private User getCustomerUser(
            String loggedInEmail) {

        User user =
                userRepository.findByEmail(loggedInEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Logged-in user not found"));

        if (user.getRole() != Role.CUSTOMER) {
            throw new RuntimeException(
                    "Logged-in user is not a customer");
        }

        return user;
    }

    private CustomerResponseDTO convertToResponse(
            Customer customer) {

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt());
    }
}