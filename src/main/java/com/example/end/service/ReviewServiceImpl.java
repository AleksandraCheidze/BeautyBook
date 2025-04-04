package com.example.end.service;

import com.example.end.dto.ReviewDto;
import com.example.end.dto.UserDto;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
import com.example.end.mapping.ReviewMapper;
import com.example.end.mapping.UserMapper;
import com.example.end.models.Review;
import com.example.end.models.User;
import com.example.end.repository.ReviewRepository;
import com.example.end.service.interfaces.ReviewService;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ReviewService interface.
 * Provides business logic for managing reviews for users, specifically for masters.
 */
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Retrieves all reviews for a specific master.
     *
     * @param masterId the ID of the master for whom to retrieve reviews.
     * @return a list of ReviewDto objects representing the reviews for the specified master.
     */
    public List<ReviewDto> getReviewsByMaster(Long masterId) {
        List<Review> reviews = reviewRepository.findByMasterId(masterId);
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new review for a master by a client.
     *
     * @param reviewDto the ReviewDto containing the review details.
     * @return the added ReviewDto after being saved in the database.
     * @throws IllegalArgumentException if the client or master is not found.
     */
    @Override
    public ReviewDto addReview(ReviewDto reviewDto) {
        UserDto clientDto = userService.getClientById(reviewDto.getClientId());
        User clientEntity = userMapper.toEntity(clientDto);

        UserDto masterDto = userService.getMasterById(reviewDto.getMasterId());
        User masterEntity = userMapper.toEntity(masterDto);

        if (clientEntity != null && masterEntity != null) {
            Review review = new Review();
            review.setClient(clientEntity);
            review.setMaster(masterEntity);
            review.setContent(reviewDto.getContent());
            review.setRating(reviewDto.getRating());

            review.setCreatedAt(LocalDateTime.now());

            Review savedReview = reviewRepository.save(review);
            return reviewMapper.toDto(savedReview);
        } else {
            throw new IllegalArgumentException("Client or master not found");
        }
    }
    /**
     * Retrieves the average rating of a master based on reviews.
     *
     * @param masterId the ID of the master whose rating is to be calculated.
     * @return the average rating of the master. If there are no reviews, returns 0.
     */
    @Override
    public double getMasterRating(Long masterId) {
        List<Review> reviews = reviewRepository.findByMasterId(masterId);

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }

    /**
     * Deletes a review by its ID.
     *
     * @param reviewId the ID of the review to delete.
     * @throws  if the review with the given ID does not exist.
     */
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        reviewRepository.delete(review);
    }
}
