package com.project.gamereviewer.dto.response;

import java.util.List;

public record GameListResponse(
    Integer id,
    String title,
    Integer releaseYear,
    String developerName,
    String publisherName,
    List<String> genreNames,
    Double averageRating
) {}
