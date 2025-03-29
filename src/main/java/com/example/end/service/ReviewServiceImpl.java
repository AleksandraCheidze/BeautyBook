package com.example.end.service;

import com.example.end.dto.ReviewDto;
import com.example.end.infrastructure.exceptions.ReviewNotFoundException;
import com.example.end.mapping.ReviewMapper;
import com.example.end.mapping.UserMapper;
import com.example.end.models.Review;
import com.example.end.models.User;
import com.example.end.models.User.Role;
import com.example.end.models.BookingStatus;
import com.example.end.repository.ReviewRepository;
import com.example.end.repository.UserRepository;
import com.example.end.repository.BookingRepository;
import com.example.end.service.interfaces.ReviewService;
import com.example.end.service.interfaces.UserService;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
import com.example.end.infrastructure.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ReviewService interface.
 * Provides business logic for managing reviews for users, specifically for
 * masters.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    /**
     * Retrieves all reviews for a specific master.
     *
     * @param masterId the ID of the master for whom to retrieve reviews.
     * @return a list of ReviewDto objects representing the reviews for the
     *         specified master.
     */
    @Override
    @Cacheable(value = "masterReviews", key = "#masterId")
    public List<ReviewDto> getReviewsByMaster(Long masterId) {
        List<Review> reviews = reviewRepository.findAllByMasterId(masterId);
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new review for a master by a client.
     *
     * @param reviewDto the ReviewDto containing the review details.
     * @return the added ReviewDto after being saved in the database.
     * @throws IllegalArgumentException if the client or master is not found or if
     *                                  the rating is not between 1 and 5.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "masterReviews", key = "#reviewDto.masterId"),
            @CacheEvict(value = "masterRating", key = "#reviewDto.masterId")
    })
    public ReviewDto addReview(ReviewDto reviewDto) {
        if (reviewDto.getRating() < 1 || reviewDto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User master = userRepository.findByIdAndRole(reviewDto.getMasterId(), Role.MASTER)
                .orElseThrow(() -> new ResourceNotFoundException("Master", reviewDto.getMasterId()));

        Long currentUserId = getCurrentUserId();

        if (!bookingRepository.existsByClientIdAndMasterIdAndStatus(
                currentUserId,
                master.getId(),
                BookingStatus.COMPLETED)) {
            throw new IllegalArgumentException("You can only review masters after completing a booking with them");
        }

        Review review = Review.builder()
                .master(userRepository.getReferenceById(reviewDto.getMasterId()))
                .client(userRepository.getReferenceById(currentUserId))
                .rating(reviewDto.getRating())
                .content(reviewDto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    /**
     * Retrieves the average rating of a master based on reviews.
     *
     * @param masterId the ID of the master whose rating is to be calculated.
     * @return the average rating of the master. If there are no reviews, returns 0.
     */
    @Override
    @Cacheable(value = "masterRating", key = "#masterId")
    public double getMasterRating(Long masterId) {
        return reviewRepository.calculateAverageRatingByMasterId(masterId);
    }

    /**
     * Deletes a review by its ID.
     *
     * @param reviewId the ID of the review to delete.
     * @throws ReviewNotFoundException if the review with the given ID does not
     *                                 exist.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "masterReviews", allEntries = true),
            @CacheEvict(value = "masterRating", allEntries = true)
    })
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review with id " + reviewId + " not found"));
        reviewRepository.deleteById(reviewId);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return userService.findByEmail(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}
