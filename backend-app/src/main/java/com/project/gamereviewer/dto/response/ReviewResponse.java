package com.project.gamereviewer.dto.response;

public record ReviewResponse(
    Integer id,
    Integer gameId,
    String gameTitle,
    MediaOutletResponse mediaOutlet,
    Integer score,
    String summary
) {}
