package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.request.SystemRequirementCreateRequest;
import com.project.gamereviewer.dto.response.SystemRequirementResponse;
import com.project.gamereviewer.entity.SystemRequirement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {SystemRequirementTypeMapper.class}
)
public interface SystemRequirementMapper {
    
    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "systemRequirementType", target = "type")
    SystemRequirementResponse toResponse(SystemRequirement systemRequirement);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "systemRequirementType", ignore = true)
    SystemRequirement toEntity(SystemRequirementCreateRequest request);
}
