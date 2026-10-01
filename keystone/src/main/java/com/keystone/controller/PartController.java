package com.keystone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.keystone.dto.PartRequestDTO;
import com.keystone.dto.PartResponseDTO;
import com.keystone.service.PartService;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    // CREATE PART
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<PartResponseDTO> createPart(
            @Valid @RequestBody PartRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(partService.createPart(request));
    }

    // GET ALL PARTS
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<List<PartResponseDTO>> getAllParts() {

        return ResponseEntity.ok(
                partService.getAllParts());
    }

    // GET PART BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER', 'TECHNICIAN')")
    public ResponseEntity<PartResponseDTO> getPartById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                partService.getPartById(id));
    }

    // UPDATE PART
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'DISPATCHER')")
    public ResponseEntity<PartResponseDTO> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody PartRequestDTO request) {

        return ResponseEntity.ok(
                partService.updatePart(id, request));
    }

    // DELETE PART
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deletePart(
            @PathVariable Long id) {

        partService.deletePart(id);

        return ResponseEntity.noContent().build();
    }
}