package com.example.moonshot.review;

import com.example.moonshot.exception.MoonshotException;
import com.example.moonshot.itinerary.Itinerary;
import com.example.moonshot.itinerary.ItineraryRepository;
import com.example.moonshot.review.dto.ReviewRequest;
import com.example.moonshot.review.dto.ReviewResponse;
import com.example.moonshot.user.User;
import com.example.moonshot.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItineraryRepository itineraryRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         ItineraryRepository itineraryRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.itineraryRepository = itineraryRepository;
    }

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAllWithRelations()
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    public Review getReviewById(UUID id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new MoonshotException("Review not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Review createReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new MoonshotException("User not found", HttpStatus.NOT_FOUND));

        Itinerary itinerary = itineraryRepository.findById(request.getItineraryId())
                .orElseThrow(() -> new MoonshotException("Itinerary not found", HttpStatus.NOT_FOUND));

        Review review = Review.builder()
                .user(user)
                .itinerary(itinerary)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewRepository.save(review);
    }

    public void deleteReview(UUID id) {
        reviewRepository.deleteById(id);
    }
}
