package com.project.gamesreviewer.model;

public record MediaOutlet(
    int id,
    String name,
    String websiteUrl,
    Integer foundedYear
) {
    public MediaOutlet {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Media outlet name cannot be empty");
        }
        if (foundedYear != null && (foundedYear < 1900 || foundedYear > 2100)) {
            throw new IllegalArgumentException("Founded year must be between 1900 and 2100");
        }
    }
}
