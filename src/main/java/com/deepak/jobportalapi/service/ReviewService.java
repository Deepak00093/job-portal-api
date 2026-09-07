package com.deepak.jobportalapi.service;

import com.deepak.jobportalapi.dto.ReviewRequestDTO;
import com.deepak.jobportalapi.entity.Company;
import com.deepak.jobportalapi.entity.Review;
import com.deepak.jobportalapi.entity.User;
import com.deepak.jobportalapi.exception.DuplicateApplicationException;
import com.deepak.jobportalapi.exception.ResourceNotFoundException;
import com.deepak.jobportalapi.repository.CompanyRepository;
import com.deepak.jobportalapi.repository.ReviewRepository;
import com.deepak.jobportalapi.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    public Review addReview(Long companyId, String userEmail, ReviewRequestDTO dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (reviewRepository.existsByCompanyIdAndUserId(companyId, user.getId())) {
            throw new DuplicateApplicationException("You have already reviewed this company");
        }

        Review review = new Review();
        review.setTitle(dto.getTitle());
        review.setDescription(dto.getDescription());
        review.setRating(dto.getRating());
        review.setCompany(company);
        review.setUser(user);

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByCompany(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found with id: " + companyId);
        }
        return reviewRepository.findByCompanyId(companyId);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only delete your own review");
        }

        reviewRepository.delete(review);
    }
}