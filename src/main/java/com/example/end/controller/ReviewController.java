package com.example.end.controller;

import com.example.end.controller.api.ReviewApi;
import com.example.end.dto.ReviewDto;
import com.example.end.service.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    @Override
    public List<ReviewDto> getReviewsByMaster(Long masterId) {
        return reviewService.getReviewsByMaster(masterId);
    }

    @Override
    public ReviewDto addReview(ReviewDto reviewDto) {
        return reviewService.addReview(reviewDto);
    }

    @Override
    public void deleteReview(Long reviewId) {
         reviewService.deleteReview(reviewId);
    }

    @Override
    public double getMasterRating(Long masterId) {
        return reviewService.getMasterRating(masterId);
    }
}