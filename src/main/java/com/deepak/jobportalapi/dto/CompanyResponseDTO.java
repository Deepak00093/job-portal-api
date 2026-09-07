package com.deepak.jobportalapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String location;
    private String website;
}