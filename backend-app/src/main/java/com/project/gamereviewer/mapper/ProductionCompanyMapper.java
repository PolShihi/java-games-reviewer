package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.request.ProductionCompanyCreateRequest;
import com.project.gamereviewer.dto.response.ProductionCompanyResponse;
import com.project.gamereviewer.entity.ProductionCompany;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductionCompanyMapper {
    
    @Mapping(source = "companyType.name", target = "companyTypeName")
    ProductionCompanyResponse toResponse(ProductionCompany company);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyType", ignore = true)
    ProductionCompany toEntity(ProductionCompanyCreateRequest request);
}
