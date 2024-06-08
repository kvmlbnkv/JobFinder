package com.it.jobfinder.dtos;

import lombok.Data;

@Data
public class EmployerRegistrationDTO {

    private String username;

    private String password;

    private String email;

    private String name;

    private String description;
}
