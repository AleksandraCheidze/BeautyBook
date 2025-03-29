package com.example.end.service.interfaces;

import com.example.end.dto.ReviewDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewService {


    ReviewDto addReview(ReviewDto reviewDto);

    double getMasterRating(Long masterId);

    void deleteReview(Long reviewId);

    List<ReviewDto> getReviewsByMaster(Long masterId);
}
