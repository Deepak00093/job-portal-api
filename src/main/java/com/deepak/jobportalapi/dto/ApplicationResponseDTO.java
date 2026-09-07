package com.deepak.jobportalapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponseDTO {

    private Long id;

    private Long userId;
    private String userName;

    private Long jobId;
    private String jobTitle;

    private String status;

    private String resumePath;
    private LocalDateTime appliedAt;
}