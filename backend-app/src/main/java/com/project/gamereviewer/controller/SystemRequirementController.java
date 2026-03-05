package com.project.gamereviewer.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.request.SystemRequirementCreateRequest;
import com.project.gamereviewer.dto.response.PageResponse;
import com.project.gamereviewer.dto.response.SystemRequirementResponse;
import com.project.gamereviewer.service.SystemRequirementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.SYSTEM_REQUIREMENTS)
@RequiredArgsConstructor
@Tag(name = "System Requirements", description = "Operations related to game system requirements")
public class SystemRequirementController {
    
    private final SystemRequirementService systemRequirementService;
    
    @GetMapping
    @Operation(summary = "Get all system requirements", description = "Returns paginated list of all system requirements")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved system requirements")
    public ResponseEntity<PageResponse<SystemRequirementResponse>> getAllSystemRequirements(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<SystemRequirementResponse> requirementsPage = systemRequirementService.getAllSystemRequirements(pageable);
        
        PageResponse<SystemRequirementResponse> response = PageResponse.of(requirementsPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/game/{gameId}")
    @Operation(summary = "Get system requirements by game ID", description = "Returns all system requirements for a specific game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved system requirements"),
        @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<PageResponse<SystemRequirementResponse>> getSystemRequirementsByGameId(
            @Parameter(description = "Game ID") @PathVariable Integer gameId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemRequirementResponse> requirementsPage = systemRequirementService.getSystemRequirementsByGameId(gameId, pageable);
        
        PageResponse<SystemRequirementResponse> response = PageResponse.of(requirementsPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get system requirement by ID", description = "Returns a specific system requirement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved system requirement"),
        @ApiResponse(responseCode = "404", description = "System requirement not found")
    })
    public ResponseEntity<SystemRequirementResponse> getSystemRequirementById(
            @Parameter(description = "System Requirement ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(systemRequirementService.getSystemRequirementById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new system requirement", description = "Creates a new system requirement for a game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "System requirement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Game or System Requirement Type not found"),
        @ApiResponse(responseCode = "409", description = "System requirement already exists for this game and type")
    })
    public ResponseEntity<SystemRequirementResponse> createSystemRequirement(
            @Valid @RequestBody SystemRequirementCreateRequest request
    ) {
        SystemRequirementResponse created = systemRequirementService.createSystemRequirement(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update system requirement", description = "Updates an existing system requirement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "System requirement updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "System requirement not found")
    })
    public ResponseEntity<SystemRequirementResponse> updateSystemRequirement(
            @Parameter(description = "System Requirement ID") @PathVariable Integer id,
            @Valid @RequestBody SystemRequirementCreateRequest request
    ) {
        return ResponseEntity.ok(systemRequirementService.updateSystemRequirement(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete system requirement", description = "Deletes a system requirement by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "System requirement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "System requirement not found")
    })
    public ResponseEntity<Void> deleteSystemRequirement(
            @Parameter(description = "System Requirement ID") @PathVariable Integer id
    ) {
        systemRequirementService.deleteSystemRequirement(id);
        return ResponseEntity.noContent().build();
    }
}
