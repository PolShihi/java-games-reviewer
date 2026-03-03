package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.request.MediaOutletCreateRequest;
import com.project.gamereviewer.dto.response.MediaOutletResponse;
import com.project.gamereviewer.entity.MediaOutlet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MediaOutletMapper {
    
    MediaOutletResponse toResponse(MediaOutlet mediaOutlet);
    
    @Mapping(target = "id", ignore = true)
    MediaOutlet toEntity(MediaOutletCreateRequest request);
}
