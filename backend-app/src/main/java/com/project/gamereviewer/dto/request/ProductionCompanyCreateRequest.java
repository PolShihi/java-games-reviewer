package com.project.gamereviewer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductionCompanyCreateRequest(
    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,
    
    @Min(value = 1900, message = "Founded year must be at least 1900")
    Integer foundedYear,
    
    @Size(max = 255, message = "Website URL must not exceed 255 characters")
    String websiteUrl,
    
    @Size(max = 100, message = "CEO name must not exceed 100 characters")
    String ceo,
    
    Integer companyTypeId
) {}
