package com.project.gamesreviewer.model;

public record CompanyType(
    int id,
    String name
) {
    public CompanyType {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company type name cannot be empty");
        }
    }
}
