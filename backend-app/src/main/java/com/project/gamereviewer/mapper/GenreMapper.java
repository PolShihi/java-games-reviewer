package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.request.GenreCreateRequest;
import com.project.gamereviewer.dto.response.GenreResponse;
import com.project.gamereviewer.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {
    
    GenreResponse toResponse(Genre genre);
    
    @Mapping(target = "id", ignore = true)
    Genre toEntity(GenreCreateRequest request);
}
