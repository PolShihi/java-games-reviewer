package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.response.CompanyTypeResponse;
import com.project.gamereviewer.entity.CompanyType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompanyTypeMapper {
    
    CompanyTypeResponse toResponse(CompanyType companyType);
}
