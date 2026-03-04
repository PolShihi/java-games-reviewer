package com.project.gamereviewer.controller;

import com.project.gamereviewer.constant.ApiConstants;
import com.project.gamereviewer.dto.request.ProductionCompanyCreateRequest;
import com.project.gamereviewer.dto.response.PageResponse;
import com.project.gamereviewer.dto.response.ProductionCompanyResponse;
import com.project.gamereviewer.service.ProductionCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.PRODUCTION_COMPANIES)
@RequiredArgsConstructor
@Tag(name = "Production Companies", description = "Operations related to game production companies")
public class ProductionCompanyController {
    
    private final ProductionCompanyService productionCompanyService;
    
    @GetMapping
    @Operation(summary = "Get all production companies", description = "Returns paginated list of production companies")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved companies")
    public ResponseEntity<PageResponse<ProductionCompanyResponse>> getAllCompanies(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)") @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ProductionCompanyResponse> companiesPage = productionCompanyService.getAllCompanies(pageable);
        
        PageResponse<ProductionCompanyResponse> response = PageResponse.of(companiesPage);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get production company by ID", description = "Returns a specific production company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved company"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<ProductionCompanyResponse> getCompanyById(
            @Parameter(description = "Company ID") @PathVariable Integer id
    ) {
        return ResponseEntity.ok(productionCompanyService.getCompanyById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create new production company", description = "Creates a new production company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Company created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Company already exists")
    })
    public ResponseEntity<ProductionCompanyResponse> createCompany(
            @Valid @RequestBody ProductionCompanyCreateRequest request
    ) {
        ProductionCompanyResponse created = productionCompanyService.createCompany(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update production company", description = "Updates an existing production company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "409", description = "Company name already exists")
    })
    public ResponseEntity<ProductionCompanyResponse> updateCompany(
            @Parameter(description = "Company ID") @PathVariable Integer id,
            @Valid @RequestBody ProductionCompanyCreateRequest request
    ) {
        return ResponseEntity.ok(productionCompanyService.updateCompany(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete production company", description = "Deletes a production company by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "Company ID") @PathVariable Integer id
    ) {
        productionCompanyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
