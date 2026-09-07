package com.deepak.jobportalapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequestDTO {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    @NotBlank
    private String salary;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}
