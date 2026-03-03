package com.project.gamereviewer.dto.response;

public record MediaOutletResponse(
    Integer id,
    String name,
    String websiteUrl,
    Integer foundedYear
) {}
