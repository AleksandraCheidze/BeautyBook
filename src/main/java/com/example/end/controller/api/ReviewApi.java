package com.example.end.controller.api;

import com.example.end.dto.StandardResponseDto;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.end.dto.ReviewDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@Tag(name = "Reviews", description = "API endpoints for review management")
@RequestMapping("/api/reviews")
public interface ReviewApi {

        @Operation(summary = "Get Reviews by Master (Public)", description = "Get all reviews for a specific master. Access: All users")
        @GetMapping("/master/{masterId}")
        List<ReviewDto> getReviewsByMaster(
                        @Parameter(description = "ID of the master to filter reviews", example = "1", required = true) @PathVariable("masterId") Long masterId);

        @Operation(summary = "Add Review (CLIENT)", description = "Add a new review for a master. Access: Authorized clients only")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Review was added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))),
                        @ApiResponse(responseCode = "401", description = "User is not authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @SecurityRequirement(name = "bearerAuth")
        ReviewDto addReview(@RequestBody @Valid ReviewDto reviewDto);

        @Operation(summary = "Delete Review (ADMIN)", description = "Delete a review from the system. Access: ADMIN only")
        @DeleteMapping("/{reviewId}")
        @SecurityRequirement(name = "bearerAuth")
        void deleteReview(
                        @Parameter(description = "ID of the review to be deleted", example = "1", required = true) @PathVariable("reviewId") Long reviewId);

        @Operation(summary = "Get Master Rating (Public)", description = "Get the average rating for a specific master. Access: All users")
        @GetMapping("/rating/{masterId}")
        double getMasterRating(
                        @Parameter(description = "ID of the master to get rating", example = "1", required = true) @PathVariable("masterId") Long masterId);
}