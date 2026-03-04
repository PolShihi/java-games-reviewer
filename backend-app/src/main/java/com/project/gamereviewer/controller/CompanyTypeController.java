package com.project.gamereviewer.controller;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.response.CompanyTypeResponse;
import com.project.gamereviewer.service.CompanyTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.COMPANY_TYPES)
@RequiredArgsConstructor
@Tag(name = "Company Types", description = "Operations related to company types (read-only)")
public class CompanyTypeController {
    
    private final CompanyTypeService companyTypeService;
    
    @GetMapping
    @Operation(summary = "Get all company types", description = "Returns list of all company types")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved company types")
    public ResponseEntity<List<CompanyTypeResponse>> getAllCompanyTypes() {
        return ResponseEntity.ok(companyTypeService.getAllCompanyTypes());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get company type by ID", description = "Returns a specific company type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved company type"),
        @ApiResponse(responseCode = "404", description = "Company type not found")
    })
    public ResponseEntity<CompanyTypeResponse> getCompanyTypeById(
            @Parameter(description = "Company Type ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(companyTypeService.getCompanyTypeById(id));
    }
}
