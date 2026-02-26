package com.project.gamesreviewer.model;

public record Review(
    int id,
    int gameId,
    int mediaOutletId,
    int score,
    String summary,
    String mediaOutletName,
    String gameTitle
) {
    public Review {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
    }
    
    public Review(int id, int gameId, int mediaOutletId, int score, String summary) {
        this(id, gameId, mediaOutletId, score, summary, null, null);
    }
    
    public Review(int id, int gameId, int mediaOutletId, int score, String summary, String mediaOutletName) {
        this(id, gameId, mediaOutletId, score, summary, mediaOutletName, null);
    }
    
    public Review withMediaOutletName(String mediaOutletName) {
        return new Review(id, gameId, mediaOutletId, score, summary, mediaOutletName, gameTitle);
    }
    
    public Review withGameTitle(String gameTitle) {
        return new Review(id, gameId, mediaOutletId, score, summary, mediaOutletName, gameTitle);
    }
}
