package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.UserRole;
import lombok.Data;

@Data
public class RegistrationDTO {
    private String username;

    private String password;

    private String email;

    private UserRole role;
}
