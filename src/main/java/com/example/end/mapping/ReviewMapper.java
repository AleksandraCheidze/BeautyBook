package com.example.end.mapping;

import com.example.end.dto.ReviewDto;
import com.example.end.models.Review;
import com.example.end.models.User;
import org.springframework.stereotype.Service;

@Service
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }
        return ReviewDto.builder()
                .masterId(review.getMaster() != null ? review.getMaster().getId() : null)
                .clientId(review.getClient() != null ? review.getClient().getId() : null)
                .comment(review.getContent())
                .build();
    }

    public Review toEntity(ReviewDto reviewDto) {
        if (reviewDto == null) {
            return null;
        }

        User master = null;
        if (reviewDto.getMasterId() != null) {
            master = new User();
            master.setId(reviewDto.getMasterId());
        }

        User client = null;
        if (reviewDto.getClientId() != null) {
            client = new User();
            client.setId(reviewDto.getClientId());
        }

        return Review.builder()
                .master(master)
                .client(client)
                .content(reviewDto.getComment())
                .build();
    }
}
