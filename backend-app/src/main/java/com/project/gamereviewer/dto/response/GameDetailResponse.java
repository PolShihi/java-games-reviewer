package com.project.gamereviewer.dto.response;

import java.util.List;

public record GameDetailResponse(
    Integer id,
    String title,
    Integer releaseYear,
    String description,
    ProductionCompanyResponse developer,
    ProductionCompanyResponse publisher,
    List<GenreResponse> genres,
    List<SystemRequirementResponse> systemRequirements,
    List<ReviewResponse> reviews,
    Double averageRating
) {}
