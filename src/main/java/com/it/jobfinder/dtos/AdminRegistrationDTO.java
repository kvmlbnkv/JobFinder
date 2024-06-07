package com.it.jobfinder.dtos;

import lombok.Data;

@Data
public class AdminRegistrationDTO {

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;
}
