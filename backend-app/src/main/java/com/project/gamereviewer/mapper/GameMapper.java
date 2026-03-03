package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.request.GameCreateRequest;
import com.project.gamereviewer.dto.response.GameDetailResponse;
import com.project.gamereviewer.dto.response.GameListResponse;
import com.project.gamereviewer.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {ProductionCompanyMapper.class, GenreMapper.class, ReviewMapper.class, SystemRequirementMapper.class}
)
public interface GameMapper {
    
    @Mapping(source = "developer.name", target = "developerName")
    @Mapping(source = "publisher.name", target = "publisherName")
    @Mapping(source = "genres", target = "genreNames", qualifiedByName = "genresToNames")
    @Mapping(target = "averageRating", ignore = true)
    GameListResponse toListResponse(Game game);
    
    @Mapping(target = "averageRating", ignore = true)
    GameDetailResponse toDetailResponse(Game game);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "developer", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "systemRequirements", ignore = true)
    Game toEntity(GameCreateRequest request);
    
    @Named("genresToNames")
    default List<String> genresToNames(Set<com.project.gamereviewer.entity.Genre> genres) {
        if (genres == null) {
            return List.of();
        }
        return genres.stream()
            .map(com.project.gamereviewer.entity.Genre::getName)
            .collect(Collectors.toList());
    }
}
