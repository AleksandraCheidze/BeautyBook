package com.example.end.service;

import com.example.end.dto.ReviewDto;
import com.example.end.dto.UserDto;
import com.example.end.exceptions.ReviewNotFoundException;
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

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final UserMapper userMapper;


    public List<ReviewDto> getReviewsByMaster(Long masterId) {
        List<Review> reviews = reviewRepository.findByMasterId(masterId);
        return reviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
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
            review.setCreatedAt(LocalDateTime.parse(reviewDto.getCreatedAt()));

            Review savedReview = reviewRepository.save(review);
            return reviewMapper.toDto(savedReview);
        } else {
            throw new IllegalArgumentException("Client or master not found");
        }
        }

    @Override
    public double getMasterRating(Long masterId) {
        List<Review> reviews = reviewRepository.findByMasterId(masterId);

        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0);
    }


    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        reviewRepository.delete(review);
    }
}