package com.example.end.mapping;

import com.example.end.dto.ReviewDto;
import com.example.end.models.Review;
import org.springframework.stereotype.Service;

@Service
public class ReviewMapper {
    public ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .clientId(review.getClient().getId())
                .masterId(review.getMaster().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(String.valueOf(review.getCreatedAt()))
                .build();
    }
}




