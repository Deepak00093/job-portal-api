package com.deepak.jobportalapi.dto;

import lombok.Getter;
import lombok.Setter;
import com.deepak.jobportalapi.entity.UserRole;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
}