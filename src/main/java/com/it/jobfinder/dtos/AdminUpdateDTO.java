package com.it.jobfinder.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdminUpdateDTO {

    private UUID id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;
}
