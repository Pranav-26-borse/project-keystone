package com.keystone.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.keystone.dto.PartRequestDTO;
import com.keystone.dto.PartResponseDTO;
import com.keystone.entity.Part;
import com.keystone.repository.PartRepository;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    // CREATE PART
    public PartResponseDTO createPart(
            PartRequestDTO request) {

        if (partRepository
                .findByPartNumber(request.getPartNumber())
                .isPresent()) {

            throw new RuntimeException(
                    "Part number already exists");
        }

        Part part = new Part();

        part.setName(request.getName());
        part.setPartNumber(request.getPartNumber());
        part.setQuantityAvailable(
                request.getQuantityAvailable());
        part.setUnitPrice(
                request.getUnitPrice());

        return convertToResponse(
                partRepository.save(part));
    }

    // GET ALL PARTS
    public List<PartResponseDTO> getAllParts() {

        return partRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET PART BY ID
    public PartResponseDTO getPartById(Long id) {

        Part part = partRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Part not found"));

        return convertToResponse(part);
    }

    // UPDATE PART
    public PartResponseDTO updatePart(
            Long id,
            PartRequestDTO request) {

        Part part = partRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Part not found"));

        partRepository
                .findByPartNumber(request.getPartNumber())
                .ifPresent(existingPart -> {

                    if (!existingPart.getId().equals(id)) {

                        throw new RuntimeException(
                                "Part number already exists");
                    }
                });

        part.setName(request.getName());
        part.setPartNumber(request.getPartNumber());
        part.setQuantityAvailable(
                request.getQuantityAvailable());
        part.setUnitPrice(
                request.getUnitPrice());

        return convertToResponse(
                partRepository.save(part));
    }

    // DELETE PART
    public void deletePart(Long id) {

        Part part = partRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Part not found"));

        partRepository.delete(part);
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private PartResponseDTO convertToResponse(
            Part part) {

        return new PartResponseDTO(
                part.getId(),
                part.getName(),
                part.getPartNumber(),
                part.getQuantityAvailable(),
                part.getUnitPrice(),
                part.getCreatedAt()
        );
    }
}