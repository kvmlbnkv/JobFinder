package com.it.jobfinder.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdminRegistrationDTO {

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;
}
