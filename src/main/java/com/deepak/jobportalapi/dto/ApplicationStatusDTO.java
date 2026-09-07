package com.deepak.jobportalapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.deepak.jobportalapi.entity.ApplicationStatus;

@Getter
@Setter
public class ApplicationStatusDTO {

    @NotNull
    private ApplicationStatus status;


}