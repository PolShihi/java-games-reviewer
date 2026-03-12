package com.project.gamereviewer.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
    
    @DecimalMin(value = "0.0", message = "CPU GHz must be non-negative")
    @DecimalMax(value = "99.9", message = "CPU GHz must not exceed 99.9")
    @Digits(integer = 2, fraction = 1, message = "CPU GHz must have up to 2 integer digits and 1 decimal place")
    BigDecimal cpuGhz,
    
    @DecimalMin(value = "0.0", message = "GPU TFLOPS must be non-negative")
    @DecimalMax(value = "99.99", message = "GPU TFLOPS must not exceed 99.99")
    @Digits(integer = 2, fraction = 2, message = "GPU TFLOPS must have up to 2 integer digits and 2 decimal places")
    BigDecimal gpuTflops,
    
    Integer vramGb
) {}
