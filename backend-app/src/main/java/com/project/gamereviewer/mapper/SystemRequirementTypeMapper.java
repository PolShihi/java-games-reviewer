package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.response.SystemRequirementTypeResponse;
import com.project.gamereviewer.entity.SystemRequirementType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SystemRequirementTypeMapper {
    
    SystemRequirementTypeResponse toResponse(SystemRequirementType type);
}
