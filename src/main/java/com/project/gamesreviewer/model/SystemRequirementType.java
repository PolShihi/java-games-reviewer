package com.project.gamesreviewer.model;

public record SystemRequirementType(
    int id,
    String name
) {
    public SystemRequirementType {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("System requirement type name cannot be empty");
        }
    }
}
