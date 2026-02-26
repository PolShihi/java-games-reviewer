package com.project.gamesreviewer.model;

public record ProductionCompany(
    int id,
    String name,
    Integer foundedYear,
    String websiteUrl,
    String ceo,
    Integer companyTypeId,
    String companyTypeName
) {
    public ProductionCompany {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        if (foundedYear != null && (foundedYear < 1900 || foundedYear > 2100)) {
            throw new IllegalArgumentException("Founded year must be between 1900 and 2100");
        }
    }
    
    public ProductionCompany(int id, String name, Integer foundedYear, String websiteUrl, String ceo, Integer companyTypeId) {
        this(id, name, foundedYear, websiteUrl, ceo, companyTypeId, null);
    }
    
    public ProductionCompany withCompanyTypeName(String companyTypeName) {
        return new ProductionCompany(id, name, foundedYear, websiteUrl, ceo, companyTypeId, companyTypeName);
    }
}
