package com.project.gamereviewer.dto.filter;

import java.util.List;

public record GameFilterDto(
    String title,
    Integer yearFrom,
    Integer yearTo,
    List<Integer> genreIds,
    Integer developerId,
    Integer publisherId,
    Double ratingFrom,
    Double ratingTo
) {}
