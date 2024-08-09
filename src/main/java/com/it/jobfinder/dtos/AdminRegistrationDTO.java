package com.it.jobfinder.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRegistrationDTO {

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;
}
