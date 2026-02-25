package com.project.gamesreviewer.model;

import java.util.List;

public record Game(
    int id,
    String title,
    int releaseYear,
    String description,
    int developerId,
    int publisherId,
    String developerName,
    String publisherName,
    List<String> genres,
    Double averageRating
) {
    public Game {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (releaseYear < 1950 || releaseYear > 2100) {
            throw new IllegalArgumentException("Release year must be between 1950 and 2100");
        }
    }
    
    public Game(int id, String title, int releaseYear, String description,
                int developerId, int publisherId) {
        this(id, title, releaseYear, description, developerId, publisherId,
             null, null, null, null);
    }
    
    public Game(int id, String title, int releaseYear, String description,
                int developerId, int publisherId, 
                String developerName, String publisherName) {
        this(id, title, releaseYear, description, developerId, publisherId,
             developerName, publisherName, null, null);
    }
    
    public Game withDeveloperName(String developerName) {
        return new Game(id, title, releaseYear, description,
                       developerId, publisherId, developerName, publisherName,
                       genres, averageRating);
    }
    
    public Game withPublisherName(String publisherName) {
        return new Game(id, title, releaseYear, description,
                       developerId, publisherId, developerName, publisherName,
                       genres, averageRating);
    }
    
    public Game withGenres(List<String> genres) {
        return new Game(id, title, releaseYear, description,
                       developerId, publisherId, developerName, publisherName,
                       genres, averageRating);
    }
    
    public Game withAverageRating(Double averageRating) {
        return new Game(id, title, releaseYear, description,
                       developerId, publisherId, developerName, publisherName,
                       genres, averageRating);
    }
    
    @Override
    public String toString() {
        return String.format("Game{id=%d, title='%s', year=%d, developer='%s', publisher='%s'}",
                id, title, releaseYear,
                developerName != null ? developerName : "ID:" + developerId,
                publisherName != null ? publisherName : "ID:" + publisherId);
    }
}
