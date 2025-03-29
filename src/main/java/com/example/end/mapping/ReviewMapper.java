package com.example.end.mapping;

import com.example.end.dto.ReviewDto;
import com.example.end.models.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "master.id", target = "masterId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "content", target = "comment")
    ReviewDto toDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "master.id", source = "masterId")
    @Mapping(target = "client.id", source = "clientId")
    @Mapping(target = "content", source = "comment")
    Review toEntity(ReviewDto reviewDto);
}
