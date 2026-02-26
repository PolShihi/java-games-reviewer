package com.project.gamesreviewer.model;

public record Genre(
    int id,
    String name
) {
    public Genre {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Genre name cannot be empty");
        }
    }
}
