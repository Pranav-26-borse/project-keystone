package com.keystone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.keystone.dto.SiteRequestDTO;
import com.keystone.dto.SiteResponseDTO;
import com.keystone.service.SiteService;

@RestController
@RequestMapping("/api/customers/{customerId}/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    // MANAGER / DISPATCHER
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<SiteResponseDTO> createSite(
            @PathVariable Long customerId,
            @Valid @RequestBody SiteRequestDTO request) {

        SiteResponseDTO response =
                siteService.createSite(
                        customerId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // MANAGER / DISPATCHER
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<List<SiteResponseDTO>> getSites(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                siteService.getSitesByCustomer(
                        customerId));
    }

    // CUSTOMER - THEIR OWN SITES
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<SiteResponseDTO>> getMySites(
            Authentication authentication) {

        return ResponseEntity.ok(
                siteService.getMySites(
                        authentication.getName()));
    }

    // MANAGER / DISPATCHER
    @GetMapping("/{siteId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<SiteResponseDTO> getSite(
            @PathVariable Long customerId,
            @PathVariable Long siteId) {

        SiteResponseDTO site =
                siteService.getSiteById(siteId);

        if (!site.getCustomerId().equals(customerId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(site);
    }

    // CUSTOMER - THEIR OWN SITE
    @GetMapping("/{siteId}/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<SiteResponseDTO> getMySite(
            @PathVariable Long customerId,
            @PathVariable Long siteId,
            Authentication authentication) {

        SiteResponseDTO site =
                siteService.getMySite(
                        siteId,
                        authentication.getName());

        if (!site.getCustomerId().equals(customerId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(site);
    }

    // MANAGER / DISPATCHER
    @PutMapping("/{siteId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<SiteResponseDTO> updateSite(
            @PathVariable Long customerId,
            @PathVariable Long siteId,
            @Valid @RequestBody SiteRequestDTO request) {

        SiteResponseDTO site =
                siteService.updateSite(
                        siteId,
                        request);

        if (!site.getCustomerId().equals(customerId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(site);
    }
}