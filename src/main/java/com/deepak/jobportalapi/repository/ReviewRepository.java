package com.deepak.jobportalapi.repository;

import com.deepak.jobportalapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCompanyId(Long companyId);
    boolean existsByCompanyIdAndUserId(Long companyId, Long userId);
}