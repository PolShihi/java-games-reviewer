package com.project.gamereviewer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
    @NotNull(message = "Game ID is required")
    Integer gameId,
    
    @NotNull(message = "Media outlet ID is required")
    Integer mediaOutletId,
    
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must not exceed 100")
    Integer score,
    
    String summary
) {}
