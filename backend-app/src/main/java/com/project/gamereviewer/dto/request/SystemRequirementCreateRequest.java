package com.project.gamereviewer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SystemRequirementCreateRequest(
    @NotNull(message = "Game ID is required")
    Integer gameId,
    
    @NotNull(message = "System requirement type ID is required")
    Integer systemRequirementTypeId,
    
    @NotNull(message = "Storage GB is required")
    @Min(value = 1, message = "Storage must be at least 1 GB")
    Integer storageGb,
    
    @NotNull(message = "RAM GB is required")
    @Min(value = 1, message = "RAM must be at least 1 GB")
    Integer ramGb,
    
    BigDecimal cpuGhz,
    
    BigDecimal gpuTflops,
    
    Integer vramGb
) {}
