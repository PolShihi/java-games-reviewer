package com.project.gamereviewer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.response.SystemRequirementTypeResponse;
import com.project.gamereviewer.service.SystemRequirementTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiConstants.SYSTEM_REQUIREMENT_TYPES)
@RequiredArgsConstructor
@Tag(name = "System Requirement Types", description = "Operations related to system requirement types (read-only)")
public class SystemRequirementTypeController {
    
    private final SystemRequirementTypeService systemRequirementTypeService;
    
    @GetMapping
    @Operation(summary = "Get all system requirement types", description = "Returns list of all system requirement types")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved system requirement types")
    public ResponseEntity<List<SystemRequirementTypeResponse>> getAllSystemRequirementTypes() {
        return ResponseEntity.ok(systemRequirementTypeService.getAllSystemRequirementTypes());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get system requirement type by ID", description = "Returns a specific system requirement type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved system requirement type"),
        @ApiResponse(responseCode = "404", description = "System requirement type not found")
    })
    public ResponseEntity<SystemRequirementTypeResponse> getSystemRequirementTypeById(
            @Parameter(description = "System Requirement Type ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(systemRequirementTypeService.getSystemRequirementTypeById(id));
    }
}
