package com.project.gamereviewer.dto.response;

public record ProductionCompanyResponse(
    Integer id,
    String name,
    Integer foundedYear,
    String websiteUrl,
    String ceo,
    String companyTypeName
) {}
