package com.project.gamereviewer.mapper;

import com.project.gamereviewer.dto.request.ReviewCreateRequest;
import com.project.gamereviewer.dto.response.ReviewResponse;
import com.project.gamereviewer.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {MediaOutletMapper.class}
)
public interface ReviewMapper {
    
    @Mapping(source = "game.id", target = "gameId")
    @Mapping(source = "game.title", target = "gameTitle")
    ReviewResponse toResponse(Review review);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "mediaOutlet", ignore = true)
    Review toEntity(ReviewCreateRequest request);
}
