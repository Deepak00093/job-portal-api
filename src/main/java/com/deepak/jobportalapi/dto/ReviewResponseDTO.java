package com.deepak.jobportalapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double rating;
    private Long companyId;
    private String companyName;
    private String reviewerEmail;
    private LocalDateTime createdAt;
}