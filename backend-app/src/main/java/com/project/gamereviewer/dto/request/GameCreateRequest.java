package com.project.gamereviewer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GameCreateRequest(
    @NotBlank(message = "Game title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    String title,
    
    @Min(value = 1950, message = "Release year must be at least 1950")
    Integer releaseYear,
    
    String description,
    
    Integer developerId,
    
    Integer publisherId,
    
    List<Integer> genreIds
) {}
