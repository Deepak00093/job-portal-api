package com.deepak.jobportalapi.controller;

import com.deepak.jobportalapi.dto.ReviewRequestDTO;
import com.deepak.jobportalapi.dto.ReviewResponseDTO;
import com.deepak.jobportalapi.entity.Review;
import com.deepak.jobportalapi.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies/{companyId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setTitle(review.getTitle());
        dto.setDescription(review.getDescription());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setCompanyId(review.getCompany().getId());
        dto.setCompanyName(review.getCompany().getName());
        dto.setReviewerEmail(review.getUser().getEmail());
        return dto;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(
            @PathVariable Long companyId,
            @Valid @RequestBody ReviewRequestDTO dto,
            Authentication authentication) {
        Review review = reviewService.addReview(companyId, authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(review));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getCompanyReviews(@PathVariable Long companyId) {
        List<ReviewResponseDTO> reviews = reviewService.getReviewsByCompany(companyId).stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long companyId,
            @PathVariable Long reviewId,
            Authentication authentication) {
        reviewService.deleteReview(reviewId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}