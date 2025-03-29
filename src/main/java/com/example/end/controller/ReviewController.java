package com.example.end.controller;

import com.example.end.controller.api.ReviewApi;
import com.example.end.dto.ReviewDto;
import com.example.end.service.interfaces.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "API endpoints for review management")
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;

    @Override
    @GetMapping("/master/{masterId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get Reviews by Master (Public)", description = "Get all reviews for a specific master. Access: All users")
    public List<ReviewDto> getReviewsByMaster(Long masterId) {
        return reviewService.getReviewsByMaster(masterId);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Add Review (CLIENT)", description = "Add a new review for a master. Access: Authorized clients only")
    @SecurityRequirement(name = "bearerAuth")
    public ReviewDto addReview(ReviewDto reviewDto) {
        return reviewService.addReview(reviewDto);
    }

    @Override
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Review (ADMIN)", description = "Delete a review from the system. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteReview(Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @Override
    @GetMapping("/rating/{masterId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get Master Rating (Public)", description = "Get the average rating for a specific master. Access: All users")
    public double getMasterRating(Long masterId) {
        return reviewService.getMasterRating(masterId);
    }
}