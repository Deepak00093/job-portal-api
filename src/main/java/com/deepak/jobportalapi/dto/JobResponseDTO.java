package com.deepak.jobportalapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String salary;
    private Long companyId;
    private String companyName;
    private LocalDateTime postedAt;
}